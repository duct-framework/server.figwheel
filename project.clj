(defproject duct/server.figwheel "0.1.3"
  :description "Integrant methods for running Figwheel"
  :url "https://github.com/duct-framework/server.figwheel"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/core.async "0.3.443"]
                 [com.cemerick/piggieback "0.2.1"]
                 [compojure "1.6.0"]
                 [figwheel-sidecar "0.5.8"]
                 [http-kit "2.2.0"]
                 [integrant "0.4.0"]
                 [integrant/repl "0.2.0"]
                 [ring/ring-core "1.6.1"]]
  :profiles
  {:provided {:dependencies [[org.clojure/clojurescript "1.9.562"]]}
   :dev {:source-paths   ["dev/src/clj"]
         :resource-paths ["dev/resources" "target/js"]
         :dependencies [[duct/server.http.jetty "0.1.0"]
                        [figwheel "0.5.8"]
                        [org.clojure/tools.nrepl "0.2.12"]]
         :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}})
