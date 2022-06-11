package com.example.arsivuygulamasi

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit

class MySharedPreferences {
    val THEMECOLOR = "themeColor"

    companion object {
        private var sharedPreferences: SharedPreferences? = null

        @Volatile
        private var instance: MySharedPreferences? = null
        private val lock = Any()
        operator fun invoke(context: Context): MySharedPreferences =
            instance ?: synchronized(lock) {
                instance ?: createSharedPreference(context).also {
                    instance = it
                }
            }

        private fun createSharedPreference(context: Context): MySharedPreferences {
            sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context)
            return MySharedPreferences()
        }
    }

    fun setThemeColor(themeColor: String?) {
        sharedPreferences?.edit(commit = true) {
            putString(THEMECOLOR, themeColor)
        }
    }

    fun getThemeColor(): String? {
        return sharedPreferences?.getString(THEMECOLOR, "")
    }

}