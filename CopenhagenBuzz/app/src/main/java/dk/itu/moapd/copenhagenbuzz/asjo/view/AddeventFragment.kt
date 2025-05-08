package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
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
    private var selectedStartDate: LocalDate? = null
    private var selectedEndDate: LocalDate? = null
    private var _binding: FragmentAddeventBinding? = null
    private var photoUri: Uri? = null
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>


    val auth = FirebaseAuth.getInstance()
    val database = Firebase.database(dotenv).reference

    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access this"
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAddeventBinding.inflate(inflater, container, false).also {

        _binding = it

        setUpEventAttr()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.buttonOpenCamera.setOnClickListener {
            if (checkPermission()) {
                showImageSourceDialog()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)

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
//        val resolver = requireContext().contentResolver
//        val contentValues = ContentValues().apply {
//            put(MediaStore.Images.Media.DISPLAY_NAME, "event_${System.currentTimeMillis()}.jpg")
//            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
//        }
//
//        photoUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
//            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
//        }
//
//        cameraLauncher.launch(intent)

        photoUri = ImageUtils.createImageUri(requireContext())
        val intent = ImageUtils.buildCameraIntent(photoUri!!)
        cameraLauncher.launch(intent)
    }

//    private fun UploadImage(uri: Uri, onSuccess: (String) -> Unit) {
//        val filename = "events/${System.currentTimeMillis()}.jpg"
//        val storageRef = Firebase.storage.reference.child(filename)
//
//        storageRef.putFile(uri)
//            .addOnSuccessListener {
//                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
//                    onSuccess(downloadUri.toString())
//                }
//            }
//            .addOnFailureListener {
//                Log.e("Upload", "Upload failed", it)
//            }
//    }



    private fun setUpEventAttr() {

        eventName = binding.editTextEventName
        eventLocation = binding.editTextEventLocation
        addEventButton = binding.fabAddEvent
        eventDate = binding.editTextEventDate
        eventType = binding.editTextEventType
        eventDescription = binding.editTextEventDescription

        eventDate.setOnClickListener {
            DatePickerUtils.pickDateRange(requireContext(), eventDate) { startDate, endDate ->
                selectedStartDate = startDate
                selectedEndDate = endDate
            }
        }



        addEventButton.setOnClickListener {

            if (eventName.text.toString().isNotEmpty() &&
                eventLocation.text.toString().isNotEmpty() &&
                eventType.text.toString().isNotEmpty() &&
                eventDate.text.toString().isNotEmpty() &&
                eventDescription.text.toString().isNotEmpty() &&
                selectedStartDate != null && selectedEndDate != null
            ) {

            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val address = geocoder.getFromLocationName(eventLocation.text.toString(), 1)


            if (!address.isNullOrEmpty()) {
                val lat = address[0].latitude
                val long = address[0].longitude
                val street = address[0].getAddressLine(0)


                // Only execute the following code when the user fills all
                // `EditText `.

                    val uri = photoUri
                    if (uri != null) {
                        val uploadingDialog = showLoadingDialog()
                        FirebaseUtils.uploadImage(uri) { downloadref ->
                            val newEvent = Event(
                                userId = auth.currentUser!!.uid,
                                eventName = eventName.text.toString().trim(),
                                eventLocation = EventLocation(street, lat, long),
                                eventPhotoURL = downloadref,
                                startDate = selectedStartDate!!.atStartOfDay(ZoneId.systemDefault())
                                    .toInstant().toEpochMilli(),
                                endDate = selectedEndDate!!.atStartOfDay(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli(),
                                eventType = eventType.text.toString().trim(),
                                eventDescription = eventDescription.text.toString().trim()
                            )
                            auth.currentUser?.let { _ ->
                                database.child("events")
                                    .push()
                                    .setValue(newEvent)
                                    .addOnCompleteListener {
                                        uploadingDialog.dismiss()
                                        showMessage(binding.root,newEvent)
                                    }
                            }
                        }
                    } else {
                        Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
                    }
                } else {
                Toast.makeText(context, "Failed to fetch location coordinates", Toast.LENGTH_SHORT)
                    .show()
                }

            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()

            }

        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}