package me.escoffier.fortune.blocking;

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
    public List<Fortune> getAllFortunes() {
        return this.repository.listAll();
    }

    @GET
    @Path("/random")
    public Fortune getRandomFortune() {
        return this.repository.getRandomFortune();
    }

}
