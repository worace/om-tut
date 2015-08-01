(ns ^:figwheel-always om-tut.core
    (:require-macros [cljs.core.async.macros :refer [go]])
    (:require[om.core :as om :include-macros true]
             [cljs.core.async :refer [put! chan <!]]
             [om.dom :as dom :include-macros true]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state
  (atom
   {:contacts
    [{:first "Ben" :last "Bitdiddle" :email "benb@mit.edu"}
     {:first "Alyssa" :middle-initial "P" :last "Hacker" :email "aphacker@mit.edu"}
     {:first "Eva" :middle "Lu" :last "Ator" :email "eval@mit.edu"}
     {:first "Louis" :last "Reasoner" :email "prolog@mit.edu"}
     {:first "Cy" :middle-initial "D" :last "Effect" :email "bugs@mit.edu"}
     {:first "Lem" :middle-initial "E" :last "Tweakit" :email "morebugs@mit.edu"}]}))

(defn by-id [id] (.getElementById js/document id))

(defn middle-name [{:keys [middle middle-initial]}]
  (cond
    middle (str " " middle)
    middle-initial (str " " middle-initial ".")))

(defn display-name [{:keys [first last] :as contact}]
  (str last ", " first (middle-name contact)))

(defn contact-view [contact owner]
  (println "contact view")
  (reify
    om/IRenderState
    (render-state [this {:keys [delete]}]
      (dom/li nil
              (dom/span nil (display-name contact))
              (dom/button #js {:onClick (fn [e] (put! delete @contact) nil)} "Delete")))))

(defn contacts-view [data owner]
  (reify
    om/IInitState
    (init-state [_] {:delete (chan)})
    om/IWillMount
    (will-mount [_]
      (let [delete (om/get-state owner :delete)]
        (go (loop []
              (let [contact (<! delete)]
                (om/transact! data :contacts
                              (fn [xs] (vec (remove #(= contact %) xs)))))
              (recur)))))
    om/IRenderState
    (render-state [this {:keys [delete]}]
      (dom/div nil
               (dom/h2 nil "Contact List:")
               (apply dom/ul nil
                      (om/build-all
                       contact-view
                       (:contacts data)
                       {:init-state {:delete delete}}))))))

(defn stripe [text bgc]
  (let [style #js {:backgroundColor bgc}]
    (dom/li #js {:style style} text)))

(om/root contacts-view
         app-state
         {:target (by-id "contacts")})

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

