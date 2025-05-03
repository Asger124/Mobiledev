package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.asjo.R
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.FragmentAddeventBinding
import dk.itu.moapd.copenhagenbuzz.asjo.model.Event
import dk.itu.moapd.copenhagenbuzz.asjo.model.EventLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


class AddeventFragment : Fragment() {

    private lateinit var eventName: EditText
    private lateinit var eventLocation: EditText
    private lateinit var addEventButton: FloatingActionButton
    private lateinit var eventType: EditText
    private lateinit var eventDate: EditText
    private lateinit var eventDescription: EditText
   // private val event: Event = Event("", "","", 0L,0L, "", "")
    private var selectedStartDate: LocalDate? = null
    private var selectedEndDate: LocalDate? = null
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private var _binding: FragmentAddeventBinding? = null

        val auth = FirebaseAuth.getInstance()
        val database = Firebase.database(dotenv).reference



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
            DatePickerUtils.pickDateRange(requireContext(),eventDate) {startDate, endDate ->
                selectedStartDate = startDate
                selectedEndDate = endDate
            }
        }

        addEventButton.setOnClickListener {

            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val address = geocoder.getFromLocationName(eventLocation.text.toString(),1)


            if(!address.isNullOrEmpty()) {
                val lat = address[0].latitude
                val long = address[0].longitude
                val street = address[0].getAddressLine(0)


                // Only execute the following code when the user fills all
                // `EditText `.
                if (eventName.text.toString().isNotEmpty() &&
                    eventLocation.text.toString().isNotEmpty() &&
                    eventType.text.toString().isNotEmpty() &&
                    eventDate.text.toString().isNotEmpty() &&
                    eventDescription.text.toString().isNotEmpty() &&
                    selectedStartDate != null && selectedEndDate != null
                ) {
                    val newEvent = Event(
                        userId = auth.currentUser!!.uid,
                        eventName = eventName.text.toString().trim(),
                        eventLocation = EventLocation(street,lat,long),
                        startDate = selectedStartDate!!.atStartOfDay(ZoneId.systemDefault())
                            .toInstant().toEpochMilli(),
                        endDate = selectedEndDate!!.atStartOfDay(ZoneId.systemDefault()).toInstant()
                            .toEpochMilli(),
                        eventType = eventType.text.toString().trim(),
                        eventDescription = eventDescription.text.toString().trim()
                    )

                    auth.currentUser?.let { _ ->
                        database.child("events")
                            .push()
                            .setValue(newEvent)
                    }

                    showMessage(newEvent)


                }
            } else {
                Toast.makeText(context, "Failed to fetch location coordinates", Toast.LENGTH_SHORT)
                    .show()
            }

        }

    }



//     fun pickDateRange(editText: EditText) {
//        val calendar = Calendar.getInstance()
//
//        DatePickerDialog(
//            requireContext(),
//            { _, startYear, startMonth, startDay ->
//                selectedStartDate = LocalDate.of(startYear, startMonth + 1, startDay)
//
//                DatePickerDialog(requireContext(), { _, endYear, endMonth, endDay ->
//                    selectedEndDate = LocalDate.of(endYear, endMonth + 1, endDay)
//
//                    editText.setText("${dateFormatter.format(selectedStartDate)}, - ${dateFormatter.format(selectedEndDate)}")
//                }, startYear, startMonth, startDay).show() // Default date: Start Date
//            },
//            calendar.get(Calendar.YEAR),
//            calendar.get(Calendar.MONTH),
//            calendar.get(Calendar.DAY_OF_MONTH)
//        ).show()
//    }

    private fun showMessage(event:Event) {
        val msg = ("Event added using\n$event")
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
        Log.d("Event:", event.toString())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}