(ns aoc18.core-test
  (:require [clojure.test :as test]
            [aoc18.core :as core]))

(deftest predicate-tests
  (is (= :open-ground (core/char->keyword ".")))
  (is (= :trees (core/char->keyword "|")))
  (is (= :lumberard (core/char->keyword "#"))))
