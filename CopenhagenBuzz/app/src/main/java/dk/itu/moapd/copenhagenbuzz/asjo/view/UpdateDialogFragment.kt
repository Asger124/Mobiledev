package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.provider.CalendarContract.Events
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.FragmentUpdateDialogBinding
import dk.itu.moapd.copenhagenbuzz.asjo.model.Event
import dk.itu.moapd.copenhagenbuzz.asjo.view.AddeventFragment
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
private var selectedStartDate: LocalDate? = null
private var selectedEndDate: LocalDate? = null


class UpdateDataDialogFragment(private val event: Event, private val position: Int,
                               private val adapter: EventAdapter) : DialogFragment() {

    private var _binding: FragmentUpdateDialogBinding? = null

    val uid = Firebase.auth.currentUser?.uid

    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)


        // Inflate the view using view binding.
        _binding = FragmentUpdateDialogBinding.inflate(layoutInflater)
        binding.editTextEventName.setText(event.eventName)
        binding.editTextEventLocation.setText(event.eventLocation)

        binding.editTextEventDate.setOnClickListener {
            DatePickerUtils.pickDateRange(
                requireContext(),
                binding.editTextEventDate
            ) { startDate, endDate ->
                selectedStartDate = startDate
                selectedEndDate = endDate
            }
        }
        binding.editTextEventType.setText(event.eventType)
        binding.editTextEventDescription.setText(event.eventDescription)

        binding.fabAddEvent.setOnClickListener {
            if (binding.editTextEventName.toString().isNotEmpty() &&
                binding.editTextEventLocation.text.toString().isNotEmpty() &&
                binding.editTextEventType.text.toString().isNotEmpty() &&
                binding.editTextEventDescription.text.toString().isNotEmpty() &&
                binding.editTextEventDate.text.toString().isNotEmpty() &&
                selectedStartDate != null && selectedEndDate != null
            ) {
                val updatedEvent = Event(
                    userId = uid,
                    eventName = binding.editTextEventName.text.toString().trim(),
                    eventLocation = binding.editTextEventLocation.text.toString().trim(),
                    startDate = selectedStartDate!!.atStartOfDay(ZoneId.systemDefault()).toInstant()
                        .toEpochMilli(),
                    endDate = selectedEndDate!!.atStartOfDay(ZoneId.systemDefault()).toInstant()
                        .toEpochMilli(),
                    eventType = binding.editTextEventType.text.toString().trim(),
                    eventDescription = binding.editTextEventDescription.text.toString().trim()
                )
                adapter.getRef(position).setValue(updatedEvent)

                val eventKey = adapter.getRef(position).key!!
                val favRef = FirebaseDatabase
                    .getInstance(dotenv)
                    .getReference("favorites")

                favRef.get().addOnSuccessListener { favSnapshot ->
                    for (userSnapshot in favSnapshot.children) {
                        val userId = userSnapshot.key
                        if (userSnapshot.hasChild(eventKey)) {
                            favRef.child(userId!!).child(eventKey).setValue(updatedEvent)
                        }
                    }
                }

//                favRef.get().addOnSuccessListener { snap ->
//                    if (snap.exists()) {
//                        favRef.setValue(updatedEvent)
//                    }
//                }
            }
                dismiss()
            }
            return MaterialAlertDialogBuilder(requireContext())
                .setTitle("Update Event")
                .setView(binding.root)  // << This is your inflated layout
                .create()
        }
}




