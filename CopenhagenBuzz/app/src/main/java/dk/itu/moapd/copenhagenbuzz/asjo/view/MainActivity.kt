/*
MIT License

Copyright (c) [2025] [Asger Jørgensen]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 */

package dk.itu.moapd.copenhagenbuzz.asjo.view
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.copenhagenbuzz.asjo.R
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.ActivityMainBinding


/**
 * An Activity class which serves as the primary activity after login.
 *  It sets up navigation components, manages the top app bar menu,
 *  and handles user authentication state.
 *  This activity hosts fragments managed by the FragmentManager.
 */

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var appBarConfiguration: AppBarConfiguration

    /**
     * View binding instance for accessing UI elements.
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * Called when the activity is starting. Sets up the navigation host, app bar, and bottom navigation.
     * @param savedInstanceState Contains data from a previously saved instance, if available.
     */
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


        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val isGuest = FirebaseAuth.getInstance().currentUser?.isAnonymous == true
            val guestRestrictedDestinations = setOf(
                R.id.fragment_addevent,
                R.id.fragment_favorites
            )

            return@setOnItemSelectedListener when {
                isGuest && item.itemId in guestRestrictedDestinations -> {
                    Toast.makeText(this, "Guests can't access this section", Toast.LENGTH_SHORT)
                        .show()
                    true
                }

                else -> {
                    navController.navigate(item.itemId)
                    true
                }
            }
        }


        auth = FirebaseAuth.getInstance()

        invalidateOptionsMenu()
        setupMenuListener()

    }

    override fun onStart() {
        super.onStart()

        // Redirect the user to the LoginActivity if they are not logged in.
        auth.currentUser ?: startLoginActivity()
    }

    private fun startLoginActivity() {
        Intent(this, LoginActivity::class.java).apply {
            // An alternative to instead of calling finish() method.
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
    }

    /**
     * Inflates the menu for the top app bar.
     * @param menu The menu to be inflated.
     * @return Boolean indicating success.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d("create", "Create called")
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    /**
     * Handles menu item selections.
     * @param item The selected menu item.
     * @return Boolean indicating whether the event was handled.
     */

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        // Handle top app bar menu item clicks.
        R.id.account -> {
            UserInfoDialogFragment().apply {
                isCancelable = false
            }.also { dialogFragment ->
                dialogFragment.show(supportFragmentManager, "UserInfoDialogFragment")
            }
            true
        }

        R.id.logout -> {
            auth.signOut()
            startLoginActivity()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    /**
     * Prepares the options menu before displaying it, based on the user's login state.
     * @param menu The menu to be modified.
     * @return Boolean indicating success.
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {

        val user = auth.currentUser
        //Set up menuItem(Login and logout) components.
        //Based in the value of isLoggedIn, set which component should be visible
        if (user != null) {
            if (user.isAnonymous) {
                menu.findItem(R.id.account).isVisible = false
            }
        }
        return true
    }

    /**
     * Sets up a menu listener for item clicks in the menu.
     * Calls onOptionsSelected when an item is clicked.
     */
    private fun setupMenuListener() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            onOptionsItemSelected(menuItem)
        }

    }
}



