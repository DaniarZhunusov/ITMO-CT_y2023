(defn make-operation [op]
  (fn [a b]
    (fn [env] (op (a env) (b env)))))

(defn constant [value]
  (fn [args] value))

(defn variable [name]
  (fn [args] (args name)))

(def add (make-operation +))
(def subtract (make-operation -))
(def multiply (make-operation *))
(def divide (make-operation #(/ (double %1) (double %2))))
(def negate (fn [a] (fn [arg] (- (a arg)))))
(def sin (fn [a] (fn [env] (Math/sin (a env)))))
(def cos (fn [a] (fn [env] (Math/cos (a env)))))

(def operations
  {'+      add
   '-      subtract
   '*      multiply
   '/      divide
   'negate negate
   'sin    sin
   'cos    cos})

(defn parse [c v mapping a]
  (cond
    (number? a) (c a)
    (symbol? a) (v (str a))
    (contains? mapping (first a)) (apply (get mapping (first a)) (mapv #(parse c v mapping %) (rest a)))))

(defn parseFunction [a]
  (parse constant variable operations (read-string a)))

(definterface Expression
  (^Number evaluate [vars])
  (^String toString [expression]))

(defn evaluate [expr vars] (.evaluate expr vars))
(defn toString [expr] (.toString expr))

(defn toStr [op a]
  (str "(" op " " (clojure.string/join " " (mapv toString a)) ")"))

(deftype Const [value]
  Expression
  (evaluate [this _] value)
  (toString [this] (str (.-value this))))

(deftype Var [vars]
  Expression
  (evaluate [this args] (args vars))
  (toString [this] (str vars)))

(defn Constant [x] (Const. x))
(defn Variable [x] (Var. x))

(deftype MakeOperation [op sign val]
  Expression
  (evaluate [this args] (apply op (map #(.evaluate % args) val)))
  (toString [this] (toStr sign val)))

(defn operation [op sign]
  (fn [& val] (MakeOperation. op sign val)))

(def Add (operation + '+))
(def Subtract (operation - '-))
(def Multiply (operation * '*))

(defn DivideZero [num divide]
  (if (zero? divide)
    Double/NaN
    (/ num divide)))

(def Divide (operation DivideZero '/))
(def Negate (operation - 'negate))
(def Exp (operation #(Math/exp %) 'exp))
(def Ln (operation #(Math/log %) 'ln))

(def op
  {'+ Add
   '- Subtract
   '* Multiply
   '/ Divide
   'negate Negate
   'exp Exp
   'ln Ln})

(defn parseObject [a]
  (parse Constant Variable op (read-string a)))
