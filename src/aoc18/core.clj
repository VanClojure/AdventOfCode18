(ns aoc18.core
  (:require [clojure.string :as str]
            [clojure.pprint :as pprint]))

(def test-board1 [[:. :# :. :# :|]
                  [:. :. :. :# :|]
                  [:. :# :| :| :.]])

(defn neighbour-coordinates [[x y]] ;=> seq. of coordinate pairs (vectors of two). Including ones out of matrix (filter them out on the caller's side).
  (for [i (range (dec x) (+ x 2))
        j (range (dec y) (+ y 2))
        :when (or (not= i x)
                  (not= j y))
        ]
    [i j]
    ))
#_(assert (= (neighbour-coordinates [1 1])
           [[0 1] [0 2] [0 3] [1 1] [1 3] [2 1] [2 2] [2 3]]))

(defn how-many-neighbours [board [x y :as pair]]
  (let [coordinates (neighbour-coordinates pair)
        neighbours (map (partial get-in board) coordinates)]
    (println (str "Pair: " pair))
    (->> neighbours
        (filter (fn [c] (not (nil? c)))) ;TODO filter identity ;This handles neighb. coordinates out matrix (corner cases)
        #_(reduce (fn [counts neighbour]
                  (update counts neighbours inc))
                  {})
        (group-by identity)
        (map (fn [[k v]] {k (count v)}))
        (apply merge {:. 0 :| 0 :# 0}))))
(assert (= (how-many-neighbours [[:. :# :.]
                                 [:. :. :.]
                                 [:. :# :|]]
                                [1 1])
           {:. 5, :| 1, :# 2}))
#_{:. 1 :| 2 :# 0}

(defn parse-lines [text] ;=> matrix of keywords
  (let [lines (clojure.string/split text #"\n")
        mx-characters (map seq lines)]
    (mapv #(mapv (comp keyword str) %) mx-characters)
    ))
;(println (parse-lines ".|\n#."))
(assert (= (parse-lines ".|\n#.")
           [[:. :|][:# :.]]))

(defn evolve
  "Takes the current type and returns the next type"
  [current-type neighbours]
  (case current-type
    :. (if (>= (:| neighbours) 3) :| :.)
    :| (if (>= (:# neighbours) 3) :# :|)
    :# (if (and
            (>= (:# neighbours) 1)
            (>= (:| neighbours) 1))
         :#
         :.)))

(defn resource-value
  [game]
  (let [flatten-game (flatten game)
        n-trees (->> flatten-game
                     (filter (fn [x] (= x :|)))
                     (count))
        n-lumberyards (->> flatten-game
                           (filter (fn [x] (= x :#)))
                           (count))]
    (* n-trees n-lumberyards)))


(comment
  (= 2 (resource-value [[:#,:.],[:|,:|]])))

(defn cell
  [game row col]
  (get-in game [row col]))

(defn next-game-state
  [neighbour-provider game size]
  (println "next-game-state")
  (pprint/pprint game)
  (->> (for [row (range size)
             col (range size)
             :let [neighbours (neighbour-provider game [row col])
                   cell (cell game row col)]
             :let [_ (println "x y cell: " row col cell)]
             :while (some? cell)
             ]
         (do (println (str "Cell: " cell))
             (evolve cell neighbours)))
       (partition size)))

(defn iterate
  [game size minutes]
  (println (str "Size: " size))
  (loop [g game
         gs size ;TODO not needed - just use size
         m 0]
    (if (< m minutes)
      (recur (next-game-state how-many-neighbours g gs) gs (inc m))
      g)))

(defn -main
  [& args]
  (let [game (-> args first slurp parse-lines)
        size (count (first game))
        final-game (iterate game size 10)]
    (println "Initial game:")
    (pprint/pprint game)
    (println "After 10 minuts:")
    (pprint/pprint final-game)
    (println "Resource value:")
    (println (resource-value final-game))))
