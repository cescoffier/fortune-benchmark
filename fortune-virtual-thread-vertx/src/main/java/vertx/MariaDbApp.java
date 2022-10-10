package vertx;

import io.vertx.core.json.JsonObject;
import io.vertx.core.sync.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.SqlConnectOptions;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;

import static io.vertx.await.Async.await;

public class MariaDbApp extends AppBase {

    public MariaDbApp(Vertx vertx, JsonObject config, boolean initDb) {
        super(vertx, config, initDb);
    }

    protected SqlConnectOptions connectOptions(JsonObject config) {
        MySQLConnectOptions options = new MySQLConnectOptions();
        options.setHost(config.getString("host", "localhost"));
        options.setPort(config.getInteger("port", 3306));
        options.setDatabase(config.getString("database", "mariadb"));
        options.setUser(config.getString("username", "mariadb"));
        options.setPassword(config.getString("password", "mariadb"));
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
            await(conn.preparedQuery("INSERT INTO fortunes (id, fortune) VALUES  (?, ?)").execute(Tuple.of(i, "fortune-" + i)));
        }
    }

    public static void main(String[] args) throws Exception {
        AppBase.main(args, (vertx, config, initDb) -> new MariaDbApp(vertx, config, initDb));
    }
}