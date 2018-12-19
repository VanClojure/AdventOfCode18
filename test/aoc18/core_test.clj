(ns aoc18.core-test
  (:require [clojure.test :as test]
            [aoc18.core :as core]))

(deftest predicate-tests
  (is (= :open-ground (core/char->keyword ".")))
  (is (= :trees (core/char->keyword "|")))
  (is (= :lumberard (core/char->keyword "#"))))

(deftest cell-tests
  (let [game [[:. :|] [:. :.]]]
    (is (= :. (core/cell game 0 0)))
    (is (nil? (core/cell game 0 2))) ;; out of bound returns nil
    ))

(deftest next-state-tests
  (let [game [[:. :# :. :# :. :. :. :| :# :.]
              [:. :. :. :. :. :# :| :# :# :|]
              [:. :| :. :. :| :. :. :. :# :.]
              [:. :. :| :# :. :. :. :. :. :#]
              [:# :. :# :| :| :| :# :| :# :|]
              [:. :. :. :# :. :| :| :. :. :.]
              [:. :| :. :. :. :. :| :. :. :.]
              [:| :| :. :. :. :# :| :. :# :|]
              [:| :. :| :| :| :| :. :. :| :.]
              [:. :. :. :# :. :| :. :. :| :.]]
        my-neighbours-fn (constantly {:. 4
                                      :| 0
                                      :# 1})]
    (core/next-state my-neighbours-fn game 10)))
