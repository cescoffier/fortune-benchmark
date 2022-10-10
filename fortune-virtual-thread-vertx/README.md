## Vert.x virtual thread fortunes benchmark

1. build the fat jar: mvn package
2. run it with the path to the json config:
   1. `java -jar --enable-preview -jar target/fortune-postgres-fat.jar [/path/to/config.json]`
   2`java -jar --enable-preview -jar target/fortune-mariadb-fat.jar [/path/to/config.json]`

With `config.json`:

```json
{
  "host": "localhost",
  "port": 5432,
  "database": "postgres",
  "username": "postgres",
  "password": "postgres"
}
```

or

```json
{
  "host": "localhost",
  "port": 3306,
  "database": "mariadb",
  "username": "mariadb",
  "password": "mariadb"
}
```

When `config.json` is omitted, the above configuration are used instead

When available (add `--add-opens=java.base/java.lang=ALL-UNNAMED` JVM options), the benchmark will use an event-loop scheduler, vertx virtual threads will be mounted on their own event-loop.

## Note to self

### Starting a Postgres container

`docker run --rm -it --name postgres-fortunes -p 5432:5432 -e POSTGRES_PASSWORD=postgres postgres`

### Starting a MariaDB container

`docker run --rm -it --name mariadb-fortunes -p 3306:3306 -e MARIADB_ROOT_PASSWORD=mariadb -e MARIADB_USER=mariadb -e MARIADB_PASSWORD=mariadb -e MARIADB_DATABASE=mariadb mariadb`