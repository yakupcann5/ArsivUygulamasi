package com.example.arsivuygulamasi

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth

    private lateinit var girisYapButton: AppCompatButton
    private lateinit var kayitOlButton: AppCompatButton
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val guncelKullanıcı = auth.currentUser
        if (guncelKullanıcı != null) {
            replaceFragment(FirstFragment())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        girisYapButton = view.findViewById(R.id.giris_yap_button)
        girisYapButton.setOnClickListener(this)
        kayitOlButton = view.findViewById(R.id.kayit_ol_button)
        kayitOlButton.setOnClickListener(this)
        emailText = view.findViewById(R.id.email)
        passwordText = view.findViewById(R.id.password)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.giris_yap_button -> {
                val progress = ProgressDialog(context)
                progress.setTitle("Login " + "...")
                progress.setMessage("Logging In " + "...")
                progress.setCancelable(false)
                progress.show()
                CoroutineScope(Dispatchers.Main).launch {
                    delay(TimeUnit.SECONDS.toMillis(2))
                    progress.cancel()
                }
                girisYap()
            }
            R.id.kayit_ol_button -> {
                kayitOl()
            }
        }
    }

    fun girisYap() {
        auth.signInWithEmailAndPassword(emailText.text.toString(), passwordText.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val guncelKullanıcı = auth.currentUser?.email.toString()
                    Toast.makeText(
                        requireContext(),
                        "Hoşgeldin: $guncelKullanıcı",
                        Toast.LENGTH_SHORT
                    ).show()
                    replaceFragment(FirstFragment())
                }
            }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    fun kayitOl() {
        val email = emailText.text.toString()
        val password = passwordText.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    replaceFragment(FirstFragment())
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
            }
        }else {
            Toast.makeText(requireContext(), "Lütfen Tüm Bilgilerinizi Doldurun", Toast.LENGTH_SHORT).show()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val changer: FragmentTransaction =
            (context as AppCompatActivity?)?.supportFragmentManager!!.beginTransaction()
        changer.addToBackStack(null)
        changer.replace(R.id.flFragment, fragment)
        changer.commit()
    }
}