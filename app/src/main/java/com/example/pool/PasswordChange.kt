package com.example.pool

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton

class PasswordChange : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_change)
        // block for bottom nav bar
        val imagebuttonClick = findViewById<ImageButton>(R.id.homebutton)
        imagebuttonClick.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }
        val imagebutton1Click = findViewById<ImageButton>(R.id.settings_button)
        imagebutton1Click.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }
        val passchange = findViewById<Button>(R.id.PassChange)
        passchange.setOnClickListener {
                val password1 = findViewById<EditText>(R.id.NEWpass)
                val password2 = findViewById<EditText>(R.id.ConfPASS)
                if (password1.text.toString() == password2.text.toString()) {
                    val passcheck = if (password1.text.isNotEmpty()) password1 else R.string.Password
                    resources.getString(R.string.Password).also { updatedString ->
                        resources.updateConfiguration(
                            Configuration().apply { setLocale(resources.configuration.locale) },
                            resources.displayMetrics
                        )
                    }
                }
            }
    }
}
