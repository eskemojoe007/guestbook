(ns guestbook.core
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [ajax.core :refer [GET POST]]
   [clojure.string :as string]
   [guestbook.validation :refer [validate-message]]))


; (-> (.getElementById js/document "content")
;     (.-innerHTML)
;     (set! "Hello, World!"))
; (r/render
;  [:div#hello.content>h1 "Hello, Auto!"]
;  (.getElementById js/document "content"))

;;;; re-frame functions


(rf/reg-event-fx
 :app/initialize
 (fn [_ _]
   {:db {:messages/loading? true}}))

(rf/reg-sub
 :messages/loading?
 (fn [db _]
   (:messages/loading? db)))

(rf/reg-event-db
 :messages/set
 (fn [db [_ messages]]
   (-> db
       (assoc :messages/loading? false
              :messages/list messages))))

(rf/reg-sub
 :messages/list
 (fn [db _]
   (:messages/list db [])))

(rf/reg-event-db
 :message/add
 (fn [db [_ message]]
   (update db :messages/list conj message)))



;;;; Reagent Functions


(defn send-message!
  "Sends form details to api backend."
  [fields errors]
  (if-let [validation-errors (validate-message @fields)]
    (reset! errors validation-errors)
    (POST "/api/message"
          {:format :json
           :headers
           {"Accept" "application/transit+json"
            "x-csrf-token" (.-value (.getElementById js/document "token"))}
           :params @fields
           :handler #(do
                       ; (.log js/console (str @fields))
                       ; (.log js.console (str @messages))
                       ; (.log js/console (str (conj @messages (assoc @fields :timestamp (js/Date.)))))
                       ; (swap! messages conj (assoc @fields :timestamp (js/Date.)))
                       (rf/dispatch [:message/add (-> @fields
                                                      (assoc :timestamp (js/Date.))
                                                      (update :name str " [CLIENT]"))])
                       (reset! errors nil)
                       (reset! fields nil))
           :error-handler #(do
                             (.log js.console (str %))
                             (reset! errors (get-in % [:response :errors])))})))

(defn get-messages
  "Gets messages from api and dispatches messages"
  []
  (GET "/api/messages"
       {:headers {"Accept" "application/transit+json"}
        :handler #(do
                    ; (.log js/console (str (:messages %)))
                    (rf/dispatch [:messages/set (:messages %)]))}))

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

(defn message-list
  [messages]
  [:ul.messages
   ; (for [{:keys [timestamp message name]} (sort-by :timestamp #(compare %2 %1) @messages)])
   (for [{:keys [timestamp message name]} (reverse @messages)]
     ^{:key timestamp}
     [:li
      [:time (.toLocaleString timestamp)]
      [:p message]
      [:p "@" name]])])

(defn home []
  (let [messages (rf/subscribe [:messages/list])]
    ; (rf/dispatch [:app/initialize])
    ; (get-messages)
    (fn []
      (if @(rf/subscribe [:messages/loading?])
        [:div>div.row>div.span12>h3 "Loading Messages..."]
        [:div.content
         [:div.columns.is-centered>div.column.is-two-thirds
          [:div.columns>div.column
           [message-form]]
          [:div.columns>div.column
           [:h3 "Messages"]
           [message-list messages]]]]))))

(defn mount-components
  []
  (.log js.console "Mounting Components...")
  (r/render [#'home] (.getElementById js/document "content"))
  (.log js.console "Components Mounted!"))

(defn init!
  []
  (.log js.console "Initializing App..")
  (rf/dispatch [:app/initialize])
  (get-messages)
  (mount-components))
; (.log js/console "guestbook.core evaluated!")
; (.log js/console "guestbook.core evaluated 2!")
;
; (r/render
;   [home]
;   (.getElementById js/document "content"))
