(ns aoc18.core
  (:require [clojure.string :as str]))

(defn char->keyword
  [c]
  (cond
    (= 0 (str/index-of c \.)) :open-ground
    (= 0 (str/index-of c \|)) :trees
    (= 0 (str/index-of c \#)) :lumberard))

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

(defn resource-value [board]
  (let [flatten-board (flatten board)
        n-trees (->> flatten-board
                     (filter (fn [x] (= x :|)))
                     (count))
        n-lumberyards (->> flatten-board
                           (filter (fn [x] (= x :#)))
                           (count))]
    (* n-trees n-lumberyards)))


(comment
  (= 2 (resource-value [[:#,:.],[:|,:|]])))

(declare who-are-my-neighbours)

(defn cell
  [game row col]
  (get-in game [row col]))

(defn who-are-my-neighbours
  [game row col]
  {:. 4
   :| 0
   :# 1})

(defn next-state
  [neighbour-provider game size]
  (->> (for [row (range size)
             col (range size)
             :let [neighbours (neighbour-provider game row col)
                   cell (cell game row col)]
             :while (some? cell)]
         (evolve cell neighbours))
       (partition size)))

(def test-board1 [[:. :# :. :# :|]
                  [:. :. :. :# :|]
                  [:. :# :| :| :.]])

(defn neighbour-coordinates [[x y]]
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
    (->> neighbours
        (filter (fn [c] (not (nil? c))))
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
