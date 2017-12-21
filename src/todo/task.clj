(ns todo.task
  (:require [clj-time.core :as tm]
            [clj-time.coerce :as tc]))

(import org.bson.types.ObjectId)

(defn create-task-obj
  "The structure of a task document in DB"
  ([uid title desc]
    (create-task-obj uid title desc "I"))
  ([uid title desc status]
    {
     :_id (ObjectId.)
     :type "task"
     :title title
     :desc desc
     :created (tc/to-long (tm/now))
     :enddate nil
     :status status
     :uid uid}))
