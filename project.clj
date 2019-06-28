(defproject guestbook "0.1.0-SNAPSHOT"

  :description "A simple guestbook webapp with Luminus"
  :url "https://guestbook.davidfolkner.com"

  :dependencies [[cheshire "5.8.1"]
                 [clojure.java-time "0.3.2"]
                 [com.h2database/h2 "1.4.197"]
                 [conman "0.8.3"]
                 [cprop "0.1.13"]
                 [funcool/struct "1.3.0"]
                 [luminus-immutant "0.2.5"]
                 [luminus-migrations "0.6.5"]
                 [luminus-transit "0.1.1"]
                 [luminus/ring-ttl-session "0.3.2"]
                 [markdown-clj "1.10.0"]
                 [metosin/muuntaja "0.6.4"]
                 [metosin/reitit "0.3.1"]
                 [metosin/ring-http-response "0.9.1"]
                 [mount "0.1.16"]
                 [nrepl "0.6.0"]
                 [org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/tools.logging "0.4.1"]
                 [org.webjars.npm/bulma "0.7.4"]
                 [org.webjars.npm/material-icons "0.3.0"]
                 [org.webjars/webjars-locator "0.36"]
                 [org.webjars/webjars-locator-jboss-vfs "0.1.0"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.7.1"]
                 [ring/ring-defaults "0.3.2"]
                 [selmer "1.12.12"]
                 [cljs-ajax "0.7.3"]
                 [org.clojure/clojurescript "1.10.238" :scope "provided"]
                 [reagent "0.8.1"]
                 [re-frame "0.10.6"]]

  :min-lein-version "2.0.0"

  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot guestbook.core

  :plugins [[lein-immutant "2.1.0"]
            [lein-cljsbuild "1.1.7"]]
  ; :cljsbuild
  ; {:builds
  ;  {:app {:source-paths ["src/cljs" "src/cljc"]
  ;         :compiler {:output-to "target/cljsbuild/public/js/app.js"
  ;                    :output-dir "target/cljsbuild/public/js/out"
  ;                    :main "guestbook.core"
  ;                    :asset-path "/js/out"
  ;                    :optimizations :none
  ;                    :source-map true
  ;                    :pretty-print true}}}}
  :clean-targets
  ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]


  :profiles
  {:uberjar {:omit-source true
             :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
             :aot :all
             :uberjar-name "guestbook.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]
             :cljsbuild
             {:builds
              {:min {:source-paths
                     ["src/cljs" "src/cljc" "env/prod/cljs"]

                     :compiler
                     {:output-to "target/cljsbuild/public/js/app.js"
                      :output-dir "target/cljsbuild/public/js"
                      :source-map "target/cljsbuild/public/js/app.js.map"
                      :optimizations :advanced
                      :pretty-print false

                      :closure-warnings
                      {:externs-validation :off
                       :non-standard-jsdoc :off}}}}}}
                                ; :closure-defines
                                ; {"re_frame.trace.trace_enabled_QMARK_" true}}}}}}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn"]
                  :dependencies [[binaryage/devtools "0.9.10"]
                                 [expound "0.7.2"]
                                 [pjstadig/humane-test-output "0.9.0"]
                                 [figwheel-sidecar "0.5.18"]
                                 [prone "1.6.3"]
                                 [ring/ring-devel "1.7.1"]
                                 [ring/ring-mock "0.4.0"]
                                 [day8.re-frame/re-frame-10x "0.3.3-react16"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.24.1"]
                                 [lein-figwheel "0.5.18"]]
                  :cljsbuild
                  {:builds
                   {:app {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                          :figwheel {:on-jsload "guestbook.core/mount-components"}
                          :compiler {:output-to "target/cljsbuild/public/js/app.js"
                                     :output-dir "target/cljsbuild/public/js/out"
                                     :main "guestbook.core"
                                     :asset-path "/js/out"
                                     :optimizations :none
                                     :source-map true
                                     :pretty-print true
                                     :closure-defines
                                     {"re_frame.trace.trace_enabled_QMARK_" true}
                                     :preloads [day8.re-frame-10x.preload]}}}}
                  :figwheel
                  {:http-server-root "public"
                   :nrepl-port 7002
                   :css-dirs ["resources/public/css"]}
                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn"]
                  :resource-paths ["env/test/resources"]}
   :profiles/dev {}
   :profiles/test {}})
