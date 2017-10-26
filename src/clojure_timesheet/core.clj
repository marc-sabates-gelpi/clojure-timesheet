(ns clojure-timesheet.core
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]
            [clojure.core.reducers :as r]
            [clojure.string :refer [blank?]])
  (:gen-class))

(def MESSAGE "A total of %.1f hours")
(def START "start")
(def END "end")
(def DEFAULT-DESC "default description")

(defn start-session [ss file desc]
  "Registers a new started session with the current time into the file"
  (let [start (c/to-date (t/now))
        new {:start start :desc (if (blank? desc) DEFAULT-DESC desc)}]
    (spit file (conj ss new))
    (println (str "Added new session: " new))))

(defn end-session [ss file]
  "Registers the end of the last started session with the current time into the file"
  (let [end (c/to-date (t/now))
        current (peek ss)
        ended (assoc current :end end)]
    (spit file (sort-by #(:start %) #(compare %2 %) (conj (pop ss) ended)))
    (println (str "Current ended session: " ended))))

(defn sum-up-sessions [ss]
  "Sums up the completed sessions time"
  (-> (r/fold + (r/map #(t/in-minutes (t/interval
                                       (c/from-date (:start %))
                                       (c/from-date (:end %))))
                       (r/filter #(and (:start %) (:end %)) ss)))
      (as-> minutes
          (/ minutes 60))
      float
      (as-> amount (format MESSAGE amount))
      println))

(defn -main
  [& [file op desc]]
  (-> file
      slurp
      read-string
      (as-> sessions
          (cond (= op START) (start-session sessions file desc) 
                (= op END) (end-session sessions file)
                :else (sum-up-sessions sessions)))))
