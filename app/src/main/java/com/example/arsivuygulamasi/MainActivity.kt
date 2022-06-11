package com.example.arsivuygulamasi

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(LoginFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val changer: FragmentTransaction =
            this.supportFragmentManager.beginTransaction()
        changer.addToBackStack(null)
        changer.replace(R.id.flFragment, fragment)
        changer.commit()
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO){
            applyDayNight(OnDayNightStateChanged.DAY)
        }else{
            applyDayNight(OnDayNightStateChanged.NIGHT)
        }
    }
    private fun applyDayNight(state: Int){
        if (state == OnDayNightStateChanged.DAY){
            //apply day colors for your views
        }else{
            //apply night colors for your views
        }
    }
}