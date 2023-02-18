package com.smartshehar.customercallingv2.utils

import android.content.Context

import android.net.ConnectivityManager
import android.net.NetworkInfo


class InternetConnectivity {

    companion object{
        fun isNetworkConnected(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo

            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting
        }
    }


}