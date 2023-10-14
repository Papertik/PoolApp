package com.example.pool

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val imagebuttonClick = findViewById<ImageButton>(R.id.homebutton)
        imagebuttonClick.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)}
// button to go to WIFI settings
        val buttonClick = findViewById<Button>(R.id.wifi)
        buttonClick.setOnClickListener {
            val intent = Intent(this, WifiCreds::class.java)
            startActivity(intent)}
        // button to go to password change
        val Passchange = findViewById<Button>(R.id.PasswordChange)
        Passchange.setOnClickListener {
            val intent = Intent(this, PasswordChange::class.java)
            startActivity(intent)}
    }
}