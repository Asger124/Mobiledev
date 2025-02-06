package dk.itu.moapd.copenhagenbuzz.asjo

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.core.view.WindowCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
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


    private val event: Event = Event("", "","", "", "")

     override fun onCreate(savedInstanceState: Bundle?) {
         WindowCompat.setDecorFitsSystemWindows(window , false)
         super.onCreate(savedInstanceState)
         binding = ActivityMainBinding.inflate(layoutInflater)
         setContentView(binding.root)

         eventName = findViewById(R.id.edit_text_event_name)
         eventLocation = findViewById(R.id.edit_text_event_location)
         addEventButton = findViewById(R.id.fab_add_event)
         eventDate = findViewById(R.id.edit_text_event_date)
         eventType = findViewById(R.id.edit_text_event_type)
         eventDescription = findViewById(R.id.edit_text_event_description)

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

                  // Update the object attributes.
                  event.setEventName(
                      eventName.text.toString().trim()
                  )
                  event.setEventLocation(
                      eventLocation.text.toString().trim()
                  )

                  event.setEventDate(
                      eventDate.text.toString().trim()
                  )

                  event.setEventType(
                      eventType.text.toString().trim()
                  )

                  event.setEventDescription(
                      eventDescription.text.toString().trim()
                  )


                  // Write in the `Logcat ` system.
                  showMessage()
              }
         }
     }

    private fun pickDateRange(editText: EditText) {
        val calendar = Calendar.getInstance()


        DatePickerDialog(this, { _, startYear, startMonth, startDay ->
            calendar.set(startYear, startMonth, startDay)
            val startDate = calendar.time


            DatePickerDialog(this, { _, endYear, endMonth, endDay ->
                calendar.set(endYear, endMonth, endDay)
                val endDate = calendar.time


                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                editText.setText("${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}")
            }, startYear, startMonth, startDay).show() // Default date: Start Date
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }


    private fun showMessage() {
        Log.d(TAG, event.toString())
     }


}