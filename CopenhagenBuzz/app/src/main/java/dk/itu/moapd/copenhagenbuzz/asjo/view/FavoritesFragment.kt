package dk.itu.moapd.copenhagenbuzz.asjo.view
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.FragmentFavoritesBinding
import dk.itu.moapd.copenhagenbuzz.asjo.model.Event


class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null


    private  lateinit var adapter: FavoriteAdapter

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
        val uid   = FirebaseAuth.getInstance().currentUser!!.uid
        val query = FirebaseDatabase.getInstance(dotenv)
            .getReference("favorites")
            .child(uid)

        val options = FirebaseRecyclerOptions.Builder<Event>()
            .setQuery(query, Event::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()

        adapter = FavoriteAdapter(options)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        adapter.startListening()
    }

        override fun onDestroyView() {
        super.onDestroyView()
            adapter.stopListening()
        _binding = null
    }

}
