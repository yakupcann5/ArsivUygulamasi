package com.example.arsivuygulamasi

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.arsivuygulamasi.databinding.FragmentDetayBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class DetayFragment(val post: Post, context: Context) : Fragment(),
    View.OnClickListener {
    private lateinit var binding: FragmentDetayBinding
    private lateinit var menuButton: ImageView
    private lateinit var ekleButton: ImageView
    private lateinit var deleteButton: ImageView
    private lateinit var detailBackButton: ImageView
    private lateinit var guncelleButton: AppCompatButton
    private lateinit var guncellenecekYerIsim: EditText
    private lateinit var guncellenecekYerAciklama: EditText
    private lateinit var guncellenecekYerKonum: EditText
    private lateinit var database: FirebaseFirestore
    private lateinit var firstFragmentAdapter: FirstFragmentAdapter
    private lateinit var postID: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseFirestore.getInstance()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detay, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.detail = post
        menuButton = view.findViewById(R.id.toolbar_menu_btn)
        menuButton.visibility = View.GONE
        ekleButton = view.findViewById(R.id.toolbar_yeni_kayit)
        ekleButton.visibility = View.GONE
        deleteButton = view.findViewById(R.id.toolbar_post_delete)
        deleteButton.setOnClickListener(this)
        deleteButton.visibility = View.VISIBLE
        detailBackButton = view.findViewById(R.id.toolbar_back_btn)
        detailBackButton.setOnClickListener(this)
        detailBackButton.visibility = View.VISIBLE
        guncelleButton = view.findViewById(R.id.gezilen_yer_detay_guncelle_buton)
        guncelleButton.setOnClickListener(this)
        guncellenecekYerIsim = view.findViewById(R.id.gezilen_yer_detay_isim_edit_text)
        guncellenecekYerAciklama = view.findViewById(R.id.gezilen_yer_detay_aciklama)
        guncellenecekYerKonum = view.findViewById(R.id.gezilem_yer_detay_konum_text)
        Picasso.get().load(post.gorselUrl).into(binding.detayImageView)

        database.collection("Post")
            .addSnapshotListener { value, error ->
                value?.forEach {
                    postID = it.id


                }
            }

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.gezilen_yer_detay_guncelle_buton -> {
                Toast.makeText(requireContext(), "Post Güncellendi", Toast.LENGTH_SHORT).show()
                update()
                replaceFragment(FirstFragment())
            }
            R.id.toolbar_back_btn -> {
                replaceFragment(FirstFragment())
            }
            R.id.toolbar_post_delete -> {
                val alertDialog = AlertDialog.Builder(context)
                alertDialog.setTitle("Post Delete")
                alertDialog.setMessage("Should You Delete The Post?")
                alertDialog.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                alertDialog.setPositiveButton("Yes") { dialog, which ->
                    dialog.dismiss()
                    val progress = ProgressDialog(context)
                    progress.setTitle("Post Delete " + "...")
                    progress.setMessage("The Post Is Being Deleted" + "...")
                    progress.setCancelable(false)
                    progress.show()
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(TimeUnit.SECONDS.toMillis(2))
                        progress.cancel()
                    }
                    database.collection("Post").document(postID).delete()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(requireContext(), "Post Silindi", Toast.LENGTH_SHORT)
                                    .show()
                                replaceFragment(FirstFragment())
                            } else {
                                Toast.makeText(requireContext(), "Post Silinemedi", Toast.LENGTH_SHORT).show()
                            }
                        }
                    replaceFragment(LoginFragment())
                }
                alertDialog.show()
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
    fun update() {
        val tarih = Timestamp.now()
        val postHasMap = hashMapOf<String, Any>()
        postHasMap.put("gorselurl", post.gorselUrl)
        postHasMap.put("gezilenyer",guncellenecekYerIsim.text.toString())
        postHasMap.put("kullaniciyorum",guncellenecekYerAciklama.text.toString())
        postHasMap.put("tarih",tarih)
        postHasMap.put("konum",guncellenecekYerKonum.text.toString())
        database.collection("Post").document(postID).update(postHasMap)
    }
}