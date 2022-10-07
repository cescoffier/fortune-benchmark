package me.escoffier.fortune.kotlin.mariadb

import io.smallrye.mutiny.coroutines.awaitSuspending
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path

@Path("/fortunes")
class FortuneResource {

    @Inject
    @field: Default
    lateinit var repository: FortuneRepository;


    @GET
    suspend fun getAllFortunes(): List<Fortune> {
        return repository.listAll().awaitSuspending();
    }

    @GET
    @Path("/random")
    suspend fun getRandomFortune(): Fortune {
        return repository.randomFortune.awaitSuspending();
    }
}