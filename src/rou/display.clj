(ns rou.display
  (:require [clojure2d.core :as c2d]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [fastmath.core :as m]))

(def config-template
  {:font-name "Mx437_IBM_BIOS.ttf"
   :font-size 16
   :font-height-correction 0
   :width 80
   :height 50
   :border 20
   :name "ROU"})

(defn- get-font
  [font]
  (if (or (str/ends-with? font ".ttf")
          (str/ends-with? font ".otf"))
    (c2d/load-font (io/resource font))
    font))

(defn calc-font-metrics
  [{:keys [font-name ^long font-size ^long font-height-correction] :as config}]
  (let [font (get-font font-name)]
    (c2d/with-canvas [c (c2d/canvas {:width 1 :height 1 :font font})]
      (c2d/set-font-attributes c font-size)
      (let [[^double offx ^double offy ^double fw ^double fh] (c2d/text-bounding-box c "@")]
        (assoc config
               :font-metrics {:offx (m/round (m/abs offx))
                              :offy (m/round (m/abs offy))
                              :fw (m/round fw)
                              :fh (m/+ (m/round fh) font-height-correction)}
               :font font)))))

(defrecord Display [window canvas config])

(defprotocol DisplayProto
  (draw-text [canvas txt x y]))

(defn make-draw-text
  [config]
  (let [{:keys [^long offx ^long offy ^long fw ^long fh]} (:font-metrics config)]
    (fn [canvas txt ^long x ^long y]
      (let [xx (m/+ (m/* fw x) offx)
            yy (m/+ (m/* fh y) offy)]
        (c2d/text canvas txt xx yy)))))

(defn draw-tile
  [canvas tile]
  (let [{:keys [ch bg fg]} (:glyph tile)
        {:keys [^long x ^long y]} (:position tile)]
    (when bg (-> canvas
                 (c2d/set-color bg)
                 (draw-text "█" x y)))
    (-> canvas
        (c2d/set-color (or fg :white))
        (draw-text ch x y))))

(defn display
  ([world tick-fn] (display world tick-fn {}))
  ([world tick-fn config]
   (let [{:keys [font-metrics font ^long font-size name
                 ^long width ^long height
                 ^long border] :as config} (->> config
                                                (merge config-template)
                                                (calc-font-metrics))
         {:keys [^long fw ^long fh]} font-metrics
         canvas (c2d/canvas {:width (+ border (* fw width))
                             :height (+ border (* fh height))
                             :font font
                             :hints :mid})
         hborder (/ border 2)
         window (c2d/show-window {:canvas canvas
                                  :fps 60
                                  :draw-fn (fn [c w f _]
                                             (let [wstate (c2d/get-state w)]
                                               (c2d/translate c hborder hborder)
                                               (c2d/set-font-attributes c font-size)
                                               (c2d/set-state! w (tick-fn c w f wstate))))
                                  :state (assoc world :rou/config config)
                                  :window-name name})]
     (extend clojure2d.core.Canvas
       DisplayProto {:draw-text (make-draw-text config)})
     (->Display window canvas config))))
