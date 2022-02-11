package dk.cpe.pokemonapi.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import dk.cpe.pokemonapi.R
import dk.cpe.pokemonapi.model.Pokemon
import dk.cpe.pokemonapi.ui.OnPokemonClickCallback
import dk.cpe.pokemonapi.ui.list.PokemonAdapter.ViewHolderItem

private const val POKEMON_ITEM_VIEW_TYPE = 0
private const val LOADING_ITEM_VIEW_TYPE = 1

class PokemonAdapter : RecyclerView.Adapter<ViewHolderItem>() {

    private var listOfPokemons: List<PokemonListItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItem {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewHolder: ViewHolderItem = when (viewType) {
            POKEMON_ITEM_VIEW_TYPE -> {
                val view = layoutInflater.inflate(R.layout.pokemon_item, parent, false)
                ViewHolderItem.PokemonItemVH(view)
            }
            LOADING_ITEM_VIEW_TYPE -> {
                val view = layoutInflater.inflate(R.layout.loading_item, parent, false)
                ViewHolderItem.LoadingItemVH(view)
            }
            else -> {
                val view = layoutInflater.inflate(R.layout.pokemon_item, parent, false)
                ViewHolderItem.PokemonItemVH(view)
            }
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolderItem, position: Int) {
        when (val listItem = listOfPokemons[position]) {
            is PokemonListItem.LoadingItem -> {
                listItem.bind(holder as ViewHolderItem.LoadingItemVH)
            }
            is PokemonListItem.PokemonItem -> {
                listItem.bind(holder as ViewHolderItem.PokemonItemVH)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (listOfPokemons[position]) {
            is PokemonListItem.PokemonItem -> POKEMON_ITEM_VIEW_TYPE
            is PokemonListItem.LoadingItem -> LOADING_ITEM_VIEW_TYPE
        }
    }

    override fun getItemCount() = listOfPokemons.size

    fun submitList(listOfPokemonItems: List<PokemonListItem>) {
        val diffInCount = listOfPokemonItems.size - listOfPokemons.size
        val lastIndex = listOfPokemonItems.lastIndex
        listOfPokemons = listOfPokemonItems
        notifyItemRangeInserted(lastIndex, diffInCount)
    }

    fun toggleLoading() {
        val listWithLoadingItem = listOfPokemons.toMutableList()
        listWithLoadingItem.add(PokemonListItem.LoadingItem())
        listOfPokemons = listWithLoadingItem
        notifyItemInserted(listOfPokemons.lastIndex)
    }

    open class ViewHolderItem(val view: View) : RecyclerView.ViewHolder(view) {
        class PokemonItemVH(view: View) : ViewHolderItem(view) {
            val pokemonImage: ImageView? = view.findViewById(R.id.pokemon_image)
            val pokemonNumber: TextView? = view.findViewById(R.id.pokemon_number)
            val pokemonName: TextView? = view.findViewById(R.id.pokemon_name)
        }

        class LoadingItemVH(view: View) : ViewHolderItem(view)
    }
}

sealed class PokemonListItem {

    class PokemonItem(private val pokemon: Pokemon, val callback: OnPokemonClickCallback) :
        PokemonListItem() {

        fun bind(viewHolder: ViewHolderItem.PokemonItemVH) {
            viewHolder.pokemonNumber?.text = "# ${pokemon.id}"
            viewHolder.pokemonName?.text = pokemon.nameTitleCase()
            viewHolder.pokemonImage?.load(pokemon.findBestArtWork())
            viewHolder.view.setOnClickListener { callback.onPokemonSelected(pokemon) }
        }
    }

    class LoadingItem : PokemonListItem() {
        fun bind(viewHolder: ViewHolderItem.LoadingItemVH) {

        }
    }
}
