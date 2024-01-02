package com.chillarcards.gsfk.utills

import android.content.Context
import android.content.SharedPreferences

class PrefManager(_context: Context) {

    companion object {
        // Shared preferences file name
        private const val PREF_NAME = "GSFK"

        private const val IS_LOGGED_IN = "IS_LOGGED_IN"
        private const val TOKEN = "TOKEN"
        private const val MOBILENO = "MOBILENO"

        // shared pref mode
        private const val PRIVATE_MODE = Context.MODE_PRIVATE
    }

    private val pref: SharedPreferences = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    private val editor: SharedPreferences.Editor = pref.edit()

    fun isLoggedIn(): Boolean {
        return pref.getBoolean(IS_LOGGED_IN, false)
    }

    fun setIsLoggedIn(value: Boolean) {
        editor.putBoolean(IS_LOGGED_IN, value)
        editor.commit()
    }

    fun getToken(): String {
        return pref.getString(TOKEN, "") ?: ""
    }

    fun setToken(token: String) {
        editor.putString(TOKEN, token)
        editor.commit()
    }
    fun getMobileNo(): String {
        return pref.getString(MOBILENO, "") ?: ""
    }

    fun setMobileNo(token: String) {
        editor.putString(MOBILENO, token)
        editor.commit()
    }
    fun clearAll() {
        editor.clear()
        editor.commit()
    }

    fun clearField(keyName: String) {
        editor.remove(keyName).apply()
    }
}
