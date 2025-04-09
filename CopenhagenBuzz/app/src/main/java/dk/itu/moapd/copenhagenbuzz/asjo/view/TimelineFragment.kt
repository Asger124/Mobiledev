package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import dk.itu.moapd.copenhagenbuzz.asjo.R
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.FragmentTimelineBinding
import dk.itu.moapd.copenhagenbuzz.asjo.viewmodel.DataViewModel


class TimelineFragment : Fragment() {

    private var _binding: FragmentTimelineBinding? = null

    private lateinit var viewModel: DataViewModel



    private val binding
        get() = requireNotNull(_binding){
            "Cannot access this"
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentTimelineBinding.inflate(inflater,container, false).also {
        _binding = it
    }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[DataViewModel::class.java]
        val listView = view.findViewById<ListView>(R.id.listView)

        val adapter = EventAdapter(requireContext(),R.layout.event_row_item, mutableListOf())

        viewModel.events.observe(viewLifecycleOwner) {eventList ->

            adapter.addAll(eventList)
        }

        listView.adapter = adapter

        }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}