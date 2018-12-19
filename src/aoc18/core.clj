(ns aoc18.core
  (:require [clojure.string :as str]
            [clojure.pprint :as pprint]))

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

(declare who-are-my-neighbours)

(defn cell
  [game row col]
  (get-in game [row col]))

(defn who-are-my-neighbours
  [game row col]
  {:. 4
   :| 0
   :# 1})

(defn next-game-state
  [neighbour-provider game size]
  (->> (for [row (range size)
             col (range size)
             :let [neighbours (neighbour-provider game row col)
                   cell (cell game row col)]
             :while (some? cell)]
         (evolve cell neighbours))
       (partition size)))

(defn iterate
  [game size minutes]
  (loop [g game
         gs size
         m 0]
    (if (< m minutes)
      (recur (next-game-state who-are-my-neighbours g gs) gs (inc m))
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
