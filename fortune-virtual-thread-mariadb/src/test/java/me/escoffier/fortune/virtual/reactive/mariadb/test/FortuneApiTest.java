package me.escoffier.fortune.virtual.reactive.mariadb.test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;

import me.escoffier.fortune.virtual.reactive.mariadb.Fortune;
import me.escoffier.loom.loomunit.LoomUnitExtension;
import me.escoffier.loom.loomunit.ShouldNotPin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static io.restassured.RestAssured.get;

@QuarkusTest
@ExtendWith(LoomUnitExtension.class)
public class FortuneApiTest {

    @Test
    @ShouldNotPin
    public void testListAll() {
        var fortunes = get("/fortunes")
                .as(new TypeRef<List<Fortune>>() {
                });

        Assertions.assertEquals(fortunes.size(), 15);
    }

    @Test
    @ShouldNotPin
    public void testRandom() {
        var fortune = get("/fortunes/random")
                .as(Fortune.class);
        Assertions.assertFalse(fortune.getMessage().isEmpty());
    }

}
