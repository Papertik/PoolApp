package com.example.pool

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
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


    companion object{
        val EXTRA_ADRESS: String = "Device_adress"
        var PASS: String? = null
        var SSID: String? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_creds)

        var bt: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        var m_bluetoothAdapter:BluetoothAdapter = bt.adapter
        lateinit var m_pairedDevices: Set<BluetoothDevice>
        val REQUEST_ENABLE_BLUETOOTH = 1

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

        val bluetoothLeScanner = m_bluetoothAdapter.bluetoothLeScanner
        var scanning = false
        val handler = Handler()
        var devicesList = mutableListOf<ScanResult>()
        val SCAN_PERIOD: Long = 1000
        val bluetoothList: ListView =
            findViewById(R.id.bluetoothList) // Replace with your ListView ID
        bluetoothList.setOnItemClickListener { parent, view, position, id ->
            val selectedDevice = devicesList[position]

            // Start the DeviceConnectionActivity and pass the selected device
            val intent = Intent(this, DeviceConnectionActivity::class.java)
            intent.putExtra("selectedDevice", selectedDevice.device)
            startActivity(intent)
        }
        // Adapter to manage the data for the ListView
        val adapter: ArrayAdapter<String> by lazy {
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                devicesList.map { it.device.name ?: "Unknown Device" })
        }

        fun updateListView() {
            // Update the adapter data
            val uniqueDevices = HashSet<BluetoothDevice>()

            // Filter out duplicate devices
            devicesList.forEach { scanResult ->
                val device = scanResult.device
                uniqueDevices.add(device)
            }

            // Update the adapter with the unique devices
            adapter.clear()
            uniqueDevices.forEach { uniqueDevice ->
                val deviceName = uniqueDevice.name ?: "Unknown Device"
                adapter.add(deviceName)
            }

            // Notify the adapter that the data has changed
            adapter.notifyDataSetChanged()

            // Set the adapter to the ListView
            bluetoothList.adapter = adapter
        }

        val leScanCallback: ScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                // Called when a new BLE device is discovered
                val device = result.device
                val rssi = result.rssi
                // Add the discovered device to your list
                devicesList.add(result)
                // Update your ListView with the devices
                updateListView()
            }
        }

        fun scanLeDevice() {
            if (!scanning) {
                handler.postDelayed({
                    scanning = false
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_SCAN
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                    } else {
                        if (leScanCallback != null) {
                            bluetoothLeScanner.stopScan(leScanCallback!!)
                        }

                    }

                }, SCAN_PERIOD)
                scanning = true
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_SCAN
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                } else {
                    bluetoothLeScanner.startScan(leScanCallback)
                }
            } else {
                scanning = false
                bluetoothLeScanner.stopScan(leScanCallback!!)
            }
        }
        val blueButton = findViewById<Button>(R.id.select_device_refresh)
        blueButton.setOnClickListener {
            scanLeDevice()
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