package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.copenhagenbuzz.asjo.R
import dk.itu.moapd.copenhagenbuzz.asjo.model.Event
import java.time.LocalDate
import java.time.format.DateTimeFormatter



internal object SharedPreferenceUtil {

    /**
     * The name of the SharedPreferences variable.
     */
    const val KEY_FOREGROUND_ENABLED = "tracking_foreground_location"

    /**
     * Stores the location updates state in SharedPreferences.
     *
     * @param requestingLocationUpdates The location updates state.
     */
    fun saveLocationTrackingPref(context: Context, requestingLocationUpdates: Boolean) =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit {
            putBoolean(KEY_FOREGROUND_ENABLED, requestingLocationUpdates)
        }


}

object DatePickerUtils {

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun pickDateRange(
        context: Context,
        editText: EditText,
        onDatesSelected: (startDate: LocalDate, endDate: LocalDate) -> Unit
    ) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            context,
            { _, startYear, startMonth, startDay ->
                val selectedStartDate = LocalDate.of(startYear, startMonth + 1, startDay)

                DatePickerDialog(context, { _, endYear, endMonth, endDay ->
                    val selectedEndDate = LocalDate.of(endYear, endMonth + 1, endDay)

                    // Update the EditText
                    editText.setText(
                        "${dateFormatter.format(selectedStartDate)} - ${dateFormatter.format(selectedEndDate)}"
                    )

                    // Callback if you need the dates elsewhere
                    onDatesSelected(selectedStartDate, selectedEndDate)

                }, startYear, startMonth, startDay).show()

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}

object FirebaseUtils {
    fun uploadImage(uri: Uri, onSuccess: (String) -> Unit) {
        val filename = "events/${System.currentTimeMillis()}.jpg"
        val storageRef = Firebase.storage.reference.child(filename)

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }
            }
            .addOnFailureListener {
                Log.e("Upload", "Upload failed", it)
            }
    }
}

object ImageUtils {

    fun createImageUri(context: Context): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "event_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    fun buildCameraIntent(photoUri: Uri): Intent {
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }
    }

    fun buildGalleryIntent(): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }

}

fun Fragment.showLoadingDialog(): AlertDialog {
    val composeView = ComposeView(requireContext()).apply {
        setContent {
            MaterialTheme {
                UploadingDialog()
            }
        }
    }
    val dialog = MaterialAlertDialogBuilder(requireContext())
        .setView(composeView)
        .setCancelable(false)
        .create()
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    dialog.show()
    return dialog
}


fun showMessage(view: View,event: Event) {
    val msg = ("Event added\n${event.eventName}")
    Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    Log.d("Event:", event.toString())

}





