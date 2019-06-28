(ns guestbook.app
  (:require
   [guestbook.core :as core]))

; (enable-console-print!)
; (println "loading env/dev/cljs/guestbook/app.cljs")
; (devtools/install!)
(set! *print-fn* (fn [& _]))

(core/init!)
