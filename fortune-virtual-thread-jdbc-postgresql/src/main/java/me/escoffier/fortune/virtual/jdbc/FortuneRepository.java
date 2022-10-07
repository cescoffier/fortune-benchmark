package me.escoffier.fortune.virtual.jdbc;

import io.agroal.api.AgroalDataSource;
import io.quarkus.runtime.Startup;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ApplicationScoped
@Startup
public class FortuneRepository {


    private final AgroalDataSource datasource;
    private final Logger logger;
    private final Random random;

    public FortuneRepository(AgroalDataSource datasource, Logger logger) {
        this.datasource = datasource;
        this.logger = logger;
        this.random = new Random();
        prepare();
    }


    private void prepare() {
        logger.infof("Preparing application...");
        try (Connection connection = datasource.getConnection()) {
            connection.setAutoCommit(false);
            connection.createStatement().executeUpdate(Statements.CREATE_TABLE);
            if (!Statements.isFortunesTableEmpty(connection)) {
                PreparedStatement statement = connection.prepareStatement(Statements.INSERT_FORTUNE);
                for (int i = 0; i < 15; i++) {
                    statement.setInt(1, i);
                    statement.setString(2, "fortune-" + i);
                    statement.executeUpdate();
                }
            }
            connection.commit();
            logger.infof("Application ready");
        } catch (SQLException e) {
            logger.error("Unable to prepare application", e);
        }
    }

    public Fortune getRandomFortune() {
        var fortunes = listAll();
        var rnd = random.nextInt(fortunes.size());
        return fortunes.get(rnd);
    }

    public List<Fortune> listAll() {
        List<Fortune> list = new ArrayList<>();
        try (Connection connection = datasource.getConnection()) {
            var resultSet = connection.createStatement().executeQuery(Statements.SELECT_ALL_FORTUNES);
            while (resultSet.next()) {
                var id = resultSet.getInt(1);
                var msg = resultSet.getString(2);
                list.add(new Fortune(id, msg));
            }
            return list;
        } catch (SQLException e) {
            logger.error("Unable to prepare application", e);
            throw new RuntimeException(e);
        }
    }


}
