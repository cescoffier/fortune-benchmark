package me.escoffier.fortune.reactive;

import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ApplicationScoped
@Startup
public class FortuneRepository {


    private final MySQLPool datasource;
    private final Logger logger;
    private final Random random;

    public FortuneRepository(MySQLPool datasource, Logger logger) {
        this.datasource = datasource;
        this.logger = logger;
        this.random = new Random();
        prepare();
    }


    private void prepare() {
        logger.infof("Preparing application...");
        SqlConnection connection = datasource.getConnection().await().indefinitely();
        connection.query(Statements.CREATE_TABLE).executeAndAwait();
        if (Statements.isFortunesTableEmpty(connection)) {
            for (int i = 0; i < 15; i++) {
                connection.preparedQuery(Statements.INSERT_FORTUNE).executeAndAwait(Tuple.of(i, "fortune-" + i));
            }
        }
        connection.closeAndAwait();
        logger.infof("Application ready");
    }

    public Uni<Fortune> getRandomFortune() {
        return listAll().map(
                list -> {
                    var rnd = random.nextInt(list.size());
                    return list.get(rnd);
                }
        );
    }

    public Uni<List<Fortune>> listAll() {
        List<Fortune> list = new ArrayList<>();
        return datasource.getConnection().flatMap(connection ->
                connection.query(Statements.SELECT_ALL_FORTUNES).execute()
                        .map(rs -> {
                            for (Row row : rs) {
                                list.add(new Fortune(row.getInteger(0), row.getString(1)));
                            }
                            return list;
                        })
                        .onTermination().call(connection::close)
        );
    }


}
