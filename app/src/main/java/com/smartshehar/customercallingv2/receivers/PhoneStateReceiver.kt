package com.smartshehar.customercallingv2.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.smartshehar.customercallingv2.FloatingWindow
import android.os.Build
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import android.telephony.PhoneStateListener

import android.telephony.TelephonyManager
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import com.smartshehar.customercallingv2.repositories.customer.CustomerRepository
import com.smartshehar.customercallingv2.repositories.sqlite.AppLocalDatabase
import kotlin.math.log

class PhoneStateReceiver : BroadcastReceiver() {
    private val TAG = "PhoneStateReceiver"
    var isTriggered: Boolean = false

    lateinit var customerRepository: CustomerRepository

    override fun onReceive(context: Context, intent: Intent) {
        val db = AppLocalDatabase.getInstance(context)
        customerRepository = CustomerRepository(db.customerDao())

        try {
            if (isTriggered) {
                return
            }
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            val listener = object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, incomingNumber: String) {
                    var stateString = "N/A"
                    when (state) {
                        TelephonyManager.CALL_STATE_IDLE -> stateString = "Idle"
                        TelephonyManager.CALL_STATE_OFFHOOK -> stateString = "Off Hook"
                        TelephonyManager.CALL_STATE_RINGING -> {
                            isTriggered = true
                            val incomingNumber2 =
                                intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                            if (incomingNumber2 != null) {
                                Log.d(TAG, "onReceive: ${customerRepository}")
                                Log.d(TAG, "onCallStateChanged: Launched worker")
                                startPopupShowWorker(context, incomingNumber2)
                            }
                            stateString = "Ringing"
                        }

                    }
                }
            }
            telephonyManager!!.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startPopupShowWorker(context: Context, incomingNumber: String) {
        try {
            val serviceIntent = Intent(context, FloatingWindow::class.java)

            val customer = customerRepository.getCustomerDetailsByNumber(incomingNumber)
            if (customer == null) {
                serviceIntent.putExtra("isNewCustomer", true);
            } else {
                serviceIntent.putExtra("name", customer.firstName)
                serviceIntent.putExtra("id", customer.customerId)
                serviceIntent.putExtra("phone", customer.msPhoneNo)
            }

            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val data = Data.Builder()
                    if (customer == null) {
                        data.putBoolean("isNewCustomer", true)
                    } else {
                        data.putString("name", customer.firstName)
                        data.putString("phone", customer.msPhoneNo)
                        data.putInt("id", customer.customerId)
                    }
                    val request: OneTimeWorkRequest =
                        OneTimeWorkRequest.Builder(FloatingWindow::class.java)
                            .setInputData(data.build())
                            .addTag("BACKUP_WORKER_TAG").build()
                    //WorkManager.getInstance(context).enqueue(request)
                    WorkManager.getInstance(context)
                        .beginUniqueWork("BACK_WORK", ExistingWorkPolicy.REPLACE, request).enqueue()
                    //WorkManager.getInstance(context).cancelAllWorkByTag("BACKUP_WORKER_TAG")
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    context.startForegroundService(serviceIntent)
                }
                else -> {
                    context.startService(serviceIntent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}