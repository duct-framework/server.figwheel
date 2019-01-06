(defproject duct/server.figwheel "0.3.0-beta1"
  :description "Integrant methods for running Figwheel"
  :url "https://github.com/duct-framework/server.figwheel"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/core.async "0.4.490"]
                 [com.cemerick/piggieback "0.2.2"]
                 [compojure "1.6.1"]
                 [duct/core "0.7.0"]
                 [figwheel-sidecar "0.5.18"]
                 [http-kit "2.3.0"]
                 [integrant "0.7.0"]
                 [integrant/repl "0.3.1"]
                 [ring/ring-core "1.7.1"]]
  :profiles
  {:provided {:dependencies [[org.clojure/clojurescript "1.10.439"]]}
   :dev {:source-paths   ["dev/src/clj"]
         :resource-paths ["dev/resources" "target/js"]
         :dependencies [[duct/server.http.jetty "0.2.0"]
                        [figwheel "0.5.18"]
                        [org.clojure/tools.nrepl "0.2.13"]]
         :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}})
