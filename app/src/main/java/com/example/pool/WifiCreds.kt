package com.example.pool

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.core.app.ActivityCompat

class WifiCreds : AppCompatActivity() {
    private var m_bluetoothAdapter:BluetoothAdapter? = null
    private lateinit var m_pairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1

    companion object{
        val EXTRA_ADRESS: String = "Device_adress"
        var PASS: String? = null
        var SSID: String? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_creds)





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
        val espButton = findViewById<Button>(R.id.espBut)
        espButton.setOnClickListener {
            @Override
            fun pushESP(view: View?) {

                var ssidET = findViewById(R.id.ssid) as EditText
                var passET = findViewById(R.id.pass) as EditText

                SSID = ssidET.getText().toString()
                PASS = passET.getText().toString()
                CoroutineScope(Dispatchers.IO).launch {
                    pushToThingspeak(SSID!!, PASS!!)
                }
                println(SSID)
                println(PASS)
            }
        }
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        m_bluetoothAdapter = bluetoothManager.adapter
        if(m_bluetoothAdapter == null){
            Toast.makeText(applicationContext, "This device does not support bluetooth", Toast.LENGTH_LONG).show()
            return
        }
        if(!m_bluetoothAdapter!!.isEnabled){
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }
        val select_device_refresh = findViewById<Button>(R.id.select_device_refresh)
        select_device_refresh.setOnClickListener{pairedDeviceList()}



    }
    fun pairedDeviceList(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList()

        if(!m_pairedDevices.isEmpty()){
            Toast.makeText(applicationContext, "Refreshed", Toast.LENGTH_SHORT).show()
            for(device:BluetoothDevice in m_pairedDevices){
                list.add(device)
                Log.i("device", ""+device)
            }
        }else{
            Toast.makeText(applicationContext, "No bluetooth devices found", Toast.LENGTH_LONG).show()
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        val select_device_list = findViewById<ListView>(R.id.bluetoothList)
        select_device_list.adapter = adapter
        select_device_list.onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val adress: String = device.address

            val intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADRESS, adress)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_ENABLE_BLUETOOTH && resultCode == Activity.RESULT_OK){
            if(m_bluetoothAdapter!!.isEnabled){
                Toast.makeText(applicationContext, "Bluetooth enabeled", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(applicationContext, "Bluetooth disabeled", Toast.LENGTH_SHORT).show()
            }
        }
    }
    suspend fun pushToThingspeak(SSID: String, PASS: String){
        val thingspeak = HttpClient()

        thingspeak.get("https://api.thingspeak.com/update?api_key=G0J6BS1QK4ASIORU") {
            url {
                parameters.append("field1", SSID)
                parameters.append("field2", PASS)
            }
        }
    }
}