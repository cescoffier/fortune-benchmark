package me.escoffier.fortune.virtual.jdbc;

import io.smallrye.common.annotation.RunOnVirtualThread;

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
