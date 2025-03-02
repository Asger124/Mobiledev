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
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.copenhagenbuzz.asjo.R
import dk.itu.moapd.copenhagenbuzz.asjo.model.Event
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.ActivityMainBinding
//import dk.itu.moapd.copenhagenbuzz.asjo.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    //Variables used by class
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(
                R.id.fragment_container_view
            ) as NavHostFragment
        val navController = navHostFragment.navController

        with(binding) {
            setSupportActionBar(binding.topAppBar)
        }

        binding.bottomNavigation.setupWithNavController(navController)
        //binding.bottomNavigationRail.setupWithNavController(navController)


        invalidateOptionsMenu()
        setupMenuListener()

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

        override fun onSupportNavigateUp(): Boolean {
            val navController = findNavController(R.id.fragment_container_view)
            return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        }



        private fun setupMenuListener() {
            binding.topAppBar.setOnMenuItemClickListener { menuItem ->
                onOptionsItemSelected(menuItem)
            }

        }

    }
