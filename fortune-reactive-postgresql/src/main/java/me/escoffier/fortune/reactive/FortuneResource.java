package me.escoffier.fortune.reactive;

import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
