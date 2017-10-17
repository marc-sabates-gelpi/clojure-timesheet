(ns clojure-timesheet.core
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]
            [clojure.core.reducers :as r])
  (:gen-class))

(def MESSAGE "A total of %.1f hours")

(defn get-time [s]
  (r/fold + (r/map #(t/in-minutes
                     (t/interval
                      (c/from-date
                       (:start %))
                      (c/from-date
                       (:end %)))) s)))

(defn to-hours [m]
  (/ m 60))

(defn -main
  [& args]
  (->> args
      first
      slurp
      read-string
      get-time
      to-hours
      float
      (format MESSAGE)
      println))
