(ns todo.dbutil)

(def usertasks (atom {}))

(defn check-id
  "Returns true if a given key/id exists in db else false"
  [id]
  (contains? @usertasks id))

(defn get-all-task
  "Returns a vector of all the tasks given an id"
  [userid]
  (get @usertasks userid))

(defn add-task
  [userid t-title t-desc]
  (let [tid (inc (apply max 0 (map #(first %) (get-all-task userid))))
        taskdata [(list tid t-title t-desc)]
        newdb (update @usertasks userid into taskdata)
        ]
    (reset! usertasks newdb)))

(defn create-id
  "Generates a new userid"
  [new-id]
  (if (check-id new-id)
    false
    (do
      (swap! usertasks merge {new-id []})
      true)))

(defn delete-task
  "Deletes a task from user's tasks given userid & taskid"
  [uid t-id])
