(defn make-operation [op]
  (fn [& args]
    (apply mapv op args)))

(def v+ (make-operation +))
(def v- (make-operation -))
(def v* (make-operation *))
(def vd (make-operation /))

(defn scalar [v1 v2]
  (reduce + (map * v1 v2)))

(def m+ (make-operation v+))
(def m- (make-operation v-))
(def m* (make-operation v*))
(def md (make-operation vd))

(defn transpose [m]
  (apply mapv vector m))

(defn m*v [m v]
  (mapv #(scalar % v) m))

(defn m*m [& args]
  (reduce (fn [a b] (mapv (partial m*v (transpose b)) a)) args))

(defn scalarmultiply [scalar]
  (fn [vector]
    (mapv (partial * scalar) vector)))

(defn v*s [vector scalar]
  ((scalarmultiply scalar) vector))

(defn m*s [matrix scalar]
  (mapv (scalarmultiply scalar) matrix))


(defn vect [[a0 a1 a2] [b0 b1 b2]]
  [( - (* a1 b2) (* a2 b1))
   ( - (* a2 b0) (* a0 b2))
   ( - (* a0 b1) (* a1 b0))])

(def c+ (make-operation m+))
(def c- (make-operation m-))
(def c* (make-operation m*))
(def cd (make-operation md))


