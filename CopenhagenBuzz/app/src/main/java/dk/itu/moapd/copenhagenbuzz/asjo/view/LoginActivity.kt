package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dk.itu.moapd.copenhagenbuzz.asjo.R
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java).apply {
                putExtra("isLoggedIn",true)
            }
            Log.d("MenuDebug", "Login Button Clicked - Sending IsLoggedIn = true")
            startActivity(intent)
            finish()
        }

        binding.guestButton.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java).apply {
                putExtra("isLoggedIn", false)
            }
            Log.d("MenuDebug", "Guest Button Clicked - Sending IsLoggedIn = false")
            startActivity(intent)
            finish()
        }


    }
}