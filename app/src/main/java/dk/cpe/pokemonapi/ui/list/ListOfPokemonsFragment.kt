package dk.cpe.pokemonapi.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dk.cpe.pokemonapi.R
import dk.cpe.pokemonapi.ui.SharedViewModel

@AndroidEntryPoint
class ListOfPokemonsFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var recyclerView: RecyclerView? = null
    private val pokemonAdapter = PokemonAdapter()

    private var loading = true
    private var pastVisiblesItems = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.pokemon_list)
        recyclerView?.adapter = pokemonAdapter

        setupEndlessScrolling()
        sharedViewModel.pokemonsLiveData.observe(viewLifecycleOwner) { pokemons ->
            loading = false
            pokemonAdapter.submitList(pokemons)
        }

        sharedViewModel.streamOfViewModelEvents.observe(viewLifecycleOwner) { event ->
            when (event) {
                is SharedViewModel.ViewModelEvents.FetchingPokemons -> {
                    pokemonAdapter.toggleLoading()
                }
            }
        }
    }

    /**
     * The optimal solution here is to use the paging library, but since I haven't tried to use that library before,
     * I wasn't comfortable including it in this case.
     */
    private fun setupEndlessScrolling() {
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (dy > 0) { //check for scroll down
                    visibleItemCount = layoutManager.childCount
                    totalItemCount = layoutManager.itemCount
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()
                    if (!loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            loading = true
                            sharedViewModel.fetchPokemons()
                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

    }

    companion object {
        fun newInstance() = ListOfPokemonsFragment()
    }
}