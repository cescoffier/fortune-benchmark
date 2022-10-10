package vertx;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.sync.Vertx;
import io.vertx.core.sync.http.HttpClient;
import io.vertx.core.sync.http.HttpClientResponse;
import me.escoffier.loom.loomunit.LoomUnitExtension;
import me.escoffier.loom.loomunit.ShouldNotPin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@ExtendWith(LoomUnitExtension.class)
public class FortuneApiPostgreSQLTest {

    @Container
    private GenericContainer postgresqlContainer = new PostgreSQLContainer()
        .withDatabaseName("foo")
        .withUsername("foo")
        .withPassword("secret");

    private Vertx vertx;
    private PgApp app;

    @BeforeEach
    public void before() throws Exception {
        vertx = new Vertx();
        app = new PgApp(vertx, new JsonObject()
            .put("host", postgresqlContainer.getHost())
            .put("port", postgresqlContainer.getMappedPort(5432))
            .put("database", ((JdbcDatabaseContainer)postgresqlContainer).getDatabaseName())
            .put("username", ((JdbcDatabaseContainer)postgresqlContainer).getUsername())
            .put("password", ((JdbcDatabaseContainer)postgresqlContainer).getPassword()), true
        );
        app.start().get(20, TimeUnit.SECONDS);
    }

    @AfterEach
    public void after() throws Exception {
        app.stop().get(20, TimeUnit.SECONDS);
        vertx.close();
    }

    @ShouldNotPin
    @Test
    public void testListAll() throws Exception {
        vertx.submit(() -> {
            HttpClient client = vertx.createHttpClient();
            HttpClientResponse response = client.request(8080, "localhost", "GET", "/fortunes").send();
            assertEquals(200, response.statusCode());
            JsonArray fortunes = response.body().toJsonArray();
            assertEquals(15,  fortunes.size());
        }).get(20, TimeUnit.SECONDS);
    }

    @ShouldNotPin
    @Test
    public void testRandom() throws Exception {
        vertx.submit(() -> {
            HttpClient client = vertx.createHttpClient();
            HttpClientResponse response = client.request(8080, "localhost", "GET", "/fortunes/random").send();
            assertEquals(200, response.statusCode());
            JsonObject fortune = response.body().toJsonObject();
            assertNotNull(fortune.getString("message"));
        }).get(20, TimeUnit.SECONDS);
    }

}
