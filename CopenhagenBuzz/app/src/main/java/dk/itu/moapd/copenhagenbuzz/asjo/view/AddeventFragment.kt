package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.copenhagenbuzz.asjo.R
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.FragmentAddeventBinding
import dk.itu.moapd.copenhagenbuzz.asjo.model.Event
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AddeventFragment : Fragment() {

    private lateinit var eventName: EditText
    private lateinit var eventLocation: EditText
    private lateinit var addEventButton: FloatingActionButton
    private lateinit var eventType: EditText
    private lateinit var eventDate: EditText
    private lateinit var eventDescription: EditText
    private val event: Event = Event("", "", Pair(LocalDate.now(), LocalDate.now()), "", "")
    private var selectedStartDate: LocalDate? = null
    private var selectedEndDate: LocalDate? = null
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private var _binding: FragmentAddeventBinding? = null

    private val binding
        get() = requireNotNull(_binding){
            "Cannot access this"
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAddeventBinding.inflate(inflater,container, false).also {
        _binding = it

        setUpEventAttr()
    }.root



    private fun setUpEventAttr() {

        eventName = binding.editTextEventName
        eventLocation = binding.editTextEventLocation
        addEventButton = binding.fabAddEvent
        eventDate = binding.editTextEventDate
        eventType = binding.editTextEventType
        eventDescription = binding.editTextEventDescription

        eventDate.setOnClickListener {
            pickDateRange(eventDate)
        }

        addEventButton.setOnClickListener {

            // Only execute the following code when the user fills all
            // `EditText `.
            if (eventName.text.toString().isNotEmpty() &&
                eventLocation.text.toString().isNotEmpty() &&
                eventType.text.toString().isNotEmpty() &&
                eventDate.text.toString().isNotEmpty() &&
                eventDescription.text.toString().isNotEmpty()
            )
            {

                // Create event object attributes.d
                event.eventName = eventName.text.toString().trim()
                event.eventLocation = eventLocation.text.toString().trim()
                event.eventDateRange = Pair(selectedStartDate!!, selectedEndDate!!)
                event.eventType = eventType.text.toString().trim()
                event.eventDescription = eventDescription.text.toString().trim()

                // Show Snackbar(information about event when added)
                showMessage()
            }

        }

    }

    private fun pickDateRange(editText: EditText) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, startYear, startMonth, startDay ->
                selectedStartDate = LocalDate.of(startYear, startMonth + 1, startDay)

                DatePickerDialog(requireContext(), { _, endYear, endMonth, endDay ->
                    selectedEndDate = LocalDate.of(endYear, endMonth + 1, endDay)

                    editText.setText("${dateFormatter.format(selectedStartDate)}, - ${dateFormatter.format(selectedEndDate)}"
                    )
                }, startYear, startMonth, startDay).show() // Default date: Start Date
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showMessage() {
        val msg = ("Event added using\n$event")
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
        Log.d("Event:", event.toString())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}