package dk.cpe.pokemonapi.network

import dk.cpe.pokemonapi.model.Pokemon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton



/**
 * Simple Repository which will hold a in-memory cache of all the pokemons we have fetched.
 * The repository will expose a [StateFlow] which will emit a list of all the pokemons we have in memory, each time we fetch some new ones.
 */
@Singleton
class PokemonRepository @Inject constructor(private val pokemonApi: PokemonApi) {

    private val DEFAULT_AMOUNT_OF_POKEMONS_TO_FETCH = 25

    private val flowEmitter: MutableStateFlow<List<Pokemon>> = MutableStateFlow(emptyList())
    // This flow act as a in-memory cache of our pokemons. Could be a database instead if we wanted.
    val flowOfPokemons: StateFlow<List<Pokemon>> = flowEmitter.asStateFlow()

    private var lastOffSet = 0

    suspend fun fetchPokemons() {
        val pokemonsFromApi = getPokemonsFromApi(offset = lastOffSet)
        flowEmitter.emit(flowEmitter.value + pokemonsFromApi)
    }

    private suspend fun getPokemonsFromApi(
        limit: Int = DEFAULT_AMOUNT_OF_POKEMONS_TO_FETCH,
        offset: Int
    ): List<Pokemon> =
        withContext(Dispatchers.IO) {
            val listOfPokemons = pokemonApi.getPokemons(limit, offset)

            val list = listOfPokemons.results.map { pokemonResult ->
                async {
                    // For each pokemon we find in the output, we will download the details for that pokemon.
                    // TODO: Handle if the fetching fails.
                    val pokemon = pokemonApi.getPokemon(pokemonResult.url)
                    pokemon
                }
            }
            // A more pretty way was to use the "next" coming from the api.
            lastOffSet += limit
            list.awaitAll()
        }

    /**
     * Lookup the pokemon in our in-memory cache for the given [id]
     */
    fun getPokemon(id: Int): Pokemon {
        return flowOfPokemons.value.first { pokemon -> pokemon.id == id }
    }
}