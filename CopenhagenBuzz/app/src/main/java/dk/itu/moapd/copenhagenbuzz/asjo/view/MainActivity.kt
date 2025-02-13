package dk.itu.moapd.copenhagenbuzz.asjo.view
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.core.view.WindowCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.copenhagenbuzz.asjo.model.Event
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.ActivityMainBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
         private val TAG = MainActivity::class.qualifiedName
         }

    private lateinit var eventName: EditText
    private lateinit var eventLocation: EditText
    private lateinit var addEventButton: FloatingActionButton
    private lateinit var eventType : EditText
    private lateinit var eventDate: EditText
    private lateinit var eventDescription: EditText


    private val event: Event = Event("",
                                    "",
                                    Pair(LocalDate.now(),LocalDate.now()),
                                    "",
                                    "")

    private var selectedStartDate: LocalDate? = null
    private var selectedEndDate: LocalDate? = null
    private val dateFormatter:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

     override fun onCreate(savedInstanceState: Bundle?) {
         WindowCompat.setDecorFitsSystemWindows(window , false)
         super.onCreate(savedInstanceState)
         binding = ActivityMainBinding.inflate(layoutInflater)
         setContentView(binding.root)

         eventName = binding.contentMain.editTextEventName
         eventLocation = binding.contentMain.editTextEventLocation
         addEventButton = binding.contentMain.fabAddEvent
         eventDate = binding.contentMain.editTextEventDate
         eventType = binding.contentMain.editTextEventType
         eventDescription = binding.contentMain.editTextEventDescription
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
              eventDescription.text.toString().isNotEmpty())
                   {

                  // Create event object attributes.d
                      event.eventName = eventName.text.toString().trim()
                      event.eventLocation = eventLocation.text.toString().trim()
                      event.eventDateRange = Pair(selectedStartDate!!,selectedEndDate!!)
                      event.eventType = eventType.text.toString().trim()
                      event.eventDescription = eventDescription.text.toString().trim()

                  // Show Snackbar(information about event when added)
                  showMessage()
              }
         }
     }

    private fun pickDateRange(editText: EditText) {
        val calendar = Calendar.getInstance()


        DatePickerDialog(this, { _, startYear, startMonth, startDay ->
            selectedStartDate = LocalDate.of(startYear, startMonth + 1, startDay)


            DatePickerDialog(this, { _, endYear, endMonth, endDay ->
                selectedEndDate = LocalDate.of(endYear, endMonth + 1, endDay)

                editText.setText("${dateFormatter.format(selectedStartDate)}, - ${dateFormatter.format(selectedEndDate)}")
            }, startYear, startMonth, startDay).show() // Default date: Start Date
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }


    private fun showMessage() {
        val msg = ("Event added using\n$event")
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
        Log.d(TAG,event.toString())

     }


}