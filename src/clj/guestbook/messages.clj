(ns guestbook.messages
  (:require [guestbook.db.core :as db]
            [guestbook.validation :refer [validate-message]]))

(defn message-list
  "API for returning database messages"
  []
  {:messages (vec (db/get-messages))})

(defn save-message!
  [message]
  (if-let [errors (validate-message message)]
    (throw (ex-info "Message is invalid"
                    {:guestbook/error-id :validation
                     :errors errors}))
    (db/save-message!
     (assoc message :timestamp (java.util.Date.)))))
