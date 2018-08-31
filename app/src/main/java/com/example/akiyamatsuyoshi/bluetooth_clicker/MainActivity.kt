package com.example.akiyamatsuyoshi.bluetooth_clicker

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    private var mBluetoothAdapter:BluetoothAdapter? = null
    private lateinit var mPairedDeviecs: Set<BluetoothDevice>
    private val REQ_ENABLE_BLUETOOTH = 1

    companion object {
        val EXTRA_ADDRESS: String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter

        if (mBluetoothAdapter == null || !mBluetoothAdapter!!.isEnabled)
        {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQ_ENABLE_BLUETOOTH)
        }
        val refresh = findViewById<Button>(R.id.refreshButton);
        refresh.setOnClickListener{ pairedDeviceList() }
    }

    private fun pairedDeviceList() {
        mPairedDeviecs = mBluetoothAdapter!!.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList()

        if (!mPairedDeviecs.isEmpty()) for (device: BluetoothDevice in mPairedDeviecs) {
            list.add(device)
            Log.i("device", ""+device)
        } else {
            toast("no paired devices found")
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        val deviceList = findViewById<ListView>(R.id.deviceList)

        deviceList.adapter = adapter
        deviceList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address

            val intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (mBluetoothAdapter!!.isEnabled) {
                    toast("Bluetooth has been available")
                } else {
                    toast("Bluetooth has been disabled")
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                toast("Bluetooth enabling has been canceled")
            }
        }
    }
}
