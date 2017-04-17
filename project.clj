(defproject duct/server.figwheel "0.1.0-SNAPSHOT"
  :description "Integrant methods for running Figwheel"
  :url "https://github.com/duct-framework/server.figwheel"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.cemerick/piggieback "0.2.1"]
                 [ring/ring-core "1.6.0-RC2"]
                 [figwheel-sidecar "0.5.8"]
                 [http-kit "2.2.0"]
                 [integrant "0.3.3"]
                 [integrant/repl "0.2.0"]]
  :profiles
  {:provided {:dependencies [[org.clojure/clojurescript "1.9.521"]]}
   :dev {:source-paths   ["dev/src/clj"]
         :resource-paths ["dev/resources" "target/js"]
         :dependencies [[duct/server.http.jetty "0.1.0"]
                        [compojure "1.6.0-beta3"]
                        [figwheel "0.5.8"]
                        [org.clojure/tools.nrepl "0.2.12"]]
         :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}})
