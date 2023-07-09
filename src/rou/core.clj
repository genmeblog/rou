(ns rou.core
  (:require [rou.display :as disp]
            [clojure2d.core :as c2d]
            [fastmath.core :as m]
            [rou.core :as rou]))

(def game-name "ROU")

(defn update-frame
  [state]
  (assoc state :movers (map (fn [mover]
                              (update-in mover [:position :x] (fn [^long pos]
                                                                (m/mod (m/dec pos) 80)))) (state :movers))))

(defn render-state
  [canvas state]
  (doseq [mover (state :movers)]
    (disp/draw-tile canvas mover))
  (disp/draw-tile canvas (state :player)))

(defn tick
  [canvas _window ^long _frame state]
  (-> canvas
      (c2d/set-background :black)
      (disp/draw-text "Hello Clojure World ╔═╦═╗ ###" 0 0)
      (disp/draw-text "║ ║ ║ # #" 20 1)
      (disp/draw-text "╚═╩═╝ ###" 20 2)
      (disp/draw-text "01234567890123456789012345678901234567890123456789012345678901234567890123456789" 0 3)
      (disp/draw-text "012345.·∙•○°○•∙·.789012345678901234567890123456789012345678901234567890123456789" 0 4)
      (render-state state))
  (update-frame state))

(def world
  {:player {:position {:x 40 :y 25}
            :glyph {:ch "@" :bg :black :fg :yellow}}
   :movers (for [i (range 10)]
             {:position {:x (* i 7) :y 20}
              :glyph {:ch "☺" :bg :black :fg :red}})})

(def game-display (disp/display world tick {:name game-name}))

(defn move-player-up-down
  [position ^long off ^long mx]
  (update position :y (fn [^long y] (m/constrain (m/+ y off) 0 (dec mx)))))

(defn move-player-left-right
  [position ^long off ^long mx]
  (update position :x (fn [^long x] (m/constrain (m/+ x off) 0 (dec mx)))))

(defmethod c2d/key-pressed [game-name c2d/virtual-key] [e state]
  (let [{:keys [width height]} (state :rou/config)]
    (update-in state [:player :position] (fn [player-position]
                                           (case (c2d/key-code e)
                                             :left (move-player-left-right player-position -1 width)
                                             :right (move-player-left-right player-position 1 width)
                                             :up (move-player-up-down player-position -1 height)
                                             :down (move-player-up-down player-position 1 height)
                                             player-position)))))
