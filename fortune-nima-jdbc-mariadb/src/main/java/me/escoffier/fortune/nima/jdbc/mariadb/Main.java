package me.escoffier.fortune.nima.jdbc.mariadb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.helidon.common.LogConfig;
import io.helidon.common.http.Http;
import io.helidon.config.Config;
import io.helidon.nima.webserver.WebServer;
import io.helidon.nima.webserver.http.HttpRules;

public class Main {


    private static String jdbc;
    private static String username;
    private static String pwd;

    public static void main(String[] args) {
        start(args[0], args[1], args[2]);
    }

    public static int start(String jdbc, String username, String pwd) {
        Main.jdbc = jdbc;
        Main.username = username;
        Main.pwd = pwd;

        WebServer server = WebServer.builder()
                .routing(Main::routing)
                .start();
        return server.port();
    }

    public static void routing(HttpRules rules) {
        FortuneRepository repository = new FortuneRepository(jdbc, username, pwd);
        ObjectMapper mapper = new ObjectMapper();

        rules.get("/fortunes", (serverRequest, serverResponse) -> {
                    try {
                        serverResponse.header(Http.Header.CONTENT_TYPE, "application/json;charset=utf=8");
                        var content = mapper.writeValueAsBytes(repository.listAll());
                        serverResponse.header(Http.Header.CONTENT_LENGTH, Integer.toString(content.length));
                        serverResponse.send(content);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .get("/fortunes/random", (serverRequest, serverResponse) -> {
                    try {
                        serverResponse.header(Http.Header.CONTENT_TYPE, "application/json;charset=utf=8");
                        var content = mapper.writeValueAsBytes(repository.getRandomFortune());
                        serverResponse.header(Http.Header.CONTENT_LENGTH, Integer.toString(content.length));
                        serverResponse.send(content);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
