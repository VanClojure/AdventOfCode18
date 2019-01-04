(ns aoc18.core-test
  (:require [clojure.test :as test :refer [deftest is testing]]
            [aoc18.core :as core]))

(def game-zero-example [[:. :# :. :# :. :. :. :| :# :.]
                        [:. :. :. :. :. :# :| :# :# :|]
                        [:. :| :. :. :| :. :. :. :# :.]
                        [:. :. :| :# :. :. :. :. :. :#]
                        [:# :. :# :| :| :| :# :| :# :|]
                        [:. :. :. :# :. :| :| :. :. :.]
                        [:. :| :. :. :. :. :| :. :. :.]
                        [:| :| :. :. :. :# :| :. :# :|]
                        [:| :. :| :| :| :| :. :. :| :.]
                        [:. :. :. :# :. :| :. :. :| :.]])

(def game-ten-example [[:. :| :| :# :# :. :. :. :. :.]
                       [:| :| :# :# :# :. :. :. :. :.]
                       [:| :| :# :# :. :. :. :. :. :.]
                       [:| :# :# :. :. :. :. :. :# :#]
                       [:| :# :# :. :. :. :. :. :# :#]
                       [:| :# :# :. :. :. :. :# :# :|]
                       [:| :| :# :# :. :# :# :# :# :|]
                       [:| :| :# :# :# :# :# :| :| :|]
                       [:| :| :| :| :# :| :| :| :| :|]
                       [:| :| :| :| :| :| :| :| :| :|]])

#_(deftest predicate-tests
  (is (= :open-ground (core/char->keyword ".")))
  (is (= :trees (core/char->keyword "|")))
  (is (= :lumberard (core/char->keyword "#"))))

(deftest neighbour-coordinates-tests
  (testing "neighbours of a cell that is not on an edge or in a corner of the board"
    (is (= (core/neighbour-coordinates [1 1] 3)
           '([0 0] [0 1] [0 2] [1 0] [1 2] [2 0] [2 1] [2 2]))))
  (testing "neighbours of a cell that is in the top-left corner"
    (is (= (core/neighbour-coordinates [0 0] 3)
           '([0 1] [1 0] [1 1]))))
  (testing "neighbours of a cell that is in the bottom-right corner"
    (is (= (core/neighbour-coordinates [2 2] 3)
           '([1 1] [1 2] [2 1])))))

(deftest how-many-neighbours-tests
  (testing "counting neighbours of a random cell in a small board."
    (is (= (core/how-many-neighbours [[:. :# :.]
                                      [:. :. :.]
                                      [:. :# :|]]
                                     [1 1]
                                     3)
           {:. 5 :| 1 :# 2}))))

(deftest cell-tests
  (let [game [[:. :|] [:. :.]]]
    (is (= :. (core/cell game 0 0)))
    (is (nil? (core/cell game 0 2))) ;; out of bound returns nil
    ))

(deftest next-state-tests
  (is (= [[:. :. :. :. :. :. :. :| :. :.]
          [:. :. :. :. :. :. :| :. :. :|]
          [:. :| :. :. :| :. :. :. :. :.]
          [:. :. :| :. :. :. :. :. :. :.]
          [:. :. :. :| :| :| :. :| :. :|]
          [:. :. :. :. :. :| :| :. :. :.]
          [:. :| :. :. :. :. :| :. :. :.]
          [:| :| :. :. :. :. :| :. :. :|]
          [:| :. :| :| :| :| :. :. :| :.]
          [:. :. :. :. :. :| :. :. :| :.]]
         (let [my-neighbours-fn (constantly {:. 4 :| 0 :# 1})]
           (core/next-game-state my-neighbours-fn 10 game-zero-example)))))

(deftest resource-value-tests
  (testing "the resource value of a board is the number of squares of lumberyards multiplied by the number of squares of forest."
    (is (= (core/resource-value game-ten-example)
           1147))))

(deftest make-game-states-tests
  (testing "Running the simulation for a certain number of iterations (whole minutes in this case) should produce the expected state for each iteration."
    (let [game-states (core/make-game-states game-zero-example)]
      (is (= (nth game-states 0)
             game-zero-example))
      (is (= (nth game-states 10)
             game-ten-example)))))
