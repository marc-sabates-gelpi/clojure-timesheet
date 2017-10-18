(ns clojure-timesheet.core
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]
            [clojure.core.reducers :as r])
  (:gen-class))

(def MESSAGE "A total of %.1f hours")

(defn -main
  [& args]
  (-> args
      first
      slurp
      read-string
      (as-> session (r/fold + (r/map #(t/in-minutes (t/interval
                                                     (c/from-date (:start %))
                                                     (c/from-date (:end %))))
                                     session)))
      (as-> minutes (/ minutes 60))
      float
      (as-> amount (format MESSAGE amount))
      println))
