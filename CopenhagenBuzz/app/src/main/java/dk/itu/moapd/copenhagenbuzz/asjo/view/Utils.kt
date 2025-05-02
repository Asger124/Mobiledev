package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.widget.EditText
import androidx.core.content.edit
import dk.itu.moapd.copenhagenbuzz.asjo.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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

    /**
     * Return the timestamp as a date `String`.
     *
     * @return A formatted date range string in the format "E, MMM dd yyyy - E, MMM dd yyyy".
     */
    fun Long.toSimpleDateFormat(): String {
        val dateFormat = SimpleDateFormat("E, MMM dd yyyy hh:mm:ss a", Locale.US)
        return dateFormat.format(this)
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
