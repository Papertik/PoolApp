package com.example.pool

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val password = getString(R.string.Password)
        val enterpass = findViewById<Button>(R.id.EnterPassword)
        enterpass.setOnClickListener{
                val passwordEDIT = findViewById<EditText>(R.id.PasswordEntry)
                if (passwordEDIT.text.toString()==password){
                    startActivity(Intent(this, HomeScreen::class.java))
            }else if(passwordEDIT.text.toString() !=password){
                Log.d("ERROR:","INCORRECT PASSWORD")
                }
            }
    }

}