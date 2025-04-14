package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.copenhagenbuzz.asjo.R

import dk.itu.moapd.copenhagenbuzz.asjo.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

   // private lateinit var binding : ActivityLoginBinding

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        onSignInResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createSignInIntent()
    }

    private fun createSignInIntent() {

        // Choose authentication providers.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent.
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(true)
            .setLogo(R.drawable.baseline_firebase_24)
            .setTheme(R.style.Theme_CopenhagenBuzz)
            .apply {
                setTosAndPrivacyPolicyUrls(
                    "https://firebase.google.com/terms/",
                    "https://firebase.google.com/policies/analytics"
                )
            }
            .build()
        signInLauncher.launch(signInIntent)
    }
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        when (result.resultCode) {
            RESULT_OK -> {
                // Successfully signed in.
                showSnackBar("User logged in the app.")
                startMainActivity()
            }
            else -> {
                // Sign in failed.
                showSnackBar("Authentication failed.")
            }
        }
    }

    private fun startMainActivity() {
        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }
    private fun showSnackBar(message: String) {
        Snackbar.make(
            window.decorView.rootView, message, Snackbar.LENGTH_SHORT
        ).show()
    }

}

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.loginButton.setOnClickListener{
//            val intent = Intent(this,MainActivity::class.java).apply {
//                putExtra("isLoggedIn",true)
//            }
//            Log.d("MenuDebug", "Login Button Clicked - Sending IsLoggedIn = true")
//            startActivity(intent)
//            finish()
//        }
//
//        binding.guestButton.setOnClickListener{
//            val intent = Intent(this,MainActivity::class.java).apply {
//                putExtra("isLoggedIn", false)
//            }
//            Log.d("MenuDebug", "Guest Button Clicked - Sending IsLoggedIn = false")
//            startActivity(intent)
//            finish()
//        }
//
//
//    }
//}