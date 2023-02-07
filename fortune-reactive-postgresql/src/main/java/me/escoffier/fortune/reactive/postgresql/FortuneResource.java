package me.escoffier.fortune.reactive.postgresql;

import io.smallrye.mutiny.Uni;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.List;

@Path("/fortunes")
public class FortuneResource {


    private final FortuneRepository repository;

    public FortuneResource(FortuneRepository repository) {
        this.repository = repository;
    }

    @GET
    public Uni<List<Fortune>> getAllFortunes() {
        return this.repository.listAll();
    }

    @GET
    @Path("/random")
    public Uni<Fortune> getRandomFortune() {
        return this.repository.getRandomFortune();
    }

}
