(ns aoc18.core
  (:require [clojure.string :as str]))

(defn char->keyword
  [c]
  (cond
    (= 0 (str/index-of c \.)) :open-ground
    (= 0 (str/index-of c \|)) :trees
    (= 0 (str/index-of c \#)) :lumberard))

(defn parse-lines [text]
  (let [lines (clojure.string/split text #"\n")]
    (mapv #(seq %) lines)
    ))
(assert (= (parse-lines ".|\n#.")
           [[\.\|][\#\.]]))


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
