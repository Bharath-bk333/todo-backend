(ns todo.user
  (:require [clj-time.core :as tm]
            [clj-time.coerce :as tc]))
(import org.bson.types.ObjectId)

(defn create-user-obj
  "Structure of user document in DB"
  [username]
  {:_id (ObjectId.)
   :uid username
   :type "user"
   :created (tc/to-long (tm/now))})
