(ns atdp-simulation.scope-tree-simulation
  (:require [atdp-simulation.atdp-wrapper :as atdp]
            [atdp-simulation.random-merge :refer [random-merge]]
            [atdp-simulation.random-walk :as random-walk])
  (:import [edu.upc.atdp_model.scope ConflictingScope SequentialScope
            IteratingScope InclusiveScope InterleavingScope LeafScope]))

(defmulti simulate-scope (fn [scope & _] (type scope)))

(defmethod simulate-scope SequentialScope [scope, auts]
  (mapcat #(simulate-scope % auts) (atdp/scope-children scope)))

(defmethod simulate-scope ConflictingScope [scope, auts]
  (simulate-scope (rand-nth (atdp/scope-children scope)) auts))

(def iter-prob 0.5)

(defmethod simulate-scope IteratingScope [scope, auts]
  (loop [trace (simulate-scope (first (atdp/scope-children scope)) auts)]
    (if (< (rand) iter-prob)
      (recur (into trace (simulate-scope (first (atdp/scope-children scope)) auts)))
      trace)))

(def inclusive-prob 0.5)

(defmethod simulate-scope InclusiveScope [scope, auts]
  (let [children (atdp/scope-children scope)
        traces (reduce
                (fn [traces, child]
                  (if (< (rand) inclusive-prob)
                    (into traces (simulate-scope child auts))
                    traces))
                []
                children)]
    (apply random-merge traces)))

(defmethod simulate-scope InterleavingScope [scope, auts]
  (apply random-merge
         (map #(simulate-scope % auts) (atdp/scope-children scope))))

(defmethod simulate-scope LeafScope [scope, auts]
  (random-walk/random-trace (auts (.getId scope))
                            (count (seq scope))
                            (* (count (seq scope)) 10)))
