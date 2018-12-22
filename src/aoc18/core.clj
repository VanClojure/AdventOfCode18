(ns aoc18.core
  (:require [clojure.string :as str]
            [clojure.pprint :as pprint]))

(defn neighbour-coordinates [[x y] size] ;=> seq. of coordinate pairs (vectors of two).
  "Provides the sequence of coordinates that are adjacent to the one given as the first argument. Uses size as an upper bound for the values in each axis."
  (for [i (range (max 0 (dec x)) (min size (+ x 2)))
        j (range (max 0 (dec y)) (min size (+ y 2)))
        :when (or (not= i x)
                  (not= j y))]
    [i j]))

(defn how-many-neighbours [board [x y :as pair] size]
  "Provides the count of each kind of cell--lumberyard, forest, and open ground--in the board of the given size, adjacent to the given coordinate."
  (let [coordinates (neighbour-coordinates pair size)
        neighbours (map (partial get-in board) coordinates)]
    (->> neighbours
         (group-by identity)
         (map (fn [[k v]] {k (count v)}))
         (apply merge {:. 0 :| 0 :# 0}))))

(defn parse-lines [text] ;=> matrix of keywords
  (let [lines (clojure.string/split text #"\n")
        mx-characters (map seq lines)]
    (mapv #(mapv (comp keyword str) %) mx-characters)))

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
  [neighbour-provider size game]
  (->> (for [row (range size)
             col (range size)
             :let [neighbours (neighbour-provider game [row col] size)
                   cell (cell game row col)]]
         (do (assert cell "cannot retrieve cell value, there might be a bug somewhere")
             (evolve cell neighbours)))
       (partition size)
       (map vec)
       vec))

(defn game->str
  [game]
  (str/join \newline (mapv #(str/join (mapv name %)) game)))

(defn make-game-states [input-game]
  "Provides the lazy sequence of game states, starting with and including the given one."
  (let [size (count (first input-game))
        next-game-state (partial next-game-state how-many-neighbours size)]
    (iterate next-game-state input-game)))

(defn -main
  [& args]
  (let [input-game (-> args first slurp parse-lines)
        games (make-game-states input-game)
        games-10 (drop 1 (take 11 games))
        final-game (nth games-10 9)]
    (println "Initial state:")
    (println (game->str input-game))
    (println)

    (doseq [[i g] (map-indexed vector games-10)]
      (println "After" (inc i) (str "minute" (when (> i 1) "s") ":"))
      (println (game->str g))
      (println))

    (println "Resource value:")
    (println (resource-value final-game))))
