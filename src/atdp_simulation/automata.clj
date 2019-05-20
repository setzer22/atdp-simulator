(ns atdp-simulation.automata
  (:require [clojure.string :as str])
  (:import [dk.brics.automaton Automaton RegExp SpecialOperations]))

(defn regexp->automaton [regex-str]
  (.toAutomaton (RegExp. regex-str)))

(defn intersection
  [& [a & as]]
  (reduce
   (fn [int, a] (.intersection int a))
   a
   as))

(defn union
  [& [a & as]]
  (reduce
   (fn [int, a] (.union int a))
   a
   as))

(defmacro defautomaton [name, args & regexp]
  `(defn ~name ~args
     (let [a# (regexp->automaton
              (do ~@regexp))]
       (~'.minimize a#)
       a#)))
