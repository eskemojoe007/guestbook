(ns guestbook.core
  (:require
   [reagent.core :as r]
   [ajax.core :refer [GET POST]]
   [clojure.string :as string]
   [guestbook.validation :refer [validate-message]]))

; (-> (.getElementById js/document "content")
;     (.-innerHTML)
;     (set! "Hello, World!"))
; (r/render
;  [:div#hello.content>h1 "Hello, Auto!"]
;  (.getElementById js/document "content"))


(defn send-message!
  "Sends form details to api backend."
  [fields errors]
  (if-let [validation-errors (validate-message @fields)]
    (reset! errors validation-errors)
    (POST "/message"
          {:format :json
           :headers
           {"Accept" "application/transit+json"
            "x-csrf-token" (.-value (.getElementById js/document "token"))}
           :params @fields
           :handler #(do
                       (.log js/console (str "response:" %))
                       (reset! errors nil))
           :error-handler #(do
                             (.log js.console (str %))
                             (reset! errors (get-in % [:response :errors])))})))


(defn errors-component
  "React component based on an errors atom.
  errors - r/atom that is used to store our errors.
  id - the key used to extract the kind of error we have"
  [errors id]
  (when-let [error (id @errors)]
    [:div.notification.is-danger (string/join error)]))

(defn message-form
  "Component that is a message form"
  []
  (let [fields (r/atom {})
        errors (r/atom {})]
    (fn []
      [:div
       [errors-component errors :server-error]
       [:div.field
        [:label.label {:for :name} "Name"]
        [errors-component errors :name]
        [:input.input
         {:type :text
          :name :name
          :on-change #(swap! fields assoc :name (-> % .-target .-value))
          :value (:name @fields)}]]
       [:div.field
        [:label.label {:for :message} "Message"]
        [errors-component errors :message]
        [:textarea.textarea
         {:type :text
          :name :message
          :on-change #(swap! fields assoc :message (-> % .-target .-value))
          :value (:message @fields)}]]
       [:input.button.is-primary
        {:type :submit
         :on-click #(send-message! fields errors)
         :value "comment"}]
       [:p "Name: " (:name @fields)]
       [:p "Message: " (:message @fields)]])))
(defn home []
  [:div.content>div.columns.is-centered>div.column.is-two-thirds
   [:div.columns>div.column
    [message-form]]])


(r/render
 (home)
 (.getElementById js/document "content"))
