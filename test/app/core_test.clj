(ns app.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [app.core :as sut]
            [clojure.java.io :as io]))

(def small-example
  (line-seq (io/reader "small-example.csv")))

(def middle-example
  (line-seq (io/reader "middle-example.csv")))

(def bigger-example
  (line-seq (io/reader "bigger-example.csv")))

(deftest calculate-rating-test
  (testing "basic calculations"
    (is (= '(["6156dca47fe5761a20b92b1d" 2] ["6156dcb90ba5173d292c9afe" 0])
           (sut/calculate small-example "6156dc9dc1f742f8e11aa14d"))))

  (testing "mid size example"
    (is (= '(["6156dcb90ba5173d292c9afe" 3] ["6156dca47fe5761a20b92b1d" 2])
           (sut/calculate middle-example "6156dc9dc1f742f8e11aa14d")))

    (is (= '(["6156dcb90ba5173d292c9afe" 2])
           (sut/calculate middle-example "6156dc9dc1f742f8e11aa17c"))))

  (testing "bigger example"
    (is (= '(["6156dfe7b8fb23f3008ce103" 9]
             ["6156e01d1618d92e76e0b62e" 7]
             ["6156e00820cb447d40fdfb3e" 0]
             ["6156dffb8ecb41af3545a946" 0])
           (sut/calculate bigger-example "6156e074d36e0ad56f9f0564"))))

  (testing "csv rendering"
    (is (= "6156dca47fe5761a20b92b1d;2\n6156dcb90ba5173d292c9afe;0"
         (sut/->csv-output (sut/calculate small-example "6156dc9dc1f742f8e11aa14d")))))

  (testing "non existed task"
    (is (empty? (sut/calculate small-example "unknown")))))
