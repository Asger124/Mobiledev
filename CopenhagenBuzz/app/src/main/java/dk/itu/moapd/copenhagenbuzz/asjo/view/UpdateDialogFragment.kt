package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.FragmentUpdateDialogBinding
import dk.itu.moapd.copenhagenbuzz.asjo.model.Event
import dk.itu.moapd.copenhagenbuzz.asjo.model.EventLocation
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

private var selectedStartDate: LocalDate? = null
private var selectedEndDate: LocalDate? = null
private var photoUri: Uri? = null
private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>


class UpdateDataDialogFragment(private val event: Event, private val position: Int,
                               private val adapter: EventAdapter) : DialogFragment() {

    private var _binding: FragmentUpdateDialogBinding? = null

    val uid = Firebase.auth.currentUser?.uid

    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && photoUri != null) {
                    binding.imageEventPhoto.setImageURI(photoUri)
                    binding.imageEventPhoto.visibility = View.VISIBLE
                }
            }
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val selectedImageUri = result.data!!.data
                    if (selectedImageUri != null) {
                        photoUri = selectedImageUri
                        binding.imageEventPhoto.setImageURI(photoUri)
                        binding.imageEventPhoto.visibility = View.VISIBLE
                    }
                }
            }
    }

    private fun launchImagePicker() {
        val intent = ImageUtils.buildGalleryIntent()
        imagePickerLauncher.launch(intent)
    }

    private fun showImageSourceDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Photo")
            .setItems(arrayOf("Take Photo", "Choose from Gallery")) { _, which ->
                when (which) {
                    0 -> launchCamera()
                    1 -> launchImagePicker()
                }
            }
            .show()
    }

    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        cameraPermissionResult(result)
    }

    private fun cameraPermissionResult(isGranted: Boolean) {
        // Use the takeIf function to conditionally execute code based on the permission result
        isGranted.takeIf { it }?.run {
            showImageSourceDialog()
        } ?: MaterialAlertDialogBuilder(requireContext())
            .setTitle("CAMERA PERMISSION")
            .setMessage("Camera permission is needed to create an event!\nChange permissions in the settings for the application on your device")
            .show()
    }

    private fun launchCamera() {
        photoUri = ImageUtils.createImageUri(requireContext())
        val intent = ImageUtils.buildCameraIntent(photoUri!!)
        cameraLauncher.launch(intent)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        photoUri = null
        _binding = FragmentUpdateDialogBinding.inflate(layoutInflater)

        binding.buttonOpenCamera.setOnClickListener {
            if (checkPermission()) {
                showImageSourceDialog()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)

            }
        }
        binding.editTextEventName.setText(event.eventName)
        binding.editTextEventLocation.setText(event.eventLocation?.address)

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
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val address =
                    geocoder.getFromLocationName(binding.editTextEventLocation.text.toString(), 1)


                if (!address.isNullOrEmpty()) {
                    val lat = address[0].latitude
                    val long = address[0].longitude
                    val street = address[0].getAddressLine(0)


                    val uri = photoUri
                    if (uri != null) {
                        val uploadingDialog = showLoadingDialog()
                        FirebaseUtils.uploadImage(uri) { downloadref ->
                            val updatedEvent = Event(
                                userId = uid,
                                eventName = binding.editTextEventName.text.toString().trim(),
                                eventLocation = EventLocation(street, lat, long),
                                eventPhotoURL = downloadref,
                                startDate = selectedStartDate!!.atStartOfDay(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli(),
                                endDate = selectedEndDate!!.atStartOfDay(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli(),
                                eventType = binding.editTextEventType.text.toString().trim(),
                                eventDescription = binding.editTextEventDescription.text.toString()
                                    .trim()
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
                                        favRef.child(userId!!).child(eventKey)
                                            .setValue(updatedEvent)

                                    }
                                }
                            }.addOnCompleteListener {
                                uploadingDialog.dismiss()
                                dismiss()
                            }
                        }

                    } else {
                        Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Failed to fetch location coordinates",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()

            }
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Update Event")
            .setView(binding.root)
            .create()
    }

}




