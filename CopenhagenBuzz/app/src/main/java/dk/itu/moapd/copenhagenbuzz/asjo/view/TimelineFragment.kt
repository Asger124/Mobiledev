package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.database.FirebaseListOptions
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.asjo.R
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.FragmentTimelineBinding
import dk.itu.moapd.copenhagenbuzz.asjo.model.Event
import dk.itu.moapd.copenhagenbuzz.asjo.viewmodel.DataViewModel
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv


class TimelineFragment : Fragment(R.layout.fragment_timeline) {
    private lateinit var adapter: EventAdapter

    private var _binding: FragmentTimelineBinding? = null


    private lateinit var viewModel: DataViewModel

    val isGuest = FirebaseAuth.getInstance().currentUser?.isAnonymous == true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentTimelineBinding.inflate(inflater,container, false).also {
        _binding = it
    }.root


    private val binding
        get() = requireNotNull(_binding){
            "Cannot access this"
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Firebase.database(dotenv)

            val query = db.reference
                .child("events")
                .orderByChild("StarDate")

            // A class provided by FirebaseUI to make a query in the database to fetch the
            // appropriate data.
            val options = FirebaseListOptions.Builder<Event>()
                .setQuery(query, Event::class.java)
                .setLayout(R.layout.event_row_item)
                .setLifecycleOwner(viewLifecycleOwner)
                .build()

            // Create the custom adapter to bind a list of strings.
            adapter = EventAdapter(options,parentFragmentManager,requireContext(), isGuest)

            view.findViewById<ListView>(R.id.listView).adapter = adapter
            //listView.adapter = adapter

    }
//
//    private fun showEditDialog(event: Event) {
//        // Show your dialog here
//        val dialogFragment = UpdateDataDialogFragment()
//        dialogFragment.show(parentFragmentManager, "editEventDialog")
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}