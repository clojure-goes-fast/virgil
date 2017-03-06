(ns lein-virgil.plugin
  (:require
   [leiningen.core.eval :as eval]))

(def overwrites
  '[[virgil "0.1.6"]
    [org.ow2.asm/asm "5.1"]
    [org.clojure/tools.namespace "0.2.11"]])

(def injections
  `((require 'virgil)
    (virgil/watch ~@(:java-source-paths project))))

(defn overwrite-dependencies [deps overwrites]
  (let [project->dep (zipmap (map first deps) deps)]
    (->> dependencies
      (reduce
        (fn [m [p v]] (assoc m p [p v]))
        project->dep)
      vals
      vec)))

(defn middleware [project]
  (if (contains? project :java-source-paths)
    (-> project
      (update-in [:dependencies] overwrite-dependencies overwrites)
      (update-in [:injections] concat injections))
    project))
