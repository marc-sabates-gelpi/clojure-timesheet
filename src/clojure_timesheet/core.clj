(ns clojure-timesheet.core
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]
            [clojure.core.reducers :as r]
            [clojure.string :refer [blank?]])
  (:gen-class))

(def ^:const SUM-UP-MESSAGE "A total of %.1f hours from %s")
(def ^:const START "start")
(def ^:const END "end")
(def ^:const STOP "stop")
(def ^:const DEFAULT-DESC "default description")
(def ^:const FINISH-MESSAGE "Session finished: %s")
(def ^:const FINISH-WARNING-MESSAGE "Session was already finished, nothing changed: %s")
(def ^:const START-MESSAGE "New session started: %s")
(def ^:const START-WARNING-MESSAGE "There is already a started session, nothing changed: %s")

(defn get-started [node]
  (when (and (:start node) (not (:end node))) node))
(defn start-session
  "Registers a new started session with the current time into the file"
  [all file desc]
  (let [all (reverse (sort-by :start all))
        ongoing (some get-started all)]
    (if ongoing
      (println (format START-WARNING-MESSAGE ongoing))
      (let [new {:start (c/to-date (t/now)) :desc (or desc DEFAULT-DESC)}]
        (spit file (conj all new))
        (println (format START-MESSAGE new))))))

(defn end-session
  "Registers the end of the last started session with the current time into the file"
  [sessions file]
  (let [[current & others] (reverse (sort-by :start sessions))]
    (if (:end current)
      (println (format FINISH-WARNING-MESSAGE current))
      (let [end (assoc current :end (c/to-date (t/now)))]
        (println (format FINISH-MESSAGE end))
        (spit file (conj others end))))))

(defn from-date
  "Gets the earliest session time"
  [sessions]
  (-> (sort-by :start sessions)
      first
      :start))

(defn sum-up-sessions
  "Sums up the completed sessions time"
  [sessions]
  (-> (r/fold + (r/map #(t/in-minutes (t/interval
                                       (c/from-date (:start %))
                                       (c/from-date (:end %))))
                       (r/filter #(and (:start %) (:end %)) sessions)))
      (/ 60)
      float
      (as-> amount (format SUM-UP-MESSAGE amount (from-date sessions)))
      println))

(defn -main
  [& [file op desc]]
  (-> file
      slurp
      read-string
      (as-> sessions
          (cond (= op START) (start-session sessions file desc) 
                (or (= op END) (= op STOP)) (end-session sessions file)
                :else (sum-up-sessions sessions)))))
