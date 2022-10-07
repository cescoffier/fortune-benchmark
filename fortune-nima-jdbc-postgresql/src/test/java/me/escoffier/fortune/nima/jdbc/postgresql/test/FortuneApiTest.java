package me.escoffier.fortune.nima.jdbc.postgresql.test;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import me.escoffier.fortune.nima.jdbc.postgresql.Fortune;
import me.escoffier.fortune.nima.jdbc.postgresql.Main;
import me.escoffier.loom.loomunit.LoomUnitExtension;
import me.escoffier.loom.loomunit.ShouldPin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static io.restassured.RestAssured.get;

@ExtendWith(LoomUnitExtension.class)
public class FortuneApiTest {

    private static PostgreSQLContainer<?> container;

    @BeforeAll
    public static void start() {
        container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14.5"));
        container.start();
        String jdbc = container.getJdbcUrl();
        int port = Main.start(jdbc, "test", "test");
        RestAssured.port = port;
    }

    @AfterAll
    public static void stop() {
        RestAssured.reset();
        container.stop();
    }

    @Test
    public void testListAll() {
        var fortunes = get("/fortunes")
                .as(new TypeRef<List<Fortune>>() {
                });

        Assertions.assertEquals(fortunes.size(), 15);
    }

    @Test
    public void testRandom() {
        var fortune = get("/fortunes/random")
                .as(Fortune.class);
        Assertions.assertFalse(fortune.getMessage().isEmpty());
    }
}
