# Fortunes

_the same application, many times, because, well..._

## The application

The application exposes two HTTP endpoints:

- `/fortunes` returns a list of `{id=..., fortune="..."}` objects
- `/fortunes/random` returns a random `{id=..., fortune="..."}`

The _fortunes_ are stored in a PostGreSQL database (initialized on startup).

## Variants

* `fortune-blocking-postgresql` - use RESTEasy Reactive, with a blocking API, JDBC (postgresql) and Agroal
* `fortune-reactive-postgresql` - use RESTEasy Reactive, with a mutiny API, and the Vert.x PostgreSQL client
* `fortune-virtual-thread-jdbc-postgresql` - `fortune-blocking-(postgresql)` with `@RunOnVirtualThread` - will suffer from pinning
* `fortune-virtual-thread-postgresql` - use RESTEasy Reactive, with a blocking API (using `@RunOnVirtualThread`), and the
  Vert.x PostgreSQL client - will suffer from the Netty/Loom dance
* `fortune-kotlin-postgresql` - use RESTEasy Reactive with Kotlin co-routines, and the Vert.x PostgreSQL client

## Build

```shell
> mvn clean verify
```

