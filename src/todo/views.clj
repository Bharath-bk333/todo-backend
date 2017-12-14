(ns todo.views
  (:require [hiccup.page :as page]
            [todo.dbutil :as db]
            [ring.util.anti-forgery :as util]))

(defn gen-task-table
  "Table of tasks given a user id"
  [uid]
  [:ul [:table {:style "width: 90%"}
        [:tr [:th "Task"] [:th "Description"]]
        (for [[id title desc] (db/get-all-task uid)]
          [:tr
           [:td {:style "align: '"} title] [:td desc]])
        ]])

(defn gen-home-page
  ([]
    (page/html5
      [:h1 "Todo List"]
      [:form {:action "/generate" :method "POST"}
       (util/anti-forgery-field)
       [:p "Create id " [:input {:type "text" :name "newid"}] "  " [:input {:type "submit" :value "newid"}]]])))

(defn gen-user-page
  [uid]
  (if (db/check-id uid)
    (page/html5
      [:h1 uid "'s ToDo List"]
      [:form {:action (str "/user/" uid "/add-task") :method "POST"}
       (util/anti-forgery-field)
       [:p "Title " [:input {:type "text" :name "title"}]]
       [:p "Desc " [:input {:type "text" :name "desc"}]]
       [:p [:input {:type "hidden" :name "id" :value uid}]]
       [:p [:input {:type "submit" :value "Add task"}]]]
      (gen-task-table uid))
    (page/html5
      [:h2 "User Does not exist"])
    ))