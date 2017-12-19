(ns todo.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response redirect]]
            [todo.query :as qry]
            [todo.task :as tsk]
            [todo.requesthandler :as rh]
            [clojure.pprint :as pprint]))

;(defroutes app-routes
;           (GET "/"
;                []
;             (redirect "/register"))
;
;           (GET "/user/:id"
;                [id]
;             (views/gen-user-page id))
;
;           (POST "/generate"
;                 {params :params}
;             (println params)
;             ;(let [res (db/create-id (:newid params))]
;             ;  (if res
;             ;    (redirect (str "/user/" (:newid params)))
;             ;    "User already exists"))
;              )
;
;           (POST "/user/:id/add-task"
;                 {params :params}
;             (db/add-task (:id params) (:title params) (:desc params))
;             (redirect (str "/user/" (:id params))))
;
;           (GET "/register"
;                []
;             (views/generate-register-page))
;
;           (POST "/register"
;                 {params :params}
;             (let [resp (lutil/register (:username params) (:pass params) (:repass params))]
;               (case resp
;                 "PassError" "Passwords do not match"
;                 "AlreadyExists" "User Already Exists"
;                 "Success" "Registered"
;                 "Failed" "Unknown Error")))
;
;           (GET "/login"
;                []
;             (views/generate-login-page))
;
;           (POST "/login"
;                 {params :params}
;             (if (lutil/attempt-login (:username params) (:pass params))
;               (redirect (str "/user/" (:username params)))
;               "Invalid Userid or Password"))
;
;           (GET "/json"
;                []
;             ;(wrap-json-response {:name "hello"})
;             {:status 200
;              :headers {}
;              :body {:name "hello"}}
;              )
;
;           (GET "/dbtest"
;                []
;             ;(println (qry/create-user "sumit"))
;             ;(println (qry/insert-task "sumit" (tsk/create-task-obj "task1" "testing 1")))
;             ;(println (qry/fetch-db-data {:user "sumit" :tasks.tid "RPKVVKTSD"}))
;             ;(println (qry/update-task "sumit" "RPKVVKTSD" ["title"  "desc"] ["title22" "hello"]))
;             (println (qry/test))
;             ;"finish"
;             )
;
;           (route/not-found "Not Found"))

(defroutes app-routes
           (POST "/post"
                 {params :params}
             (rh/route-request params))
           (route/not-found "Not Found"))

(def app
  ;(wrap-defaults app-routes site-defaults)
  (->
    app-routes
    (wrap-json-response app-routes)))
