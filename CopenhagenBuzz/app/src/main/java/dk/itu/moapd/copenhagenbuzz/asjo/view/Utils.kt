package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.widget.EditText
import androidx.core.content.ContentProviderCompat.requireContext
import dk.itu.moapd.copenhagenbuzz.asjo.model.Event
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface OnItemClickListener  {

    /**
     * Implement this method to be executed when the user press an item in the `RecyclerView` for a
     * long time.
     *
     * @param dummy An instance of `Dummy` class.
     * @param position The selected position in the `RecyclerView`.
     */
    fun onItemClickListener(event: Event, position: Int)

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
