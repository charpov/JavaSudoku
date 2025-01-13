# Functional Programming With (or Without) Streams: A Case Study

This case study starts as an attempt to use Java streams to implement a common functional programming pattern (lazy evaluation + memoization).
Due to a limitation of stream iterators (https://bugs.openjdk.org/browse/JDK-8267359) some of the code bypasses streams and is implemented directly in terms of iterators.
It was used for a talk to the *Boston Java Users ACM Chapter*.

Note that this code requires Java 23 or newer due to a stream bug (https://bugs.openjdk.org/browse/JDK-8196106) in earlier versions.
