(ns todo.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [todo.views :as views]
            [todo.dbutil :as db]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response redirect]]))

(defroutes app-routes
           (GET "/"
                []
             (views/gen-home-page))

           (GET "/user/:id"
                [id]
             (views/gen-user-page id))

           (POST "/generate"
                 {params :params}
             (println params)
             (let [res (db/create-id (:newid params))]
               (if res
                 (redirect (str "/user/" (:newid params)))
                 "User already exists")))

           (POST "/user/:id/add-task"
                 {params :params}
             (db/add-task (:id params) (:title params) (:desc params))
             (redirect (str "/user/" (:id params))))
           (GET "/json"
                [] (response {:name "hello"}))

           (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults)
  ;(->
  ;  app-routes
  ;  (wrap-json-response))
  )
