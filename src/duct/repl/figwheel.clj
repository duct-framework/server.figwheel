(ns duct.repl.figwheel
  (:require [integrant.repl :as repl]
            [duct.server.figwheel :as figwheel]))

(defn cljs-repl []
  (figwheel/cljs-repl (:duct.server/figwheel repl/system)))
