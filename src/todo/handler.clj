(ns todo.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params wrap-json-body]]
            [ring.util.response :refer [response redirect]]
            [todo.query :as qry]
            [todo.task :as tsk]
            [todo.requesthandler :as rh]
            [clojure.pprint :as pprint]))

(defroutes app-routes
           (GET "/tasks"
                []
             (response (rh/get-all-tasks)))

           (context "/tasks/:user" [user]
             (GET "/" [user]
               (response (rh/get-user-tasks user)))

             (GET "/:taskid" [taskid]
               (response (rh/get-user-task-by-id user taskid)))

             (GET "/status/:s"
                  [s]
               (response (rh/get-user-task-status user s)))

             (POST "/"
                   request
               (println (:body request))
               (response (rh/create-new-task user (:body request)))))

           (route/not-found "Not Found"))

(def app
  ;(wrap-defaults app-routes site-defaults)
  ;(wrap-json-body app-routes)
  (->
    app-routes
    wrap-json-body
    wrap-json-response)
  )
