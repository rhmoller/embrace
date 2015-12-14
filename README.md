# embrace
Embrace native web APIs from GWT by generating JSInterop stubs from WebIDL definitions.

The project consists of three sub-projects:

* tools - contains command line tools for converting WebIDL to JsInterop source files.
* webapis - a GWT module containing JSInterop stubs for a selection of HTML5 APIs (similar to Elemental)
* examples - small examples of what you can do with the project currently

## Taking it for a spin

```
./gradlew :webapis:jar
./gradlew :examples:gwtc
./gradlew :examples:run
```

and point your browser to http://localhost:8080/com.giddyplanet.embrace.examples.Examples/

## Project Philosophy

The GWT team is working on a similar tool and Elemental 2. 

I started this project because I was impatient and could not wait for Elemental 2.
This project is all about quickly getting to a usable level that solves most cases,
but it does not need to be perfect or complete.

Therefore the project has adopted some unusual conventions:

* good enough is better than perfect
* features are valued more than code quality
* ignore corner cases that would slow down progress
* it is perfectly fine to cut corners and commit code sins to get things rolling

It is a hobby project that I make for fun in my spare time. Suggestions, bug reports and pull request
will be much appreciated!

## Status

The project handles most WebIDL definition types and produces usable code. It has some rough edges 
and the code is a mess. Noticeable missing features are:

* typedef - will currently be represented as Object
* dictionary - will currently be represented as Object
* union - will currently be represented as Object
* promise - will currently be represented as Object
* exception - ignored
* numeric types may not get the best suitable Java representation
* optional primitives are not represented as boxed types in Java

## License

This project is released as open source under the MIT License
