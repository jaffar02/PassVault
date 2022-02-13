package com.zero.passvault

import android.content.Context
import android.content.SharedPreferences
import android.net.Credentials

class SessionManagerPrefrences {
    var prefs: SharedPreferences? = null

    private var gContext: Context? = null

    private var editor: SharedPreferences.Editor? = null

    private final val PREF_NAME = "PassVault"

    private final val PREF_MODE = 0

    constructor(context: Context?){
        if (context != null) {
            gContext = context
            prefs = context.getSharedPreferences(PREF_NAME, PREF_MODE)
            editor = prefs!!.edit()
        }
    }

    public final val KEY_NAME = "id"

    fun setKeyUserName(name: String?) {
        editor?.putString(KEY_NAME, name)
        editor?.commit()
    }

    fun getKeyUserName(): String? {
        return prefs!!.getString(KEY_NAME, null)
    }
}