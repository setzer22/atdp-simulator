(ns atdp-simulation.random-merge)

(defn rand-subset
  "Returns a u.a.r k-subset of {1,..,n}"
  [n, k]
  (cond
    (= k 0) #{}
    :else (if (< (rand) (/ k n))
            (conj (rand-subset (dec n) (dec k)) (dec n))
            (rand-subset (dec n) k))))

(defn random-sorted-list
  "Returns a u.a.r list with values in range [1..`max-val`] and given `size`"
  [max-val, size]
  (map-indexed
   (fn [index, val]
     (- val index))
   (sort
    (rand-subset (+ max-val size -1) size))))

(defn insert-at-v
  "Inserts `element` at position `pos` of vector `v`"
  [v, pos, element]
  (vec (concat (subvec v, 0, pos)
               [element]
               (subvec v, pos))))

(defn insert-all-at-v
  "Inserts all `elements` at position `pos` of vector `v`"
  [v, pos, elements]
  (reduce
   (fn [v element] (insert-at-v v pos element))
   v
   elements))

(defn into-bins
  "Classifies `vals` in `nbins` bins"
  [vals, nbins]
  (reduce
   (fn [bins, val]
     (let [val-bin (int (/ val (/ 1 nbins)))]
       (update bins val-bin conj val)))
   (vec (repeat nbins []))
   vals))

(defn random-merge
  "Randomly interleaves two or more lists in such a way that
  any possible interleaving is equally likely"
  ([A, B & rest]
   (apply random-merge (random-merge A B) rest))
  ([A, B]
   (let [positions (vec (random-sorted-list (count B) (count A)))]
     (reduce
      (fn [merged, i]
        (insert-at-v merged, (+ i (positions i)), (A i)))
      B
      (range (count A))))))


(comment

  (frequencies (repeatedly 10000 #(random-merge [1 2 3] [:a :b :c])))

  )
