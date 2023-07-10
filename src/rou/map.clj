(ns rou.map
  (:require [rou.display :as disp]
            [rou.map.simple :as simple-map]))

(defn make-map
  [kind config]
  (case kind
    :simple (simple-map/new-map config)))

(defn draw-map
  [canvas state]
  (let [{:keys [^long width ^long height]} (state :rou/config)
        m (state :map)]
    (doseq [x (range width)
            y (range height)
            :let [t (get-in m [y x])]]
      (if (= t :floor)
        (disp/draw-tile canvas "." :black :gray x y)
        (disp/draw-tile canvas "#" :black :green x y)))
    canvas))
