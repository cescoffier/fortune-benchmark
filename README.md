# Fortunes

_the same application, many times, because, well..._

## The application

The application exposes two HTTP endpoints:

- `/fortunes` returns a list of `{id=..., fortune="..."}` objects
- `/fortunes/random` returns a random `{id=..., fortune="..."}`

The _fortunes_ are stored in a PostGreSQL or MariaDB databases (initialized on startup).

## Variants

* `fortune-blocking-postgresql` - use RESTEasy Reactive, with a blocking API, JDBC (postgresql) and Agroal
* `fortune-blocking-mariadb` - use RESTEasy Reactive, with a blocking API, JDBC (mariadb) and Agroal
* `fortune-reactive-postgresql` - use RESTEasy Reactive, with a mutiny API, and the Vert.x PostgreSQL client
* `fortune-reactive-mariadb` - use RESTEasy Reactive, with a mutiny API, and the Vert.x MySQL/MariaDB client
* `fortune-virtual-thread-jdbc-postgresql` - `fortune-blocking-postgresql` with `@RunOnVirtualThread` - will suffer from pinning
* `fortune-virtual-thread-jdbc-mariadb` - `fortune-blocking-mariadb` with `@RunOnVirtualThread`
* `fortune-virtual-thread-postgresql` - use RESTEasy Reactive, with a blocking API (using `@RunOnVirtualThread`), and the
  Vert.x PostgreSQL client - will suffer from the Netty/Loom dance
* `fortune-virtual-thread-mariadb` - use RESTEasy Reactive, with a blocking API (using `@RunOnVirtualThread`), and the
  Vert.x MySQL/MariaDB client - will suffer from the Netty/Loom dance
* `fortune-kotlin-postgresql` - use RESTEasy Reactive with Kotlin co-routines, and the Vert.x PostgreSQL client
* `fortune-kotlin-mariadb` - use RESTEasy Reactive with Kotlin co-routines, and the Vert.x MySQL/MariaDB client
* `fortune-nima-jdbc-postgresql` - use Helidon 4 (nima) with HikariCP and the PostgreSQL JDBC driver
* `fortune-nima-jdbc-mariadb` - use Helidon 4 (nima) with HikariCP and the MariaDB JDBC driver
* [`fortune-virtual-thread-vertx`](fortune-virtual-thread-vertx) - use Vertx with virtual threads, virtual threads are mounted on event loop threads when configured so

## Build

```shell
> mvn clean verify
```

### Build with podman

```shell
# Enable socket
> systemctl --user enable podman.socket --now

# Export env var expected by Testcontainers
> export DOCKER_HOST=unix:///run/user/${UID}/podman/podman.sock
> export TESTCONTAINERS_RYUK_DISABLED=true
```

## Run benchmark with qDup

A [qDup](https://github.com/Hyperfoil/qDup) is provided to run the benchmark with minimal setup required.
Just need to download qDup distribution and run the script. Make sure you have SSH access to HOST.
```shell
# Download qDup uber jar
> wget https://repo1.maven.org/maven2/io/hyperfoil/tools/qDup/0.6.16/qDup-0.6.16-uber.jar

# Run the qDup script
> java -jar qDup-0.6.16-uber.jar -S USER=me -S HOST=somehost qDup.yaml
```
To run it on local host, just use the following command instead.
```shell
# Run qDup in localhost
> java -jar qDup-0.6.16-uber.jar -S USER=$(whoami) qDup.yaml
```

There a number of options available. These are documented in the script.
