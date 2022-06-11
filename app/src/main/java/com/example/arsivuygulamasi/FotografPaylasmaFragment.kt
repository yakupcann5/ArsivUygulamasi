package com.example.arsivuygulamasi

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception
import java.util.*

class FotografPaylasmaFragment : Fragment(), View.OnClickListener {
    private lateinit var detailBackButton: ImageView
    private lateinit var menuButton: ImageView
    private lateinit var ekleButton: ImageView
    private lateinit var gorselEkle: ImageView
    private lateinit var yeniYerKaydetButton: AppCompatButton
    private lateinit var gezilenYerIsim: EditText
    private lateinit var gezilenYerAciklama: EditText
    private lateinit var gezilenYerKonum: EditText
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    var secilenGorsel: Uri? = null
    var secilenBitmap: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fotograf_paylasma, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuButton = view.findViewById(R.id.toolbar_menu_btn)
        ekleButton = view.findViewById(R.id.toolbar_yeni_kayit)
        detailBackButton = view.findViewById(R.id.toolbar_back_btn)
        detailBackButton.setOnClickListener(this)
        gorselEkle = view.findViewById(R.id.detayImageView)
        yeniYerKaydetButton = view.findViewById(R.id.gezilen_yer_detay_guncelle_buton)
        yeniYerKaydetButton.setOnClickListener(this)
        gezilenYerIsim = view.findViewById(R.id.gezilen_yer_detay_isim_edit_text)
        gezilenYerAciklama = view.findViewById(R.id.gezilen_yer_detay_aciklama)
        gezilenYerKonum = view.findViewById(R.id.gezilem_yer_paylas_konum_text)
        detailBackButton.visibility = View.VISIBLE
        menuButton.visibility = View.GONE
        ekleButton.visibility = View.GONE
        gorselEkle.setOnClickListener {
            gorselSec(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.gezilen_yer_detay_guncelle_buton -> {
                fotografPaylas()
            }
            R.id.toolbar_back_btn -> {
                replaceFragment(FirstFragment())
            }
        }
    }

    private fun fotografPaylas() {
        val postHashMap = hashMapOf<String, Any>()
        val uudi = UUID.randomUUID()
        val gorselIsmi = "${uudi}.jpg"
        val reference = storage.reference
        val gorselReference = reference.child("images").child(gorselIsmi)
        if (secilenGorsel != null) {
            gorselReference.putFile(secilenGorsel!!).addOnSuccessListener {
                val yuklenenGorselReference =
                    FirebaseStorage.getInstance().reference.child("images").child(gorselIsmi)
                yuklenenGorselReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val guncelKullaniciEmail = auth.currentUser!!.email.toString()
                    val gezilenYer = gezilenYerIsim.text.toString()
                    val kullaniciYorumu = gezilenYerAciklama.text.toString()
                    val konum = gezilenYerKonum.text.toString()
                    val tarih = Timestamp.now()
                    //veritabanı işlemleri
                    postHashMap["gorselurl"] = downloadUrl
                    postHashMap["gezilenyer"] = gezilenYer
                    postHashMap["kullaniciyorum"] = kullaniciYorumu
                    postHashMap["kullaniciemail"] = guncelKullaniciEmail
                    postHashMap["tarih"] = tarih
                    postHashMap["konum"] = konum
                    database.collection("Post").add(postHashMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Paylaşıldı...", Toast.LENGTH_SHORT)
                                .show()
                            replaceFragment(FirstFragment())
                        }
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG)
                            .show()

                    }
                }
            }
        }
    }

    private fun gorselSec(view: View) {
        activity?.let {
            if (ContextCompat.checkSelfPermission(
                    it.applicationContext,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                val galeriIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galeriIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            secilenGorsel = data.data
            try {
                context?.let {
                    if (secilenGorsel != null) {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source =
                                ImageDecoder.createSource(it.contentResolver, secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            gorselEkle.setImageBitmap(secilenBitmap)
                        } else {
                            secilenBitmap =
                                MediaStore.Images.Media.getBitmap(it.contentResolver, secilenGorsel)
                            gorselEkle.setImageBitmap(secilenBitmap)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun replaceFragment(fragment: Fragment) {
        val changer: FragmentTransaction =
            (context as AppCompatActivity?)?.supportFragmentManager!!.beginTransaction()
        changer.addToBackStack(null)
        changer.replace(R.id.flFragment, fragment)
        changer.commit()
    }
}