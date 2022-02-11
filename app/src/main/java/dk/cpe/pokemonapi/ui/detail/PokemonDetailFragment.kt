package dk.cpe.pokemonapi.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import coil.size.Scale
import dagger.hilt.android.AndroidEntryPoint
import dk.cpe.pokemonapi.R
import dk.cpe.pokemonapi.model.Pokemon
import dk.cpe.pokemonapi.ui.SharedViewModel

@AndroidEntryPoint
class PokemonDetailFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var pokemonImageIV: ImageView? = null
    private var backButton: ImageView? = null
    private var pokemonNameTV: TextView? = null
    private var pokemonNumberTV: TextView? = null
    private var pokemonTypeTV: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pokemon_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pokemonImageIV = view.findViewById(R.id.pokemon_detail_image)
        pokemonNameTV = view.findViewById(R.id.pokemon_detail_name)
        pokemonNumberTV = view.findViewById(R.id.pokemon_detail_number)
        pokemonTypeTV = view.findViewById(R.id.pokemon_detail_type)
        backButton = view.findViewById(R.id.back_button)

        if (arguments != null && requireArguments().containsKey(POKEMON_DETAIL_ID_ARGS)) {
            val pokemonId = requireArguments().getInt(POKEMON_DETAIL_ID_ARGS)
            val pokemon = sharedViewModel.getPokemonForId(pokemonId)
            setupPokemonDetailView(pokemon)
        }

        backButton?.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun setupPokemonDetailView(pokemon: Pokemon) {
        pokemonImageIV?.load(pokemon.findBestArtWork()) {
            scale(Scale.FILL)
        }
        pokemonNameTV?.text = "Name: ${pokemon.nameTitleCase()}"
        pokemonNumberTV?.text = "Number: #${pokemon.id}"

        val types = pokemon.types.joinToString(separator = " / ") { type ->
            type.type.name.replaceFirstChar { it.titlecase() }
        }

        pokemonTypeTV?.text = "Type(s): $types"
    }


    companion object {

        const val POKEMON_DETAIL_ID_ARGS = "pokemon_detail_id_args"

        fun newInstance(id: Int): PokemonDetailFragment {
            return PokemonDetailFragment().apply {
                arguments = bundleOf(
                    POKEMON_DETAIL_ID_ARGS to id
                )
            }
        }
    }
}