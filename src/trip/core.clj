(ns trip.core
  (:require [quil.core :as q]
            [quil.util :as u]
            [quil.middleware :as m]
            [clojure.data.csv :as csv]
            [clojure.core.async :as async :refer (<! <!! >! >!!
                                                     put! chan go go-loop)]))
(def revise reset!)
(def life put!)
(def is (chan))
(def quality (atom [255 255 255])) ;; defaults to white
(def qualia-source "https://docs.google.com/spreadsheets/d/1ShOWkEyJSkeFswHBq0kE9yh8fdxVYRXw2xPyOyfIirk/export?format=csv")


(go-loop [_ (<! (async/timeout 0))]
  (let [qualias (-> qualia-source slurp csv/read-csv)]
    (defmacro reality [qualia]
      `(case ~qualia
         ~@(mapcat (fn [[name & colors]]
                     [name (mapv #(Integer/parseInt %) colors)])
                   qualias))))
  (recur (<! (async/timeout 300))))

(go-loop [qualia (<! is)]
  (let [test (reality qualia)]
    (println test)
      (->> (reality qualia)
           (revise quality)))
  (recur (<! is)))

(defn setup []
  (q/smooth))


(defn draw []
  (apply q/background @quality))



(q/sketch
     :renderer :opengl
     :setup setup
     :features [:present]
     :draw draw
     :size :fullscreen)


