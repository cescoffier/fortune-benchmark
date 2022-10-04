package me.escoffier.fortune;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import me.escoffier.fortune.virtual.reactive.Fortune;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.get;

@QuarkusTest
public class FortuneApiTest {

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
