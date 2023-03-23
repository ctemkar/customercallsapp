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
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.repositories.customer.CustomerDao
import com.smartshehar.customercallingv2.repositories.customer.CustomerRepository
import com.smartshehar.customercallingv2.repositories.sqlite.AppLocalDatabase

class PhoneStateReceiver : BroadcastReceiver() {
    private val TAG = "PhoneStateReceiver"
    var isTriggered: Boolean = false

    override fun onReceive(context: Context, intent: Intent) {

        try {
            if (isTriggered) {
                return
            }
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            val listener = object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, incomingNumber: String) {
                    when (state) {
                        TelephonyManager.CALL_STATE_IDLE -> {}
                        TelephonyManager.CALL_STATE_OFFHOOK -> {}
                        TelephonyManager.CALL_STATE_RINGING -> {
                            isTriggered = true
                            val incomingNumber2 =
                                intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                            if (incomingNumber2 != null) {
                                val db = AppLocalDatabase.getInstance(context)
                                startPopupShowWorker(context, incomingNumber, db.customerDao())
                            }
                        }

                    }
                }
            }
            telephonyManager!!.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun startPopupShowWorker(
        context: Context,
        incomingNumber: String,
        customerDao: CustomerDao
    ) {
        try {
            val serviceIntent = Intent(context, FloatingWindow::class.java)
            val customer = getCustomerDetailsByNumber(incomingNumber, customerDao)
            if (customer == null) {
                serviceIntent.putExtra("isNewCustomer", true);
            } else {
                serviceIntent.putExtra("name", customer.firstName)
                serviceIntent.putExtra("id", customer.customerId)
                serviceIntent.putExtra("phone", customer.contactNumber)
            }

            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val data = Data.Builder()
                    if (customer == null) {
                        data.putBoolean("isNewCustomer", true)
                    } else {
                        data.putString("name", customer.firstName)
                        data.putString("phone", customer.contactNumber)
                        data.putLong("id", customer.customerId)
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

    private fun getCustomerDetailsByNumber(
        unformattedContactNumber: String,
        customerDao: CustomerDao
    ): Customer {
        var contactNumber = unformattedContactNumber
        if (contactNumber.startsWith("+91")) {
            contactNumber = unformattedContactNumber.substring(3)
        }
        Log.d(TAG, "getCustomerDetailsWithNumber: $contactNumber")
        return customerDao.getCustomerByPhoneNumber(contactNumber)
    }
}