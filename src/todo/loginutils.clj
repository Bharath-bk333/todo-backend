(ns todo.loginutils
  (:require [todo.dbutil :as db]))

(defn is-logged-in
  "Checks Session info to match if a user is logged in"
  []
  false)

(defn register
  [uid pass repass]
  (if (not= pass repass)
    "PassError"
    (if (db/check-id uid)
      "AlreadyExists"
      (if (db/create-id uid pass)
        "Success"
        "Failed"))))

(defn attempt-login
  [uid pass]
  (db/match-user uid pass))
