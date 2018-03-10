(defproject clojure-timesheet "0.1.0-SNAPSHOT"
  :description "Adds up the timesheet"
  :license {:name "GNU General Public License (GPL) version 3"
            :url "https://www.gnu.org/licenses/gpl.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [clj-time "0.13.0"]]
  :main ^:skip-aot clojure-timesheet.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
