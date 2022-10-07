package vertx;

import io.netty.util.concurrent.MultithreadEventExecutorGroup;
import io.vertx.await.impl.EventLoopScheduler;
import io.vertx.core.Handler;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.core.sync.Vertx;
import io.vertx.core.sync.http.HttpServer;
import io.vertx.core.sync.http.HttpServerRequest;
import io.vertx.core.sync.http.HttpServerResponse;
import io.vertx.pgclient.*;
import io.vertx.sqlclient.PreparedQuery;
import io.vertx.sqlclient.PreparedStatement;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.impl.SqlClientInternal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static io.vertx.await.Async.await;

public class App implements Handler<HttpServerRequest> {

    private static final boolean USE_EVENT_LOOP_SCHEDULER = EventLoopScheduler.isAvailable();

    private static final String PATH_FORTUNES = "/fortunes";
    private static final String PATH_RANDOM_FORTUNE = "/fortunes/random";

    private static final CharSequence RESPONSE_TYPE_JSON = HttpHeaders.createOptimized("application/json");

    private static final CharSequence SERVER = HttpHeaders.createOptimized("vert.x");

    private static final String SELECT_FORTUNE = "SELECT id, message from FORTUNE";

    private final Vertx vertx;
    private final JsonObject config;
    private HttpServer server;
    private final Random random = new Random();
    private SqlClientInternal client;
    private CharSequence dateString;
    private PreparedQuery<RowSet<Row>> SELECT_FORTUNE_QUERY;

    public static CharSequence createDateHeader() {
        return HttpHeaders.createOptimized(DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
    }

    public App(Vertx vertx, JsonObject config) {
        this.vertx = vertx;
        this.config = config;
    }

    public CompletableFuture<Void> start() {
        int port = 8080;
        server = vertx.createHttpServer();
        dateString = createDateHeader();
        PgConnectOptions options = new PgConnectOptions();
        options.setHost(config.getString("host", "localhost"));
        options.setPort(config.getInteger("port", 5432));
        options.setDatabase(config.getString("database", "postgres"));
        options.setUser(config.getString("username", "postgres"));
        options.setPassword(config.getString("password", "postgres"));
        options.setCachePreparedStatements(true);
        options.setPipeliningLimit(100_000); // Large pipelining means less flushing and we use a single connection anyway
        return vertx.submit(() -> {
            server.requestHandler(App.this).listen(port, "0.0.0.0");
            PgConnection conn = await(PgConnection.connect(vertx.unwrap(), options));
            PreparedStatement ps = await(conn.prepare(SELECT_FORTUNE));
            client = (SqlClientInternal) conn;
            SELECT_FORTUNE_QUERY = ps.query();
        });
    }

    @Override
    public void handle(HttpServerRequest request) {
        switch (request.path()) {
            case PATH_FORTUNES:
                handleFortunes(request);
                break;
            case PATH_RANDOM_FORTUNE:
                handleRandomFortune(request);
                break;
            default:
                request.response().statusCode(404);
                request.response().end();
                break;
        }
    }

    public CompletableFuture<Void> stop() {
        return vertx.submit(() -> {
            if (server != null) {
                server.close();
            }
            if (client != null) {
                client.close();
            }
        });
    }

    private void handleFortunes(HttpServerRequest req) {
        HttpServerResponse response = req.response();
        try {
            List<Fortune> fortunes = getFortunes();
            Collections.sort(fortunes);
            response
                .putHeader(HttpHeaders.SERVER, SERVER)
                .putHeader(HttpHeaders.DATE, dateString)
                .putHeader(HttpHeaders.CONTENT_TYPE, RESPONSE_TYPE_JSON)
                .end(fortunes.toString());
        } catch (Exception err) {
            error("", err);
            response.statusCode(500).end(err.getMessage());
        }
    }

    private void handleRandomFortune(HttpServerRequest req) {
        HttpServerResponse response = req.response();
        try {
            List<Fortune> fortunes = getFortunes();
            Fortune fortune = fortunes.get(random.nextInt(fortunes.size()));
            response
                .putHeader(HttpHeaders.SERVER, SERVER)
                .putHeader(HttpHeaders.DATE, dateString)
                .putHeader(HttpHeaders.CONTENT_TYPE, RESPONSE_TYPE_JSON)
                .end(fortune.toString());
        } catch (Exception err) {
            error("", err);
            response.statusCode(500).end(err.getMessage());
        }
    }

    private List<Fortune> getFortunes() {
        RowIterator<Row> resultSet = await(SELECT_FORTUNE_QUERY.execute()).iterator();
        List<Fortune> fortunes = new ArrayList<>();
        while (resultSet.hasNext()) {
            Row row = resultSet.next();
            fortunes.add(new Fortune(row.getInteger(0), row.getString(1)));
        }
        fortunes.add(new Fortune(0, "Additional fortune added at request time."));
        return fortunes;
    }

    public static void main(String[] args) throws Exception {

        int eventLoopPoolSize = VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE;
        String sizeProp = System.getProperty("vertx.eventLoopPoolSize");
        if (sizeProp != null) {
            try {
                eventLoopPoolSize = Integer.parseInt(sizeProp);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        JsonObject config;
        if (args.length > 0) {
            config = new JsonObject(new String(Files.readAllBytes(new File(args[0]).toPath())));;
        } else {
            config = new JsonObject();
        }
        Vertx vertx = new Vertx(new VertxOptions().setEventLoopPoolSize(eventLoopPoolSize).setPreferNativeTransport(true), USE_EVENT_LOOP_SCHEDULER);
        printConfig(vertx);
        List<CompletableFuture<?>> all = new ArrayList<>();
        for (int i = 0; i < eventLoopPoolSize; i++) {
            all.add(vertx.submit(() -> {
                App app = new App(vertx, config);
                return app.start().get(20, TimeUnit.SECONDS);
            }));
        }
        try {
            CompletableFuture.allOf(all.toArray(new CompletableFuture[0])).get(20, TimeUnit.SECONDS);
            info("Server listening on port " + 8080);
        } catch (ExecutionException e) {
            error("Unable to start your application", e.getCause());
        }
        Object o = new Object();
        synchronized (o) {
            o.wait();
        }
    }

    private static void printConfig(Vertx vertx) {
        boolean nativeTransport = vertx.isNativeTransportEnabled();
        String version = "unknown";
        try {
            InputStream in = Vertx.class.getClassLoader().getResourceAsStream("META-INF/vertx/vertx-version.txt");
            if (in == null) {
                in = Vertx.class.getClassLoader().getResourceAsStream("vertx-version.txt");
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[256];
            while (true) {
                int amount = in.read(buffer);
                if (amount == -1) {
                    break;
                }
                out.write(buffer, 0, amount);
            }
            version = out.toString();
        } catch (IOException e) {
            error("Could not read Vertx version", e);
            ;
        }
        info("Vertx: " + version);
        info("Event Loop Size: " + ((MultithreadEventExecutorGroup) vertx.unwrap().nettyEventLoopGroup()).executorCount());
        info("Native transport : " + nativeTransport);
        info("Use event loop scheduler : " + USE_EVENT_LOOP_SCHEDULER);
    }

    private static void info(String msg) {
        System.out.println(msg);
    }

    private static void error(String msg, Throwable err) {
        System.err.println(msg);
        err.printStackTrace();
    }
}