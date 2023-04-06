package com.smartshehar.customercallingv2.utils

import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class ResponseHelper {

    companion object {
        fun getErrorMessage(response: Response<*>): String {
            val errorBody = response.errorBody()
            if (response.code() == 403) {
                return "You do not have access to perform this action"
            }
            if (response.code() == 401) {
                return "You are not logged in, Please login"
            }
            val errorMessage: String
            errorMessage = try {
                if (null == (errorBody)) response.message() else errorBody.string()
            } catch (e: IOException) {
                return ""
            }
            try {
                val json = JSONObject(errorMessage)
                if (json.has("error")) {
                    return json.getString("error")
                }
                if (json.has("message")) {
                    return json.getString("message")
                }
            } catch (e: JSONException) {
                return ""
            }
            return errorMessage
        }

    }

}