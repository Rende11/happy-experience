(ns app.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))


(defn heading->kw [heading]
  (keyword (str/lower-case heading)))

(defn parse-csv-content [lines]
  (let [[header* & items] (map #(str/split % #"[,|;]") lines)
        header (map heading->kw header*)]
    (map #(zipmap header %) items)))

(def scoring
  {"1" -2
   "2" -1
   "3" 1
   "4" 2
   "5" 3})

(defn add-score [tasks]
  (map #(assoc % :score (get scoring (:rating %)))tasks))

(defn task-rating-sum [tasks]
  (reduce #(+ %1 (:score %2)) 0 tasks))


(defn calc-task-rating [task-id tasks]
  (let [filtered (filter #(= task-id (:taskid %)) tasks)]
    (->> (group-by :personid filtered)
         (map (fn [[task-id person-tasks]]
                [task-id (task-rating-sum person-tasks)]))
         (sort-by second >))))


(defn ->csv-output [rating]
  (->> (map #(str/join ";" %) rating)
       (str/join "\n")))

(defn calculate [lines task-id]
  (->> lines
       (parse-csv-content)
       (add-score)
       (calc-task-rating task-id)))

(defn process [filename task-id]
  (with-open [reader (io/reader filename)]
    (-> reader
        (line-seq)
        (calculate task-id)
        (->csv-output)
        (println))))

(defn -main [& [filename task-id]]
  (if-not (and filename task-id)
    (do
      (println "Error: filename and task-id should be provided")
      (println "Example: clojure -M:run small-example.csv 6156dc9dc1f742f8e11aa14d"))
    (process filename task-id)))
