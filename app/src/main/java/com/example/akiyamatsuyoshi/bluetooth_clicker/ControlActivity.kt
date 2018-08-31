package com.example.akiyamatsuyoshi.bluetooth_clicker

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import java.io.IOException
import java.util.*

class ControlActivity : AppCompatActivity() {

    companion object {
        var mvUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var blueToothSocket: BluetoothSocket? = null
        lateinit var progress: ProgressBar
        lateinit var bluetoothAdapter: BluetoothAdapter
        var isConnected: Boolean = false
        lateinit var address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)

        ConnectToDevice(this).execute()

        val forwardButton = findViewById<Button>(R.id.forwardButton)
        val backwardButton = findViewById<Button>(R.id.backwardButton)
//        val timeText = findViewById<Button>(R.id.timeView)

        forwardButton.setOnClickListener { sendCommand("forward button is clicked")}
        backwardButton.setOnClickListener { sendCommand("0x50")}
    }

    private fun sendCommand(input: String) {
        if (blueToothSocket != null) {
            try {
                // TODO: here is the place bluetooth command happen
//                final byte KEY_ARROW_RIGHT        =byte(0x4F); input.toByteArray()
//                final byte KEY_ARROW_LEFT         =byte(0x50);
                val temp = byteArrayOf('l'.toByte())
                blueToothSocket!!.outputStream.write(input.toByteArray())
                println(input.toByteArray())

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if (blueToothSocket != null) {
            try {
                blueToothSocket!!.close()
                blueToothSocket = null
                isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progress = ProgressBar(context)
            progress.visibility = ProgressBar.VISIBLE
        }

        override fun doInBackground(vararg params: Void?): String? {
            try {
                if (blueToothSocket ==  null || !isConnected) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
                    blueToothSocket = device.createInsecureRfcommSocketToServiceRecord(mvUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    blueToothSocket!!.connect()
                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "could not connect")
            } else {
                isConnected = true
            }
            progress.visibility = View.GONE
        }
    }
}
