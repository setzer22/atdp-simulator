(ns atdp-simulation.core
  (:require [clojure.java.io :as io]
            [atdp-simulation.strint :refer [<<]]
            [clojure.string :as str]
            [atdp-simulation.simulation :refer :all]
            [atdp-simulation.atdp-wrapper :as atdp]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(def cli-options
  [["-i" "--input INPUT" "Atdp input file (json)"
    :parse-fn #(io/file %)
    :validate [#(.exists %)]]
   ["-o" "--output OUTPUT" "Output CSV log location"
    :parse-fn #(io/file %)
    :validate [#(.exists (io/file (.getParent %)))]]
   ["-s" "--size LOGSIZE" "Size of the generated log (number of cases)"
    :parse-fn #(Integer/parseInt %)
    :validate [#(>= % 1)]]]
  )


(defn log->CSV [log]
  (str/join "\n"
            (for [case log]
              (str/join "," case))))

(defn -main
  [& args]
  (let [{{:keys [input, output, size]} :options
         errors :errors}
        (parse-opts args cli-options)]
    (assert (not errors) errors)
    (assert input "Input file must be provided")
    (assert output "Output file must be provided")
    (assert size "Log Size must be provided")
    (let [interp (-> input
                     slurp
                     atdp/json->atdp
                     atdp/interpretations
                     first)
          log (repeatedly size (fn generate-log []
                                 (map #(.getId %)
                                      (simulate-atdp-interpretation interp))))
          csv (log->CSV log)]
      (spit output csv)
      (println (<< "Log successfully generated at ~(.getName output)")))))
