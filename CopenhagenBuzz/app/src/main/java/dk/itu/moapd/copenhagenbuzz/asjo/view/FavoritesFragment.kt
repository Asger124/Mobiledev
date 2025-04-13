package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.copenhagenbuzz.asjo.R
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.FragmentFavoritesBinding
import dk.itu.moapd.copenhagenbuzz.asjo.model.Event
import dk.itu.moapd.copenhagenbuzz.asjo.viewmodel.DataViewModel


class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null

    private lateinit var viewModel: DataViewModel




    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access this"
        }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFavoritesBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            viewModel = ViewModelProvider(requireActivity())[DataViewModel::class.java]
            // Define the recycler view layout manager and adapter.
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            viewModel.favorites.observe(viewLifecycleOwner) { fav ->
                val adapter = FavoriteAdapter(ArrayList(fav))
                recyclerView.adapter = adapter
            }

        }

    }


        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
