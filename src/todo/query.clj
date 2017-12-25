(ns todo.query
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [todo.connection :as tcn]
            [todo.user :as us]
            [monger.util :refer [object-id]]
            [monger.result :refer [acknowledged? updated-existing?]])
  (:import org.bson.types.ObjectId)
  (:import com.mongodb.WriteResult))

(def bucket {:tasks "taskscoll"}) ;collection names in db -do not change

(def coll-task "taskdata")

(defn create-user
  "Creates a new user in the database"
  [user]
  (let [conn (tcn/connect)
        db (tcn/get-coll conn coll-task)
        ]
    (if (mc/any? db (:tasks bucket) {:uid user :type "user"})
      nil                                                   ;if user already exists
      (let [res (mc/insert-and-return
                  db
                  (:tasks bucket)
                  (us/create-user-obj user))
            ]
        (mg/disconnect conn)
        res))))

(defn create-task
  "Creates a new task in DB with given task params"
  [task]
  (let [conn (tcn/connect)
        db (tcn/get-coll conn coll-task)
        ]
    (let [res (mc/insert-and-return
                db
                (:tasks bucket)
                task)
          ]
      (mg/disconnect conn)
      res)))

(defn delete-task
  [user taskid]
  (let [conn (tcn/connect)
        db (tcn/get-coll conn coll-task)
        ]
    (let [res (mc/remove
                db
                (:tasks bucket)
                {:_id (object-id taskid)
                 :uid user})]
      (mg/disconnect conn)
      (= 1 (.getN res)))))

(defn update-task
  "updates a certain field of task"
  [user taskid ^:Map newmap]
  (let [conn (tcn/connect)
        db (tcn/get-coll conn coll-task)
        ]
    (let [res (mc/update
                db
                (:tasks bucket)
                {:_id (object-id taskid)
                 :uid user}
                {$set newmap})
          ]
      (mg/disconnect conn)
      (updated-existing? res))))

(defn- fetch
  [db condition-map]
  (let [res (doall (mc/find-maps
                     db
                     (:tasks bucket)
                     condition-map))]
    res))

(defn- fetch-db-data
  [condition-map]
  (let [conn (tcn/connect)
        db (tcn/get-coll conn coll-task)
        ]
    (if (:uid condition-map)
      (if (mc/any? db (:tasks bucket)
                   {:uid (:uid condition-map) :type "user"})
        (let [res (fetch db condition-map)]
          (mg/disconnect conn)
          res)
        (do (mg/disconnect conn)
            (throw (Exception. "User Does not Exist")))
        )
      (let [res (fetch db condition-map)]
        (mg/disconnect conn)
        res))))

(defn fetch-all-tasks
  []
  (let [q {:type "task"}
        data (fetch-db-data q)
        ]
    data))

(defn fetch-task-by-id
  [user taskid]
  (try
    (let [obid (object-id taskid)
          q {:_id obid
             :uid user}
          data (fetch-db-data q)
          ]
      data)
    (catch Exception e
      nil)))

(defn fetch-user-tasks
  "queries the db for all the tasks of user"
  [user]
  (let [q {:uid user
           :type "task"}
        data (fetch-db-data q)
        ]
    data))

(defn fetch-user-tasks-by-status
  "Queries the database to get all user tasks with given status"
  [user status]
  (let [q {:uid user
           :status status}
        data (fetch-db-data q)
        ]
    data))