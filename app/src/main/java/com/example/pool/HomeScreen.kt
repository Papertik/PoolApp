package com.example.pool

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HomeScreen : AppCompatActivity() {
    private fun fetchDataFromThingSpeak(): String {
        return runBlocking {
            withContext(Dispatchers.IO) {
                val url = URL("https://api.thingspeak.com/channels/2120070/fields/1.json?api_key=TLITHTFB75ZF0QQW&results=2")
                val connection = url.openConnection() as HttpURLConnection

                val inputStream = connection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val jsonResponse = bufferedReader.readText()

                val jsonObject = JSONObject(jsonResponse)
                val feeds = jsonObject.getJSONArray("feeds")
                if (feeds.length() > 0) {
                    val firstFeed = feeds.getJSONObject(0)
                    return@withContext firstFeed.getString("field1")
                } else {
                    return@withContext "No data available"
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        val buttonClick = findViewById<Button>(R.id.Pool_1)
        buttonClick.setOnClickListener {
            val intent = Intent(this, Pool1::class.java)
            startActivity(intent)}

        val imagebuttonClick = findViewById<ImageButton>(R.id.settings_button)
        imagebuttonClick.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)}

        try {
            GlobalScope.launch {
                val tsData = fetchDataFromThingSpeak()
                withContext(Dispatchers.Main) {
                    updateButton(tsData)

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    private fun updateButton(value: String) {
        val buttonname = findViewById<Button>(R.id.Pool_1)
        val buttonText = getString(R.string.button_text_template, value)
        buttonname.text = buttonText
    }
}