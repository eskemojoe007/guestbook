(ns guestbook.routes.home
  (:require
    [guestbook.layout :as layout]
    [guestbook.db.core :as db]
    ; [clojure.java.io :as io]
    [guestbook.middleware :as middleware]
    [ring.util.http-response :as response]
    ; [struct.core :as st]
    [guestbook.validation :refer [validate-message]]))
    ; [clojure.pprint :refer [pprint]]))

(defn save-message!
  [{:keys [params]}]
  (if-let [errors (validate-message params)]
    (response/bad-request {:errors errors})
    (try
      (db/save-message!
       (assoc params :timestamp (java.util.Date.)))
      (response/ok "Body")
      (catch Exception e
        (response/internal-server-error
         {:errors {:server-error ["Failed to save message!"]}})))))

(defn home-page
  "Home page handler.
  Takes the request and renders the template"
  ; [{:keys [flash] :as request}]
  [request]
  (layout/render
   request
   "home.html"))
   ; (merge {:messages (db/get-messages)}
   ;        (select-keys flash [:name :message :errors]))))

(defn message-list
  "API for returning database messages"
  [_]
  (response/ok {:messages (vec (db/get-messages))}))

(defn about-page [request]
  (layout/render request "about.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/messages" {:get message-list}]
   ["/message" {:post save-message!}]
   ["/about" {:get about-page}]])
