(ns ^:figwheel-always om-tut.core
    (:require[om.core :as om :include-macros true]
              [om.dom :as dom :include-macros true]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello pizza!"}))

(om/root
  (fn [data owner]
    (om/component (dom/h2 nil (:text data))))
  app-state
  {:target (. js/document (getElementById "app0"))})

(om/root
 (fn [data owner]
   (om/component (dom/h2 nil (:text data))))
 app-state
 {:target (. js/document (getElementById "app1"))})



(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

