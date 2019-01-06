# Duct server.figwheel

[Integrant][] methods for compiling and dynamically reloading
ClojureScript files in the [Duct][] framework using [Figwheel][].
Figwheel is designed to be used during development. For compiling
ClojureScript for a production release, use [compiler.cljs][].

[integrant]:     https://github.com/weavejester/integrant
[duct]:          https://github.com/duct-framework/duct
[figwheel]:      https://github.com/bhauman/lein-figwheel
[compiler.cljs]: https://github.com/duct-framework/compiler.cljs

## Installation

To install, add the following to your project `:dependencies`:

    [duct/server.figwheel "0.3.0"]

## Usage

This library provides the `:duct.server/figwheel` key, and accepts the
same options as Figwheel.

```edn
{:duct.server/figwheel
 {:css-dirs ["dev/resources"]
  :builds   [{:id :dev
              :source-paths  ["src"]
              :build-options {:output-to "target/js/public/main.js"
                              :output-dir "target/js/public"
                              :optimizations :none}}]}}
```

See the [Figwheel README][] for more information.

[figwheel readme]: https://github.com/bhauman/lein-figwheel/blob/master/README.md

## License

Copyright © 2017 James Reeves

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
