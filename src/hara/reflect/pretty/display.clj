(ns hara.reflect.pretty.display
  (:require [hara.reflect.types.element :refer [to-element element?]]
            [hara.reflect.pretty.display.filter :refer [filter-terms-fn]]
            [hara.reflect.pretty.display.sort :refer [sort-terms-fn]]))

(defn- first-terms-fn
  [grp]
  (if (:first grp) first))

(defn- merge-terms-fn
  [grp]
  (if (:merge grp)
    (fn [eles]
      (if-let [name (-> eles first :name)]
        (let [eles (take-while #(= name (:name %)) eles)]
          (if (= 1 (count eles))
            (first eles)
            (if (every? element? eles)
              (to-element (vec eles))
              (set eles))))
        (first eles)))))

(defn- select-terms-fn
  [grp]
  (let [sterms (sort (:select-terms grp))]
    (fn [eles]
      (condp = (count sterms)
        0 eles
        1 (distinct (map (first sterms) eles))
        (map #(select-keys (get % nil) sterms) eles)))))

(defn display
  [grp eles]
  ((comp
    (or (merge-terms-fn grp) (first-terms-fn grp) identity)
    (select-terms-fn grp)
    (sort-terms-fn grp)
    (filter-terms-fn grp))
   eles))

;;((comp #(+ 3 %) #(* 8 %)) 0)

