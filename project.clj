(defproject duct/server.figwheel "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.cemerick/piggieback "0.2.1"]
                 [figwheel-sidecar "0.5.8"]
                 [http-kit "2.2.0"]
                 [integrant "0.2.3"]]
  :profiles
  {:provided {:dependencies [[org.clojure/clojurescript "1.9.494"]]}
   :dev {:source-paths   ["dev/src/clj"]
         :resource-paths ["dev/resources" "target/js"]
         :dependencies [[integrant/repl "0.1.0"]
                        [duct/server.http.jetty "0.1.0-SNAPSHOT"]
                        [compojure "1.5.1"]
                        [figwheel "0.5.8"]
                        [org.clojure/tools.nrepl "0.2.12"]]
         :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}})
