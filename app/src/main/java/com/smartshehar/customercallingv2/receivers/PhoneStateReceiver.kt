package com.smartshehar.customercallingv2.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.smartshehar.customercallingv2.FloatingWindow
import android.os.Build
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import android.telephony.PhoneStateListener

import android.telephony.TelephonyManager
import androidx.work.Data
import com.smartshehar.customercallingv2.repositories.CustomerRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PhoneStateReceiver : BroadcastReceiver() {
    private val TAG = "PhoneStateReceiver"


    @Inject
    lateinit var customerRepository: CustomerRepository

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Received", Toast.LENGTH_SHORT).show();
        try {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            val listener = object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, incomingNumber: String) {
                    var stateString = "N/A"
                    when (state) {
                        TelephonyManager.CALL_STATE_IDLE -> stateString = "Idle"
                        TelephonyManager.CALL_STATE_OFFHOOK -> stateString = "Off Hook"
                        TelephonyManager.CALL_STATE_RINGING -> {
                            val incomingNumber2 =
                                intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                            if (incomingNumber2 != null) {
                                startPopupShowWorker(context, incomingNumber)
                            }
                            stateString = "Ringing"
                        }
                    }


                    Log.d(TAG, "onCallStateChanged: $stateString ")
                }
            }
            telephonyManager!!.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startPopupShowWorker(context: Context, incomingNumber: String) {
        try {
            val customer = customerRepository.getCustomerDetailsWithNumber(incomingNumber)
            val serviceIntent = Intent(context, FloatingWindow::class.java)
            serviceIntent.putExtra("name", customer.firstName)
            serviceIntent.putExtra("phone",customer.msPhoneNo)

            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val data = Data.Builder()
                    data.putString("name", customer.firstName)
                    data.putString("phone", customer.msPhoneNo)
                    val request: OneTimeWorkRequest =
                        OneTimeWorkRequest.Builder(FloatingWindow::class.java)
                            .setInputData(data.build())
                            .addTag("BACKUP_WORKER_TAG").build()
                    WorkManager.getInstance(context).enqueue(request)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    context.startForegroundService(serviceIntent)
                }
                else -> {
                    context.startService(serviceIntent)
                }
            }
        }catch (e : Exception){

        }
    }
}