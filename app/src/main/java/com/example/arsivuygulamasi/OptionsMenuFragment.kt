package com.example.arsivuygulamasi

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_options_menu.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class OptionsMenuFragment : Fragment(), View.OnClickListener {
    private lateinit var cikisYapButton: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var darkModeTextView: TextView
    private lateinit var lightModeTextView: TextView
    private lateinit var lightModeImageView: ImageView
    private lateinit var darkModeImageView: ImageView
    var control = true
    var themeColor: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_options_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cikisYapButton = view.findViewById(R.id.cikis_text)
        cikisYapButton.setOnClickListener(this)
        darkModeTextView = view.findViewById(R.id.theme_dark_mode_text)
        darkModeTextView.setOnClickListener(this)
        darkModeImageView = view.findViewById(R.id.dark_mode_image)
        lightModeImageView = view.findViewById(R.id.light_mode_image)
        themeColor()
        if (MySharedPreferences.invoke(requireContext()).getThemeColor()
                .toString() == "Dark Mode"
        ) {
            darkModeTextView.text = "Light Mode"
            darkModeImageView.visibility = View.GONE
            lightModeImageView.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cikis_text -> {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle("Logout")
                alertDialog.setMessage("Are you sure you want to log out?")
                alertDialog.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                alertDialog.setPositiveButton("Yes") { dialog, which ->
                    dialog.dismiss()
                    val progress = ProgressDialog(context)
                    progress.setTitle("Logout " + "...")
                    progress.setMessage("Checking Out " + "...")
                    progress.setCancelable(false)
                    progress.show()
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(TimeUnit.SECONDS.toMillis(2))
                        progress.cancel()
                    }
                    auth.signOut()
                    replaceFragment(LoginFragment())
                }
                alertDialog.show()
            }
            R.id.theme_dark_mode_text -> {
                if (darkModeTextView.text == "Light Mode") {
                    MySharedPreferences.invoke(requireContext()).setThemeColor("Default")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    replaceFragment(FirstFragment())
                } else {
                    MySharedPreferences.invoke(requireContext()).setThemeColor("Dark Mode")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    darkModeTextView.text = "Light Mode"
                    darkModeImageView.visibility = View.GONE
                    lightModeImageView.visibility = View.VISIBLE
                    replaceFragment(FirstFragment())
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val changer: FragmentTransaction =
            (context as AppCompatActivity?)?.supportFragmentManager!!.beginTransaction()
        changer.addToBackStack(null)
        changer.replace(R.id.flFragment, fragment)
        changer.commit()
    }

    fun themeColor() {
        themeColor = MySharedPreferences.invoke(requireContext()).getThemeColor().toString()
        when (themeColor) {
            "Default" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            "Dark Mode" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }
}