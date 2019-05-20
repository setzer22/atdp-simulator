(defproject atdp-simulation "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [dk.brics.automaton/automaton "1.11-8"]
                 [edu.upc/atdplib-model "1.0-SNAPSHOT"]
                 [org.clojure/tools.cli "0.4.2"] ]
  :main ^:skip-aot atdp-simulation.core
  :plugins [[lein-expand-resource-paths "0.0.1"]]
  :resource-paths ["lib/*"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
