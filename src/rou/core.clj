(ns rou.core
  (:require [rou.display :as disp]
            [clojure2d.core :as c2d]
            [fastmath.core :as m]
            [rou.map :as map]))

(def game-name "ROU")

(defn render-state
  [canvas state]
  (-> canvas
      (map/draw-map state)
      (disp/draw-tile (state :player))))

(defn tick
  [canvas _window  _frame _draw-state state]
  (-> canvas
      (c2d/set-background :black)
      (render-state state)))

(defn world
  [config]
  {:player {:position [40 25] :ch "@" :bg :black :fg :yellow}
   :map (map/make-map :simple config)})

(def game-display (disp/display world tick {:name game-name
                                          :font-size 12}))

(defn move-player
  [[^long x ^long y :as position] m [^long offx ^long offy] [^long mx ^long my]]
  (let [nx (m/constrain (m/+ x offx) 0 mx)
        ny (m/constrain (m/+ y offy) 0 my)]
    (if-not (= :wall (get-in m [ny nx]))
      [nx ny]
      position)))

(defn move-player-dir
  [state dir]
  (let [borders ((juxt :width :height) (state :rou/config))
        m (:map state)]
    (update-in state [:player :position] (fn [player-position]
                                           (case dir
                                             (:left :numpad4 \h) (move-player player-position m [-1 0] borders)
                                             (:right :numpad6 \l) (move-player player-position m [1 0] borders)
                                             (:up :numpad8 \k) (move-player player-position m [0 -1] borders)
                                             (:down :numpad2 \j) (move-player player-position m [0 1] borders)
                                             player-position)))))

(defmethod c2d/key-pressed [game-name c2d/virtual-key] [e state] (move-player-dir state (c2d/key-code e)))
(defmethod c2d/key-pressed [game-name \h] [_ state] (move-player-dir state \h))
(defmethod c2d/key-pressed [game-name \l] [_ state] (move-player-dir state \l))
(defmethod c2d/key-pressed [game-name \k] [_ state] (move-player-dir state \k))
(defmethod c2d/key-pressed [game-name \j] [_ state] (move-player-dir state \j))

(defmethod c2d/key-pressed [game-name \space] [e state]
  (when (c2d/control-down? e)
    (c2d/save (:canvas game-display) "screenshots/week2.jpg"))
  state)
