(ns rou.config
  (:require [fastmath.random :as r]))

(def config-template
  {:font-name "Mx437_IBM_BIOS.ttf"
   :font-size 16
   :font-height-correction 0
   :width 80
   :height 50
   :border 20
   :name "ROU"
   :custom-event? false ;; enable custom event every frame
   :rng (r/rng :isaac)})
