package me.escoffier.fortune.blocking.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Statements {

    public static String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS fortunes (
              id int,
              fortune varchar(255),
              PRIMARY KEY (id)
            )""";

    public static String INSERT_FORTUNE = "INSERT INTO fortunes (id, fortune) VALUES  (?, ?);";

    public static String SELECT_ALL_FORTUNES = "SELECT * FROM fortunes";

    public static boolean isFortunesTableEmpty(Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT count(*) FROM fortunes;")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) != 0;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
