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
import androidx.core.view.WindowCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import dk.itu.moapd.copenhagenbuzz.asjo.R
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.ActivityMainBinding


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
