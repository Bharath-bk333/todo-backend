(ns todo.requesthandler
  (:require [todo.query :as qry]
            [todo.task]))

; Serves as a middleware between database & handler

(def wrap-data
  {
   :success {:status "success"}
   :failed {:status "failed"}
   })

(defn- wrap
  "wraps the key info with the data"
  [data & keys]
  (-> (reduce merge (map #(get wrap-data %) keys))
      (merge data)))

(defn get-all-tasks
  "Returns all task resource from the db"
  []
  (let [res (qry/fetch-all-tasks)
        result (for [task res]
                 (assoc task :_id (str (get task :_id))))]
    (if (nil? result)
      (wrap nil :failed)
      (wrap {:data result} :success))))

(defn get-user-task-by-id
  "Takes a task id & returns the task data associated with the task"
  [user taskid]
  (let [res (qry/fetch-task-by-id user taskid)
        result (when res
                 (for [task res]
                   (assoc task :_id (str (get task :_id)))))]
    (if (empty? result)
      (wrap nil :failed)
      (wrap {:data result} :success))))

(defn create-new-task
  "Creates a new task in the database
  id - unique user id
  task - generated task map"

  [user {task "data"}]
  (println task)
  (let [task (todo.task/create-task-obj
               user
               (get task "title")
               (get task "desc"))
        result (qry/create-task task)
        ]
    (if (empty? result)
      (wrap nil :failed)
      (wrap {:data (merge result {:_id (str (:_id result))})} :success))))

(defn get-user-tasks
  "Get all tasks of a user"
  [user]
  (let [res (qry/fetch-user-tasks user)
        result (when res
                 (for [task res]
                   (assoc task :_id (str (get task :_id)))))
        ]
    (if (empty? result)
      (wrap nil :failed)
      (wrap {:data result} :success))))

(defn get-user-task-status
  "Get all user tasks with the given status"
  [user status]
  (let [res (qry/fetch-user-tasks-by-status user status)
        result (when res
                 (for [task res]
                   (assoc task :_id (str (get task :_id)))))
        ]
    (if (empty? result)
      (wrap nil :failed)
      (wrap {:data result} :success))))
