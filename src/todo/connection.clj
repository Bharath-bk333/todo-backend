(ns todo.connection
  (:require [monger.core :as mg]))

(defn connect
  []
  (mg/connect))

(defn get-coll
  "Connect to the db collection"
  [conn coll-name]
  (mg/get-db conn coll-name))

(defn disconnect
  "Disconnect & close connection"
  [conn]
  (mg/disconnect conn))