package dk.itu.moapd.copenhagenbuzz.asjo.view
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.copenhagenbuzz.asjo.R
import dk.itu.moapd.copenhagenbuzz.asjo.model.Event
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.ActivityMainBinding
//import dk.itu.moapd.copenhagenbuzz.asjo.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

//    private val viewModel: MainViewModel by lazy {
//        ViewModelProvider(this)[MainViewModel::class.java]
//    }

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
         with(binding) {

             setSupportActionBar(topAppBar)
         }

         setContentView(binding.root)
         invalidateOptionsMenu()
         setupMenuListener()

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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d("create", "Create called")
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        Log.d("MenuDebug", "onPrepareOptionsMenu called")

        val isLoggedIn = intent.getBooleanExtra("isLoggedIn", false)
        Log.d("MenuDebug", "isLoggedIn: $isLoggedIn")

        //Set up menuItem(Login and logout) components.
        //Based in the value of isLoggedIn, set which component should be visible
        menu.findItem(R.id.login).isVisible =
        intent.getBooleanExtra("isLoggedIn", false)

        //If user is logged out
        menu.findItem(R.id.logout).isVisible =
        !intent.getBooleanExtra("isLoggedIn", false)
        return true
    }

    //Controls what actions are performed when a User clicks on the menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.login, R.id.logout -> {
                // Start LoginActivity when Login and Logout components are clicked.
                val intent = Intent(baseContext, LoginActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupMenuListener() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            onOptionsItemSelected(menuItem)
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

