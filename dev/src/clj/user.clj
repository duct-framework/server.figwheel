(ns user
  (:require [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [duct.server.figwheel :as figwheel]
            [duct.server.http.jetty :as jetty]
            [integrant.repl :refer [system init halt go clear reset]]))

(defroutes app-routes
  (GET "/" [] (io/resource "public/index.html"))
  (route/resources "/")
  (route/not-found "<h1>Not Found</h1>"))

(def config
  {:duct.server.http/jetty
   {:handler app-routes
    :port    3000}

   :duct.server/figwheel
   {:css-dirs ["dev/resources"]
    :builds   [{:id :dev
                :source-paths ["dev/src/cljs"]
                :build-options {:output-to "target/js/public/main.js"
                                :output-dir "target/js/public"
                                :optimizations :none}}]}})

(integrant.repl/set-prep! (constantly config))
