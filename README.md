# Fortunes

_the same application, many times, because, well..._

## The application

The application exposes two HTTP endpoints:

- `/fortunes` returns a list of `{id=..., fortune="..."}` objects
- `/fortunes/random` returns a random `{id=..., fortune="..."}`

The _fortunes_ are stored in a MariaDB database (initialized on startup).

## Variants

* `fortune-blocking` - use RESTEasy Reactive, with a blocking API, JDBC and Agroal
* `fortune-reactive` - use RESTEasy Reactive, with a mutiny API, and the Vert.x MySQL client (compatible with mariadb)
* `fortune-virtual-thread-jdbc` - `fortune-blocking` with `@RunOnVirtualThread` - will suffer from pinning
* `fortune-virtual-thread-mariadb` - use RESTEasy Reactive, with a blocking API (using `@RunOnVirtualThread`), and the
  Vert.x MySQL (compatible with mariadb) client - will suffer from the Netty/Loom dance
* `fortune-kotlin` - use RESTEasy Reactive with Kotlin co-routines, and the Vert.x MySQL client

## Build

```shell
> mvn clean verify
```

