package dk.cpe.pokemonapi.network

import dk.cpe.pokemonapi.model.ApiResultList
import dk.cpe.pokemonapi.model.Pokemon
import retrofit2.http.*

interface PokemonApi {

    @GET("pokemon")
    suspend fun getPokemons(@Query("limit") limit: Int, @Query("offset") offset: Int): ApiResultList

    @GET
    suspend fun getPokemon(@Url url: String): Pokemon
}