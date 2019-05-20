(ns atdp-simulation.simulation
  (:import [edu.upc.atdp_model.constraint TemporalConstraint TemporalConstraintType]
           [edu.upc.atdp_model.fragment FragmentType Fragment]
           [edu.upc.atdp_model.scope BranchingScope LeafScope ScopeType]
           [edu.upc.atdp_model AtdpParser])
  (:require [atdp-simulation.atdp-wrapper :as atdp]
            [atdp-simulation.automata :as aut]
            [atdp-simulation.atdp-regexp :as rxp]
            [atdp-simulation.strint :refer [<<]]
            [atdp-simulation.scope-tree-simulation :refer [simulate-scope]]))

(defn make-alphabet
  [num-activities]
  (map #(char (+ 1000 (byte \A) %)) (range num-activities)))

(defmacro case-enum
  "Like `case`, but explicitly dispatch on Java enum ordinals."
  [e & clauses]
  (letfn [(enum-ordinal [e] `(let [^Enum e# ~e] (.ordinal e#)))]
    `(case ~(enum-ordinal e)
       ~@(concat
          (mapcat (fn [[test result]]
                    [(eval (enum-ordinal test)) result])
                  (partition 2 clauses))
          (when (odd? (count clauses))
            (list (last clauses)))))))

(defn get-constraint-automata [activity->char, constraint]
  (let [acts (map activity->char (.getActivities constraint))
        aut-fn (case-enum (.getType constraint)
                          TemporalConstraintType/AlternateResponse rxp/alternate-response
                          TemporalConstraintType/NonCoOccurrence rxp/no-co-occurs
                          TemporalConstraintType/Precedence rxp/precedence
                          TemporalConstraintType/Response rxp/response
                          TemporalConstraintType/Terminating rxp/terminating
                          TemporalConstraintType/Mandatory rxp/mandatory
                          TemporalConstraintType/NonRepeating rxp/non-repeating)]
    (apply aut-fn acts)))

(defn leaf-scope-automata [interp, activity->char, leaf]
  (let [activities (seq leaf)
        constraints (atdp/leaf-scope-constraints interp leaf)]
    [(.getId leaf)
     (apply aut/intersection
            (concat
             (map (partial get-constraint-automata activity->char)
                  constraints)
             [(apply rxp/alphabet (map activity->char activities))]))]))

(defn simulate-atdp-interpretation [interp]
  (let [leaf-scopes (atdp/leaf-scopes interp)
        activities (atdp/fragments-by-type interp FragmentType/Activity)
        alph (make-alphabet (count activities))
        activity->char (into {} (map vector activities alph))
        char->activity (into {} (map vector alph activities))
        automatas-by-id (into {} (map (partial leaf-scope-automata interp activity->char) leaf-scopes))
        root (atdp/scope-tree-root interp)
        result (mapv char->activity (simulate-scope root automatas-by-id))]
    result))
