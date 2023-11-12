package com.example.pool
import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.os.Bundle
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import java.nio.charset.Charset
import java.util.UUID

class DeviceConnectionActivity : Activity() {
    private var bluetoothGatt: BluetoothGatt? = null
    private lateinit var selectedDevice: BluetoothDevice
    private lateinit var pass: String
    private lateinit var ssid: String

    private lateinit var deviceView: TextView
    private lateinit var passView: TextView
    private lateinit var ssidView: TextView

    private val context: Context
        get() {
            return(this);
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_connection)

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

        selectedDevice = intent.getParcelableExtra("selectedDevice")!!
        pass = intent.getStringExtra("pass")!!
        ssid = intent.getStringExtra("ssid")!!


        deviceView = findViewById<TextView>(R.id.devicenameView)
        passView = findViewById<TextView>(R.id.passwordView)
        ssidView = findViewById<TextView>(R.id.ssidView)

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
        deviceView.setText(selectedDevice.name)
        passView.setText(pass)
        ssidView.setText(ssid)



        // Establish a connection with the selected device
        connectToDevice(selectedDevice)

    } 

    private fun connectToDevice(device: BluetoothDevice) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        // Connect to the device and manage GATT operations
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permisAsions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        bluetoothGatt = device.connectGatt(this, true, this.gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            Looper.prepare()
            Toast.makeText(context, "Entered connection state change", Toast.LENGTH_SHORT).show();

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // Connected to the device, now discover services
                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
                if (ActivityCompat.checkSelfPermission(
                        context,
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
                gatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(context, "Not connected", Toast.LENGTH_SHORT).show();
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            Toast.makeText(context, "Entered Callback", Toast.LENGTH_SHORT).show();

            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Find the desired characteristic
                val SERVICE_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
                val CHARACTERISTIC_UUID = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8")
                val service = gatt?.getService(SERVICE_UUID)
                val characteristic = service?.getCharacteristic(CHARACTERISTIC_UUID)

                // Write your string as bytes to the characteristic

                val dataToSend = pass + "," + ssid
                val dataBytes = dataToSend.toByteArray(Charset.forName("UTF-8"))
                Log.d(TAG,dataToSend)
                characteristic?.value = dataBytes
                if (ActivityCompat.checkSelfPermission(
                        context,
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
                gatt?.writeCharacteristic(characteristic)
            } else {
                // Handle service discovery failure
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            Toast.makeText(context, "Entered characteristic write", Toast.LENGTH_SHORT).show();

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Characteristic write successful")
            } else {
                Log.e(TAG, "Characteristic write failed with status: $status")
            }
        }
    }
}
