(ns duct.server.figwheel
  "Integrant methods for running a Figwheel server."
  (:require [cider.piggieback :as piggieback]
            [cljs.repl :as repl]
            [cljs.stacktrace :as stacktrace]
            [clojure.java.io :as io]
            [compojure.core :as compojure :refer [GET]]
            [compojure.route :as route]
            [figwheel-sidecar.build-utils :as fig-build]
            [figwheel-sidecar.components.css-watcher :as fig-css]
            [figwheel-sidecar.components.cljs-autobuild :as fig-auto]
            [figwheel-sidecar.components.figwheel-server :as fig-server]
            [figwheel-sidecar.config :as fig-config]
            [figwheel-sidecar.repl :as fig-repl]
            [figwheel-sidecar.utils :as fig-util]
            [integrant.core :as ig]
            [org.httpkit.server :as httpkit]
            [ring.middleware.cors :as cors]))

(def ^:const default-source-files-pattern "\\.clj[sc]$")
(def ^:const default-css-files-pattern "\\.css$")

(defrecord FigwheelBuild [])

(defrecord FigwheelServer []
  fig-server/ChannelServer
  (-send-message [server channel-id msg-data callback]
    (let [message (fig-server/prep-message server channel-id msg-data callback)]
      (swap! (:file-change-atom server) fig-server/append-msg message)))
  (-connection-data [server]
    (-> server :connection-count deref)))

(defmethod print-method FigwheelBuild [_ ^java.io.Writer writer]
  (.write writer "#<FigwheelBuild>"))

(defmethod print-method FigwheelServer [_ ^java.io.Writer writer]
  (.write writer "#<FigwheelServer>"))

(defn- figwheel-server [state]
  (-> (compojure/routes
       (GET "/figwheel-ws/:desired-build-id" [] (fig-server/reload-handler state))
       (GET "/figwheel-ws" [] (fig-server/reload-handler state))
       (route/not-found "<h1>Page not found</h1>"))
      (cors/wrap-cors
       :access-control-allow-origin #".*"
       :access-control-allow-methods [:head :options :get :put :post :delete :patch])
      (httpkit/run-server
       {:port      (:server-port state)
        :server-ip (:server-ip state "0.0.0.0")
        :worker-name-prefix "figwh-httpkit-"})))

(defn- start-figwheel-server [opts]
  (let [state  (fig-server/create-initial-state opts)
        server (figwheel-server state)]
    (map->FigwheelServer (assoc state :http-server server))))

(defn- find-files [{:keys [paths files-pattern]}]
  (let [files (mapcat (comp file-seq io/file) paths)]
    (if files-pattern
      (filter #(re-find (re-pattern files-pattern) (.getPath %)) files)
      files)))

(defn- watch-paths [paths]
  (let [time (volatile! 0)]
    (fn []
      (locking time
        (let [now  (System/currentTimeMillis)
              then @time]
          (vreset! time now)
          (filter #(> (.lastModified %) then) (find-files paths)))))))

(defn- prep-build [{:keys [compiler-env source-paths source-files-pattern] :as build}]
  (-> build
      (dissoc :source-files-pattern)
      (cond-> (not (fig-config/prepped? build)) fig-config/prep-build)
      (cond-> (not compiler-env)                fig-build/add-compiler-env)
      (assoc :watcher (watch-paths {:paths source-paths
                                    :files-pattern (or source-files-pattern
                                                       default-source-files-pattern)}))
      (map->FigwheelBuild)))

(defn- clean-build [build]
  (fig-util/clean-cljs-build* build))

(defn- start-build [build server files]
  (fig-auto/figwheel-build
   {:build-config    (dissoc build :watcher)
    :figwheel-server server
    :changed-files   files}))

(defn rebuild-cljs
  "Tell a Figwheel server to rebuild all ClojureScript source files, and to
  send the new code to the connected clients."
  [{:keys [server prepped]}]
  (doseq [{:keys [source-paths source-files-pattern] :as build} prepped]
    (let [files (map str (find-files {:path source-paths, :files-pattern source-files-pattern}))]
      (fig-util/clean-cljs-build* build)
      (start-build build server files))))

(defn build-cljs
  "Tell a Figwheel server to build any modified ClojureScript source files, and
  to send the new code to the connected clients."
  [{:keys [server prepped]}]
  (doseq [{:keys [watcher] :as build} prepped]
    (when-let [files (seq (map str (watcher)))]
      (start-build build server files))))

(defn refresh-css
  "Tell a Figwheel server to update the CSS of connected clients."
  [{:keys [server css-watch]}]
  (fig-css/handle-css-notification {:figwheel-server server} (css-watch)) nil)

(defmethod ig/init-key :duct.server/figwheel [_ {:keys [builds css-dirs css-files-pattern] :as opts}]
  (doto {:server    (start-figwheel-server opts)
         :prepped   (mapv prep-build builds)
         :css-watch (if css-dirs
                      (watch-paths {:paths css-dirs
                                    :files-pattern (or css-files-pattern
                                                       default-css-files-pattern)})
                      (fn []))}
    (build-cljs)
    (refresh-css)))

(defmethod ig/halt-key! :duct.server/figwheel [_ {:keys [server]}]
  (fig-server/stop-server server))

(defmethod ig/suspend-key! :duct.server/figwheel [_ impl])

(defmethod ig/resume-key :duct.server/figwheel [key opts old-opts old-impl]
  (if (and (:server old-impl) (= (:builds opts) (:builds old-opts)))
    (doto old-impl
      (build-cljs)
      (refresh-css))
    (do (ig/halt-key! key old-impl)
        (ig/init-key  key opts))))

(defn- start-piggieback-repl [server build]
  {:pre [(some? build)]}
  (let [compiler (or (:compiler build) (:build-options build))]
    (piggieback/cljs-repl
     (fig-repl/cljs-repl-env build server)
     :special-fns  (:special-fns compiler repl/default-special-fns)
     :output-dir   (:output-dir compiler "out")
     :compiler-env (:compiler-env build)
     :analyze-path (:source-paths build))))

(defn cljs-repl
  "Open a ClojureScript REPL through the Figwheel server."
  ([{:keys [server prepped]}]
   (start-piggieback-repl server (first prepped)))
  ([{:keys [server prepped]} build-id]
   (start-piggieback-repl server (-> (group-by :id prepped)
                                     (get build-id)
                                     first))))
