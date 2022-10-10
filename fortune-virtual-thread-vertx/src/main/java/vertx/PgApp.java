package vertx;

import io.vertx.core.json.JsonObject;
import io.vertx.core.sync.Vertx;
import io.vertx.pgclient.*;
import io.vertx.sqlclient.SqlConnectOptions;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;

import static io.vertx.await.Async.await;

public class PgApp extends AppBase {

    public PgApp(Vertx vertx, JsonObject config, boolean initDb) {
        super(vertx, config, initDb);
    }

    protected SqlConnectOptions connectOptions(JsonObject config) {
        PgConnectOptions options = new PgConnectOptions();
        options.setHost(config.getString("host", "localhost"));
        options.setPort(config.getInteger("port", 5432));
        options.setDatabase(config.getString("database", "postgres"));
        options.setUser(config.getString("username", "postgres"));
        options.setPassword(config.getString("password", "postgres"));
        options.setCachePreparedStatements(true);
        options.setPipeliningLimit(100_000); // Large pipelining means less flushing and we use a single connection anyway
        return options;
    }


    @Override
    protected void initDb(SqlConnection conn) {
        await(conn.query("DROP TABLE IF EXISTS fortunes").execute());
        await(conn.query("""
            CREATE TABLE fortunes (
               id integer NOT NULL,
               fortune varchar(255) NOT NULL,
               PRIMARY KEY  (id)
               );
            """).execute());
        for (int i = 0;i < 15;i++) {
            await(conn.preparedQuery("INSERT INTO fortunes (id, fortune) VALUES  ($1, $2)").execute(Tuple.of(i, "fortune-" + i)));
        }
    }

    public static void main(String[] args) throws Exception {
        AppBase.main(args, (vertx, config, initDb) -> new PgApp(vertx, config, initDb));
    }
}