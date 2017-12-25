(ns todo.requesthandler
  (:require [todo.query :as qry]
            [todo.task]))

; Serves as a middleware between database & handler

(def wrap-data
  {
   :success {:status "success"}
   :failed {:status "failed"}
   :update {:action "updateTask"}
   :newtask {:action "newTask"}
   :delete {:action "deleteTask"}
   :createuser {:action "createUser"}
   })

(def action-code
  {:update "updateTask"
   :newtask "newTask"
   :delete "deleteTask"
   :createuser "createUser"})

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
      (wrap {:count (count result) :data result} :success))))

(defn get-user-task-by-id
  "Takes a task id & returns the task data associated with the task"
  [user taskid]
  (try
    (let [res (qry/fetch-task-by-id user taskid)
          result (when res
                   (for [task res]
                     (assoc task :_id (str (get task :_id)))))]
      (if (empty? result)
        (wrap {:error "Invalid task id"} :failed)
        (wrap {:data result} :success)))
    (catch Exception e
      (case (.getMessage e)
        "User Does not Exist" (wrap {:error (.getMessage e)} :failed)))))

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
      (wrap nil :failed :newtask)
      (wrap {:data (merge result {:_id (str (:_id result))})} :success :newtask))))

(defn get-user-tasks
  "Get all tasks of a user"
  [user]
  (try
    (let [res  (qry/fetch-user-tasks user)
          result (when res
                   (for [task res]
                     (assoc task :_id (str (get task :_id)))))
          ]
      (wrap {:count (count result) :data result} :success))
    (catch Exception e
      (wrap {:error (.getMessage e)} :failed))))

(defn get-user-task-status
  "Get all user tasks with the given status"
  [user status]
  (try
    (let [res (qry/fetch-user-tasks-by-status user status)
          result (when res
                   (for [task res]
                     (assoc task :_id (str (get task :_id)))))
          ]
      (wrap {:count (count result) :data result} :success))
    (catch Exception e
      (case (.getMessage e)
        "User Does not Exist" (wrap {:error (.getMessage e)} :failed)))))

(defn delete-task-by-id
  "Delete task with given id from DB"
  [user ^:Map body]
  (let [res (qry/delete-task user (get body "taskid"))]
    (if res
      (wrap {:data res} {:taskid (get body "taskid")} :success :delete)
      (wrap {:taskid (get body "taskid")} :failed :delete))))

(defn update-task-by-id
  "Updates a task params given user & data"
  [user ^:Map body]
  (let [res (qry/update-task user (get body "taskid") (get body "data"))]
    (if res
      (wrap {:data res} {:taskid (get body "taskid")} :success :update)
      (wrap {:taskid (get body "taskid")} :failed :update))))

(defn create-new-user
  "Creates a new user in db"
  [^:Map body]
  (let [res (qry/create-user (get-in body ["data" "uid"]))
        result (if (nil? res) nil (update res :_id str))
        ]
    (if (empty? result)
      (wrap {:error "User Already Exists"} :failed :createuser)
      (wrap {:data result} :success :createuser))))

(defn handle-post
  "Function that handles the post request given action type"
  [user ^:Map post-map]
  (let [body (:body post-map)]
    (case (get body "action")
      "newTask" (create-new-task user body)
      "deleteTask" (delete-task-by-id user body)
      "updateTask" (update-task-by-id user body)
      "createUser" (create-new-user body))))
