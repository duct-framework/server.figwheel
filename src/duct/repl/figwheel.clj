(ns duct.repl.figwheel
  (:require [integrant.repl.state :as state]
            [duct.server.figwheel :as figwheel]))

(defn cljs-repl
  ([]
   (figwheel/cljs-repl (:duct.server/figwheel state/system)))
  ([build-id]
   (figwheel/cljs-repl (:duct.server/figwheel state/system) build-id)))
