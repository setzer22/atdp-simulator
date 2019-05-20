(ns atdp-simulation.random-walk)

(defn initial [dfa]
  (.getInitialState dfa))

(defn branches [state]
  (map #(.getDest %) (.getTransitions state)))

(defn char-range [start end]
  (map char (range (int start) (inc (int end)))))

(defn candidates [state]
  (->> state
       (.getTransitions)
       (map #(-> [(.getMin %) (.getMax %)]))
       (map #(apply char-range %))
       (flatten)))

(defn drop-while-last [pred, coll]
  (reverse (drop-while pred (reverse coll))))

(defn random-walk [state, length]
  (let [walk (loop [path [[state, nil]], state state, length length]
               (if (or (zero? length) (not (seq (candidates state))))
                 path
                 #_:otherwise
                 (let [c (-> state candidates rand-nth)
                       next (.step state c)]
                   (recur (conj path [next, c])
                          next
                          (dec length)))))]

    (->> walk
         (drop-while-last #(not (.isAccept (first %))))
         (map second)
         (rest))))

(defn rand-int-between
  "Generates a random integer between n and m"
  [n m]
  (+ n (rand-int (inc (- m n)))))

(defn random-trace [model, min-length, max-length]
  (let [length (rand-int-between min-length max-length)]
    (random-walk (initial model) length)))

(defn generate-log [model, size, min-trace-length, max-trace-length]
  (->> (repeatedly size #(random-trace model, min-trace-length, max-trace-length))
       (filter seq)))
