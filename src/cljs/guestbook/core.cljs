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
  [fields errors messages]
  (if-let [validation-errors (validate-message @fields)]
    (reset! errors validation-errors)
    (POST "/message"
          {:format :json
           :headers
           {"Accept" "application/transit+json"
            "x-csrf-token" (.-value (.getElementById js/document "token"))}
           :params @fields
           :handler #(do
                       ; (.log js/console (str @fields))
                       ; (.log js.console (str @messages))
                       ; (.log js/console (str (conj @messages (assoc @fields :timestamp (js/Date.)))))
                       (swap! messages conj (assoc @fields :timestamp (js/Date.)))
                       (reset! errors nil)
                       (reset! fields nil))
           :error-handler #(do
                             (.log js.console (str %))
                             (reset! errors (get-in % [:response :errors])))})))


(defn get-messages
  "Gets messages from api and fills in the `messages` atom."
  [messages]
  (GET "/messages"
       {:headers {"Accept" "application/transit+json"}
        :handler #(reset! messages (:messages %))}))


(defn errors-component
  "React component based on an errors atom.
  errors - r/atom that is used to store our errors.
  id - the key used to extract the kind of error we have"
  [errors id]
  (when-let [error (id @errors)]
    [:div.notification.is-danger (string/join error)]))

; (defn message-list-component
;   [message]
;   [:li
;    [:time (:timestamp message)]
;    [:p (:message message)]
;    [:p (str " - " (:name message))]])

(defn message-form
  "Component that is a message form"
  [messages]
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
         :on-click #(send-message! fields errors messages)
         :value "comment"}]
       [:p "Name: " (:name @fields)]
       [:p "Message: " (:message @fields)]])))

(defn message-list
  [messages]
  [:ul.messages
   (for [{:keys [timestamp message name]} @messages]
     ^{:key timestamp}
     [:li
      [:time (.toLocaleString timestamp)]
      [:p message]
      [:p " - " name]])])

(defn home []
  (let [messages (r/atom nil)]
    (get-messages messages)
    (fn []
      [:div.content
       [:div.columns.is-centered>div.column.is-two-thirds
        [:div.columns>div.column
         [message-form messages]]
        [:div.columns>div.column
         [:h3 "Messages"]
         [message-list messages]]]])))



(r/render
 [home]
 (.getElementById js/document "content"))
