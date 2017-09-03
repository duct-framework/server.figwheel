(defproject duct/server.figwheel "0.2.0"
  :description "Integrant methods for running Figwheel"
  :url "https://github.com/duct-framework/server.figwheel"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/core.async "0.3.443"]
                 [com.cemerick/piggieback "0.2.2"]
                 [compojure "1.6.0"]
                 [duct/core "0.6.1"]
                 [figwheel-sidecar "0.5.13"]
                 [http-kit "2.2.0"]
                 [integrant "0.6.1"]
                 [integrant/repl "0.2.0"]
                 [ring/ring-core "1.6.2"]]
  :profiles
  {:provided {:dependencies [[org.clojure/clojurescript "1.9.908"]]}
   :dev {:source-paths   ["dev/src/clj"]
         :resource-paths ["dev/resources" "target/js"]
         :dependencies [[duct/server.http.jetty "0.2.0"]
                        [figwheel "0.5.13"]
                        [org.clojure/tools.nrepl "0.2.13"]]
         :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}})
