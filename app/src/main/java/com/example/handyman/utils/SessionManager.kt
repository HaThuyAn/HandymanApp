package com.example.handyman.utils

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    var currentUserID: String? = null
//    var currentUserEmail: String? = null
    var currentUserName: String? = null
    var currentUserType: String? = null
  
    private const val PREF_NAME = "user_session"
    private const val KEY_EMAIL = "logged_in_email"

    fun saveLoggedInEmail(context: Context, email: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_EMAIL, email).apply()
    }

    fun getLoggedInEmail(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_EMAIL, "") ?: ""
    }

    fun clearSession(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    fun clearSessionXML(context: Context) {
        val sharedPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }

}