(ns text-stream-analysis.core
  (:use [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [twitter.api.streaming])
  (:require [clojure.data.json :as json]
            [http.async.client :as ac]
            [clojure.string :as str]
            [text-stream-analysis.creds :as creds]
            [taoensso.carmine :as car :refer (wcar)])
  (:import [twitter.callbacks.protocols AsyncStreamingCallback]))

(def redis-conn {:pool {} :spec {:host "127.0.0.1" :port 6379}}) ; See `wcar` docstring for opts
(defmacro wcar* [& body] `(car/wcar redis-conn ~@body))

(def my-creds (apply make-oauth-creds creds/creds))

(defn get-all[]
  (let [keys (wcar* (car/keys "*"))]
    (map
      (fn [k]
        [k (Integer/parseInt (wcar* (car/get k)))])
     keys)))

;(doseq [pair (sort-by second (get-all))]
;  (println pair))

(defn inc-word [word]
  (let [c (wcar* (car/incr word))]
    (println (str word " " c))))

(defn process-tweet [s]
  (let [words (str/split s #" ")]
    (doseq [word words]
      (inc-word word))))

(defn is-all-ascii [s]
  (and
    (> (count s) 15)
    (re-matches #"\A\p{ASCII}*\z" s)))

(defn onbp [a b]
  (try
    (if-let [status (:text(json/read-json (str b)))]
      (cond (is-all-ascii status) (process-tweet status)))
    (catch Exception e (str "caught exception: " (.getMessage e)))))

(def ^:dynamic *custom-streaming-callback*
  (AsyncStreamingCallback. onbp
                           (comp println response-return-everything)
                           exception-print))

; retrieves the user stream
(def ^:dynamic *response*
  (statuses-sample :oauth-creds my-creds
                   :callbacks *custom-streaming-callback*))

(Thread/sleep 60000000)
