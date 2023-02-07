package me.escoffier.fortune.virtual.reactive.mariadb;

import io.vertx.mutiny.sqlclient.SqlConnection;

public class Statements {

    public static String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS fortunes (
              id int,
              fortune varchar(255),
              PRIMARY KEY (id)
            )""";

    public static String INSERT_FORTUNE = "INSERT INTO fortunes (id, fortune) VALUES  (?, ?);";

    public static String SELECT_ALL_FORTUNES = "SELECT * FROM fortunes";

    public static boolean isFortunesTableEmpty(SqlConnection connection) {
        var rows = connection.query("SELECT * FROM fortunes;").executeAndAwait();
        return rows.size() == 0;
    }

}
