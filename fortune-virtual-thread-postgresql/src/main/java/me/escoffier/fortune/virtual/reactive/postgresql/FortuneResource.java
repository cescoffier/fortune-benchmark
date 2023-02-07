package me.escoffier.fortune.virtual.reactive.postgresql;

import io.smallrye.common.annotation.RunOnVirtualThread;

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
    @RunOnVirtualThread
    public List<Fortune> getAllFortunes() {
        return this.repository.listAll();
    }

    @GET
    @Path("/random")
    @RunOnVirtualThread
    public Fortune getRandomFortune() {
        return this.repository.getRandomFortune();
    }

}
