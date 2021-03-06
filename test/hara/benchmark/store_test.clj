(ns hara.benchmark.store-test
  (:use hara.test)
  (:require [hara.benchmark.store :refer :all :as store]))

^{:refer hara.benchmark.store/create-accumulate-store :added "2.4"}
(fact "creates a store to put count accumulates"

  (def avgs (create-accumulate-store {}))
  
  (do (store/-add avgs 4)
      (store/-add avgs 5)
      (store/-add avgs [6]))

  (store/-count avgs)
  => 3

  (store/-average avgs)
  => [5.0])

^{:refer hara.benchmark.store/create-history-store :added "2.4"}
(fact "creates a store to put count history"

  (def hist (create-history-store {}))
  
  (do (store/-put hist [0 :hello])
      (store/-put hist [1 :world])
      (store/-put hist [2 :again])
      (store/-put hist [3 :again]))
  
  (store/-last hist 2)
  => [[2 :again] [3 :again]]

  (store/-from hist 2)
  => [[2 :again] [3 :again]]

  (store/-until hist 2)
  => [[0 :hello] [1 :world] [2 :again]]

  (store/-between hist 1 2)
  => [[1 :world] [2 :again]]

  (store/-all hist)
  => [[0 :hello] [1 :world] [2 :again] [3 :again]])

(comment
  (./import))
