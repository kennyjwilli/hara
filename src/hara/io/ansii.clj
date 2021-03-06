(ns hara.io.ansii
  (:require [clojure.string :as string]))

(defonce colors
  {30 :grey
   31 :red
   32 :green
   33 :yellow
   34 :blue
   35 :magenta
   36 :cyan
   37 :white})

(defonce highlights
  {40 :on-grey
   41 :on-red
   42 :on-green
   43 :on-yellow
   44 :on-blue
   45 :on-magenta
   46 :on-cyan
   47 :on-white})

(defonce attributes
  {0 :reset
   1 :bold
   2 :dark
   4 :underline
   5 :blink
   7 :reverse-color
   8 :concealed})

(defonce lookup
  (reduce-kv (fn [out k v] (assoc out v k))
             {}
             (merge colors highlights attributes)))

(defn encode
  "encodes the ansii characters for modifiers
   (encode :bold)
   => \"[1m\"
   
   (encode :red)
   => \"[31m\""
  {:added "2.4"}
  [modifier]
  (if-let [i (lookup modifier)]
    (str "\033[" i "m")
    (throw (Exception. (str "Modifier not available: " modifier)))))

(defn style
  "styles the text according to the modifiers
 
   (style \"hello\" [:bold :red])
   => \"[1m[31mhello[0m\""
  {:added "2.4"}
  [text modifiers]
  (str (string/join (map encode modifiers)) text (encode :reset)))

(defn- ansii-form [modifier]
  (let [prefix (encode modifier)
        func  (-> modifier name symbol)]
    `(defn ~func [& ~'args]
       (-> ~'args
           (->> (map (fn [~'x] (str ~prefix ~'x)))
                (string/join))
           (str ~(encode :reset))))))

(defn define-ansii-forms
  "defines ansii forms given by the lookups
 
   ;; Text:
   ;; [blue cyan green grey magenta red white yellow]
 
   (blue \"hello\")
   => \"[34mhello[0m\"
 
   ;; Background:
   ;; [on-blue on-cyan on-green on-grey
   ;;  on-magenta on-red on-white on-yellow]
 
   (on-white \"hello\")
   => \"[47mhello[0m\"
 
   ;; Attributes:
   ;; [blink bold concealed dark reverse-color underline]
 
   (blink \"hello\")
   => \"[5mhello[0m\""
  {:added "2.4"}
  []
  (->> (dissoc lookup :reset)
       (keys)
       (mapv (comp eval ansii-form))))

(define-ansii-forms)
