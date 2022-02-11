package dk.cpe.pokemonapi.ui

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.cpe.pokemonapi.model.Pokemon
import dk.cpe.pokemonapi.network.PokemonRepository
import dk.cpe.pokemonapi.ui.list.PokemonListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import javax.inject.Inject

interface OnPokemonClickCallback {
    fun onPokemonSelected(pokemon: Pokemon)
}

@HiltViewModel
class SharedViewModel @Inject constructor(private val pokemonRepository: PokemonRepository) :
    ViewModel(), OnPokemonClickCallback {

    private val viewModelStateEmitter = MutableLiveData<ViewModelEvents>()
    val streamOfViewModelEvents: LiveData<ViewModelEvents> = viewModelStateEmitter

    val pokemonsLiveData = pokemonRepository
        .flowOfPokemons
        .map { listOfPokemons ->
            listOfPokemons.map { pokemon ->
                PokemonListItem.PokemonItem(
                    pokemon,
                    this
                )
            }
        }
        .asLiveData()

    init {
        // For now, this will always be empty on first start.
        // But if we had a database, this would not be the case.
        if (pokemonRepository.flowOfPokemons.value.isEmpty()) {
            fetchPokemons()
        }
    }

    fun fetchPokemons() {
        viewModelScope.launch {
            emitEvent(ViewModelEvents.FetchingPokemons)
            pokemonRepository.fetchPokemons()
        }
    }

    override fun onPokemonSelected(pokemon: Pokemon) {
        emitEvent(ViewModelEvents.OnPokemonSelected(pokemon.id))
    }

    private fun emitEvent(selected: ViewModelEvents) {
        viewModelStateEmitter.postValue(selected)
    }

    fun getPokemonForId(id: Int): Pokemon {
        return pokemonRepository.getPokemon(id)
    }

    sealed class ViewModelEvents {
        class OnPokemonSelected(val id: Int) : ViewModelEvents()
        object FetchingPokemons : ViewModelEvents()
    }

}