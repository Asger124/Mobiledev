package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
    private var photoUri: Uri? = null
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && photoUri != null) {
                binding.imageEventPhoto.setImageURI(photoUri)
                binding.imageEventPhoto.visibility = View.VISIBLE
            }
        }

        binding.buttonOpenCamera.setOnClickListener {
            launchCamera()
        }
    }


    private fun launchCamera() {
        val resolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "event_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        photoUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }

        cameraLauncher.launch(intent)
    }




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
        val msg = ("Event added\n${event.eventName}")
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
        Log.d("Event:", event.toString())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}