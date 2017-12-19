(ns todo.task
  (:require [clj-time.core :as tm]
            [clj-time.coerce :as tc]))

;{
; title:""
; desc:""
; created: date
; enddate: date
; status: Complete/Inprogress/Dropped aka (C/I/D)
; }
(defn rand-str [len]
  (apply str (take len (repeatedly #(char (+ (rand 26) 65))))))

(defn create-task-obj
  ([title desc]
    (create-task-obj title desc "I"))
  ([title desc status]
    {
     :tid (rand-str 10)
     :title title
     :desc desc
     :created (tc/to-long (tm/now))
     :enddate nil
     :status status
     }))
