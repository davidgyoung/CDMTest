package com.davidgyoungtech.cdmtest

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.BluetoothLeDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.davidgyoungtech.cdmtest.ui.theme.CDMTestTheme
import java.util.UUID
import java.util.regex.Pattern

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CDMTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            //setIcon(R.drawable.ic_hello)
            setTitle("Ready to Find Vehicle")
            setMessage("Make sure you are near a connectable advertising peripheral and tap OK to continue.")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                startCompanionDeviceManagerPairing()
            }
        }.create().show()
    }
    fun startCompanionDeviceManagerPairing() {

        val deviceName =  // TODO: Put your expected advertised name of your device here, e.g. "MyDevice"
        val deviceFilter: BluetoothLeDeviceFilter = BluetoothLeDeviceFilter.Builder().setNamePattern(
            Pattern.compile(deviceName)).build()

        val requestBuilder = AssociationRequest.Builder()
            // Find only devices that match this request filter.
            .addDeviceFilter(deviceFilter)
            // Keep scanning and show more than one device if available
            .setSingleDevice(true)

        val pairingRequest: AssociationRequest = requestBuilder.build()
        val deviceManager =
            this.getSystemService(Context.COMPANION_DEVICE_SERVICE) as CompanionDeviceManager
        Log.d(TAG, "calling companion device manager associate")
        deviceManager.associate(pairingRequest,
            @RequiresApi(Build.VERSION_CODES.O)
            object : CompanionDeviceManager.Callback() {
                // Called when a device is found. Launch the IntentSender so the user
                // can select the device they want to pair with.
                @Deprecated("Deprecated in Java")
                override fun onDeviceFound(chooserLauncher: IntentSender) {
                    Log.d(TAG, "Companion Device Manager onDeviceFound")
                    this@MainActivity.startIntentSenderForResult(chooserLauncher,
                        11, null, 0, 0, 0)
                }

                override fun onFailure(error: CharSequence?) {
                    Log.d(TAG,  "Failed to associate: $error")
                    val alertDialog = AlertDialog.Builder(this@MainActivity)
                    alertDialog.apply {
                        //setIcon(R.drawable.ic_hello)
                        setTitle("Failed")
                        setMessage("Could not find peripheral via Companion DeviceManager.  Please ensure a peripheral is nearby and advertising and try again.")
                        setNegativeButton("Cancel") { dialog, _ ->
                            System.exit(0)
                        }
                        setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            startCompanionDeviceManagerPairing()
                        }
                    }.create().show()
                    // Handle the failure.
                }
            }, Handler(Looper.getMainLooper())
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            11 -> when(resultCode) {
                Activity.RESULT_OK -> {
                    val extra: Any? = data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)
                    // The user chose to pair the app with a Bluetooth device.
                    var deviceToPair: BluetoothDevice? = null
                    if (extra is BluetoothDevice) {
                        deviceToPair= extra
                        Log.d(TAG, "Device found via Companion DeviceManager: ${deviceToPair?.address}")
                    }
                    else if (extra is ScanResult) {
                        val scanResult: ScanResult? = extra as ScanResult?
                        deviceToPair = scanResult?.device
                    }
                    else {
                        val alertDialog = AlertDialog.Builder(this)
                        alertDialog.apply {
                            //setIcon(R.drawable.ic_hello)
                            setTitle("Failed")
                            setMessage("Unexpected object type in CDM callback: ${extra?.javaClass?.name}")
                            setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                                System.exit(0)
                            }
                        }.create().show()
                    }
                    if (deviceToPair != null) {
                        val alertDialog = AlertDialog.Builder(this)
                        alertDialog.apply {
                            //setIcon(R.drawable.ic_hello)
                            setTitle("Success")
                            setMessage("Vehicle found via Companion DeviceManager: ${deviceToPair?.address}")
                            setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                                System.exit(0)
                            }
                        }.create().show()
                    }


                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Companion Device Manager Test App",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CDMTestTheme {
        Greeting("Android")
    }
}