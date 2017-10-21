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

(defn -main
  [& [file op desc]]
  (-> file
      slurp
      read-string
      (as-> sessions
          (cond (= op START) (let [start (pr-str (c/to-date (t/now)))
                                   new-session {:start start :desc (if (blank? desc) DEFAULT-DESC desc)}]
                               (spit file (conj sessions new-session))
                               (str "Added new session: " new-session))
                (= op END) (let [end (pr-str (c/to-date (t/now)))
                                 current-session (peek sessions)
                                 ended-session (assoc current-session :end end)]
                             (spit file (conj (pop sessions) ended-session))
                             (str "Ended session: " ended-session))
                :else (-> (r/fold + (r/map #(t/in-minutes (t/interval
                                                              (c/from-date (:start %))
                                                              (c/from-date (:end %))))
                                           (r/filter #(and (:start %) (:end %)) sessions)))
                          (as-> minutes
                              (/ minutes 60))
                          float
                          (as-> amount (format MESSAGE amount)))))
      println))
