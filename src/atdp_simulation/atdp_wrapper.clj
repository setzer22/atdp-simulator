(ns atdp-simulation.atdp-wrapper
  (:require [clojure.set :as set])
  (:import [edu.upc.atdp_model.constraint TemporalConstraint TemporalConstraintType]
           [edu.upc.atdp_model.fragment FragmentType Fragment]
           [edu.upc.atdp_model.scope BranchingScope LeafScope ScopeType]
           [edu.upc.atdp_model AtdpParser Atdp Interpretation]))

(defn json->atdp [json]
  (AtdpParser/parseFromJsonString json))

(defn interpretations [^Atdp atdp]
  (.getInterpretations atdp))

(defn fragments [^Interpretation interp]
  (.getFragments interp))

(defn temporal-constraints [^Interpretation interp]
  (.getTemporalConstraints interp))

(defn leaf-scope-constraints [^Interpretation interp, ^LeafScope scope]
  (let [activities (set (seq scope))
        constraints (temporal-constraints interp)]
    (filter
     (fn [constraint]
       (not (empty? (set/intersection
                     (set (.getActivities constraint))
                     activities))))
     constraints)))

(defn fragments-by-type [^Interpretation interp, ^FragmentType t]
  (.getFragmentsWithType interp, t))

(defn scope-tree-root [^Interpretation interp]
  (.getScopeTreeRoot interp))

(defn scopes-by-type [^Interpretation interp, ^ScopeType st]
  (.getScopesWithType interp, st))

(defn leaf-scopes [^Interpretation interp]
  (scopes-by-type interp edu.upc.atdp_model.scope.ScopeType/Leaf))

(defn scope-children [^BranchingScope scope]
  (.getChildren scope))
