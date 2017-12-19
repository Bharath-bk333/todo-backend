(ns todo.query
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [todo.connection :as tcn])
  (:import org.bson.types.ObjectId))

(def bucket {:tasks "taskscoll"}) ;collection names in db -do not change

(def coll-task "taskdata")

(defn create-user
  [user]
  (let [conn (tcn/connect)
        db (tcn/get-coll conn coll-task)
        ]
    (let [res (mc/insert-and-return db (:tasks bucket)
                                    {:user user
                                     :tasks []})
          ]
      (mg/disconnect conn)
      (not (nil? res)))))

(defn insert-task
  [user task]
  (let [conn (tcn/connect)
        db (tcn/get-coll conn coll-task)
        ]
    (let [res (mc/update db (:tasks bucket)
                         {:user user}
                         {$push {:tasks task}}
                         {:upsert true})
          ]
      (mg/disconnect conn)
      (not (nil? res)))))

(defn delete-task
  [user taskid]
  (let [conn (tcn/connect)
        db (tcn/get-coll conn coll-task)
        ]
    (let [res (mc/update
                db
                (:tasks bucket)
                {:user user}
                {$pull {:tasks {:tid taskid}}})
          ]
      (mg/disconnect conn)
      (not (nil? res)))))

(defn update-task
  "updates a certain field of task"
  [user taskid updatekeys newvalues]
  (let [conn (tcn/connect)
        db (tcn/get-coll conn coll-task)
        nmap (zipmap (mapv #(str "tasks.$." %) updatekeys) newvalues)
        ]
    (println "nmappp" nmap)
    (let [res (mc/update
                db
                (:tasks bucket)
                {:user user
                 :tasks.tid taskid}
                {$set nmap})
          ]
      (println "the result" res)
      (mg/disconnect conn)
      (not (nil? res)))))

(defn- fetch-db-data
  [condition-map]
  (let [conn (tcn/connect)
        db (tcn/get-coll conn coll-task)
        ]
    (let [res (mc/find-one-as-map
                db
                (:tasks bucket)
                condition-map)
          ]
      (mg/disconnect conn)
      res)))

(defn test
  []
  (let [conn (tcn/connect)
        db (tcn/get-coll conn coll-task)
        ]
    (let [res (mc/update
                db
                (:tasks bucket)
                {:user "sumit"
                 :tasks.tid "RPKVVKTSD"}
                {$set {:tasks.title "taskman"}})
          ]
      (mg/disconnect conn)
      res)))