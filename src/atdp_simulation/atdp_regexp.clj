(ns atdp-simulation.atdp-regexp
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [atdp-simulation.automata :refer :all]
            [atdp-simulation.strint :refer [<<]]))

(defn alts
  "Shorthand notation to create an regexp alternation
   of a values in a list when interpolating strings"
  [args]
  (str "(" (str/join "|" args) ")"))

(defautomaton response [a & bs]
  (<< "[^~{a}]*(~{a}.*~(alts bs))*[^~{a}]*"))

(defautomaton precedence [& args]
  (let [as (butlast args), b (last args)]
    (<< "[^~{b}]*(~(alts as).*~{b})*[^~{b}]*")))

(defautomaton succession2 [a b]
  (<< "[^~{a}~{b}]*(~{a}.*~{b})*[^~{a}~{b}]*"))

(defn succession [& args]
  (let [clauses (map #(apply succession2 %)
                     (partition 2 args))]
    (apply intersection clauses)))

(defautomaton no-co-occurs [& args]
  (let [argset (set args)
        clauses (for [a args]
                  (let [vars (->> (set/difference argset #{a})
                                  (map (fn [x] (<< "$~{x}")))
                                  (apply str))]
                    (<< "[^~{vars}]*")))]
    (<< "~(alts clauses)")))

(defn alternatives [& [condition & branches]]
  (apply intersection
   (apply response condition branches)
   (apply no-co-occurs branches)
   (for [b branches]
     (precedence condition b))))

(defautomaton alternate-response [a b]
  (<< "[^~{a}]*(~{a}[^~{a}]*~{b}[^~{a}]*)*[^~{a}]*"))

(defautomaton terminating [a]
  (<< "([^~{a}]*|[^~{a}]*~{a})"))

(defautomaton mandatory [& args]
  (<< ".*~(alts args).*"))

(defautomaton non-repeating [a]
  (<< "([^~{a}]*|[^~{a}]*~{a}[^~{a}]*)"))

(defn between [a mid b]
  (intersection
   (succession a mid)
   (succession mid b)))

(defautomaton alphabet [& alph]
  (str "(" (str/join "|" alph) ")*"))
