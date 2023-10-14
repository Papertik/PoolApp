package com.example.pool

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.androidplot.xy.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import kotlin.math.roundToInt


class Pool1 : AppCompatActivity() {
// function to fetch from thingspeak
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
        setContentView(R.layout.activity_pool1)
        // Home button press to take home
        val imagebuttonClick = findViewById<ImageButton>(R.id.homebutton)
        imagebuttonClick.setOnClickListener {
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
        }
        // settings button to go to settings
        val imagebutton1Click = findViewById<ImageButton>(R.id.settings_button)
        imagebutton1Click.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }
        // code to update UI
        try {
            GlobalScope.launch {
                val data = fetchDataFromThingSpeak()
                withContext(Dispatchers.Main) {
                    val textView = findViewById<TextView>(R.id.TempOut)
                    textView.text = data
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // code for graph maybe?
        val tempPlot: XYPlot =  findViewById(R.id.tempGraph)
        val domainLabels = arrayOf<Number>(1,2,3,6,7,8,9,10,13,14)
        val series1Number = arrayOf<Number>(1,4,8,12,16,32,26,29,10,13)

        val series1 : XYSeries = SimpleXYSeries(listOf(* series1Number),SimpleXYSeries.ArrayFormat.Y_VALS_ONLY
            ,"Series 1")

        val series1Format = LineAndPointFormatter(Color.BLUE,Color.BLACK,null,null)

        tempPlot.addSeries(series1,series1Format)

        tempPlot.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = object : Format() {
            override fun format(
                obj: Any?,
                toAppendTo: StringBuffer,
                pos: FieldPosition
            ): StringBuffer {
                val i = (obj as Number).toFloat().roundToInt()
                return toAppendTo.append(domainLabels[i])
            }

            override fun parseObject(source: String?, pos: ParsePosition): Any? {
                return null
            }

        }
    }
}
