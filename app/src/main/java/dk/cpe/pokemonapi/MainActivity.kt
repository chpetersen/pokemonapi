package dk.cpe.pokemonapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dk.cpe.pokemonapi.ui.detail.PokemonDetailFragment
import dk.cpe.pokemonapi.ui.list.ListOfPokemonsFragment
import dk.cpe.pokemonapi.ui.SharedViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val LIST_OF_POKEMON_FRAGMENT_TAG = "list_of_pokemon_fragment_tag"
    private val POKEMON_DETAIL_FRAGMENT_TAG = "pokemon_detail_fragment_tag"

    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    ListOfPokemonsFragment.newInstance(),
                    LIST_OF_POKEMON_FRAGMENT_TAG
                )
                .commitNow()
        }
    }

    override fun onResume() {
        super.onResume()
        sharedViewModel.streamOfViewModelEvents.observe(this) { event ->
            when (event) {
                is SharedViewModel.ViewModelEvents.OnPokemonSelected -> {
                    if (supportFragmentManager.findFragmentByTag(POKEMON_DETAIL_FRAGMENT_TAG) == null) {
                        showDetailFragment(event)
                    }
                }
            }
        }
    }

    private fun showDetailFragment(event: SharedViewModel.ViewModelEvents.OnPokemonSelected) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                PokemonDetailFragment.newInstance(event.id),
                POKEMON_DETAIL_FRAGMENT_TAG
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.findFragmentByTag(POKEMON_DETAIL_FRAGMENT_TAG) != null) {
            supportFragmentManager.popBackStack()
        }
    }
}