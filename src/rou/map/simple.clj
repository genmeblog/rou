(ns rou.map.simple
  (:require [fastmath.random :as r]))

(defn new-map
  [{:keys [^long width ^long height rng]}]
  (let [w- (dec width)
        h- (dec height)
        blocks (->> (repeatedly 400 (fn [] [(r/irandom rng 1 width)
                                           (r/irandom rng 1 height)]))
                    (remove #{[40 25]})
                    (set))]
    (vec (for [y (range height)]
           (vec (for [x (range width)]
                  (if (or (zero? y) (zero? x)
                          (== x w-) (== y h-)
                          (blocks [x y]))
                    :wall :floor)))))))

#_(new-map {:width 10 :height 5 :rng r/default-rng})
