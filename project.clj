(defproject text-stream-analysis "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [twitter-api "0.7.9"]
                 [com.taoensso/carmine "2.15.1"]]



  :main ^:skip-aot text-stream-analysis.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
