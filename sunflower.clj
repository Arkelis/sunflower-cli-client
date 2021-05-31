#!/usr/bin/env bb

(ns main
  (:require [babashka.curl :as curl]
            [cheshire.core :as json]
            [clojure.tools.cli :refer [parse-opts]]))


(defn bold [string]
  (str "\033[1m" string "\033[0m"))

(defn fetch-data [channel-id]
  (let [url (str "https://api.radio.pycolore.fr/channels/"
                 channel-id
                 "/current")
        resp (curl/get url)
        data (json/parse-string (resp :body))
        broadcast (data "broadcast")]
    (println ((broadcast "station") "name"))
    (println (bold (broadcast "title")))
    (if-not (nil? (broadcast "show_title")) 
      (println "Emission :" (broadcast "show_title")))
    (if-not (nil? (broadcast "summary")) 
      (println "Résumé :" (broadcast "summary")))))

(def cli-options
   [["-h" "--help" "Show help"]])

(def help "Sunflower Radio Command Line Client
Usage: 
  main.clj [opts] CHANNEL-ENDPOINT
Options:")

((defn main []
  (let [parsed (parse-opts *command-line-args* cli-options)
       args (:arguments parsed)
       opts (:options parsed)
       summary (:summary parsed)]
    (if (or (:help opts) (empty? args))
      (do 
        (println help)
        (println summary))
      (fetch-data (first args))))))

