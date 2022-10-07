package me.escoffier.fortune.nima.jdbc.mariadb;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class FortuneRepository {

    private static final Logger logger = LogManager.getLogManager().getLogger(FortuneRepository.class.getName());

    private final HikariDataSource datasource;
    private final Random random;

    public FortuneRepository(String jdbc, String username, String pwd) {
        var url = Optional.ofNullable(jdbc).orElse("jdbc:mariadb://localhost/fortune");

        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(pwd);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        this.datasource = new HikariDataSource(hikariConfig);
        this.random = new Random();
        prepare();
    }

    private void prepare() {
        logger.info("Preparing application...");
        try (var connection = datasource.getConnection()) {
            connection.setAutoCommit(false);
            try (var createStatement = connection.createStatement()) {
                createStatement.executeUpdate(Statements.CREATE_TABLE);
                if (!Statements.isFortunesTableEmpty(connection)) {
                    try (var statement = connection.prepareStatement(Statements.INSERT_FORTUNE)) {
                        for (var i = 0; i < 15; i++) {
                            statement.setInt(1, i);
                            statement.setString(2, "fortune-" + i);
                            statement.executeUpdate();
                        }
                    }
                }
            }
            connection.commit();
            logger.info("Application ready");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Unable to prepare application", e);
        }
    }

    public Fortune getRandomFortune() {
        var fortunes = listAll();
        var rnd = random.nextInt(fortunes.size());
        return fortunes.get(rnd);
    }

    public List<Fortune> listAll() {
        List<Fortune> list = new ArrayList<>();
        try (var connection = datasource.getConnection()) {
            try (var statement = connection.createStatement()) {
                try (var resultSet = statement.executeQuery(Statements.SELECT_ALL_FORTUNES)) {
                    while (resultSet.next()) {
                        var id = resultSet.getInt(1);
                        var msg = resultSet.getString(2);
                        list.add(new Fortune(id, msg));
                    }
                    return list;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
