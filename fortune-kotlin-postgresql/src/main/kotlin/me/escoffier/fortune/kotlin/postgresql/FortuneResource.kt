package me.escoffier.fortune.kotlin.postgresql

import io.smallrye.mutiny.coroutines.awaitSuspending
import me.escoffier.fortune.kotlin.postgresql.Fortune
import me.escoffier.fortune.kotlin.postgresql.FortuneRepository
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path

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