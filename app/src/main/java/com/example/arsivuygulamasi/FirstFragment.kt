package com.example.arsivuygulamasi

import android.annotation.SuppressLint
import android.app.Application
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FirstFragment : Fragment(), View.OnClickListener, OnDayNightStateChanged {
    private lateinit var firstFragmentAdapter: FirstFragmentAdapter
    private lateinit var menuButton: ImageView
    private lateinit var ekleButton: ImageView
    private lateinit var firstFragmentRecyclerView: RecyclerView
    private lateinit var detailBackButton: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    var postListesi = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        verileriAl()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuButton = view.findViewById(R.id.toolbar_menu_btn)
        menuButton.setOnClickListener(this)
        menuButton.visibility = View.VISIBLE
        ekleButton = view.findViewById(R.id.toolbar_yeni_kayit)
        ekleButton.setOnClickListener(this)
        ekleButton.visibility = View.VISIBLE
        detailBackButton = view.findViewById(R.id.toolbar_back_btn)
        detailBackButton.setOnClickListener(this)
        firstFragmentRecyclerView = view.findViewById(R.id.firstFragmentRecyclerView)
        val layoutManager = LinearLayoutManager(requireContext())
        firstFragmentRecyclerView.layoutManager = layoutManager
        firstFragmentAdapter = FirstFragmentAdapter(postListesi,requireContext())
        firstFragmentRecyclerView.adapter = firstFragmentAdapter
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.toolbar_menu_btn -> {
                replaceFragment(OptionsMenuFragment())
            }
            R.id.toolbar_yeni_kayit -> {
                menuButton.visibility = View.GONE
                detailBackButton.visibility = View.VISIBLE
                replaceFragment(FotografPaylasmaFragment())
            }
            R.id.toolbar_back_btn -> {
                if (detailBackButton.visibility == 0) {
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

    @SuppressLint("NotifyDataSetChanged")
    fun verileriAl() {
        database.collection("Post").orderBy(
            "tarih",
            Query.Direction.DESCENDING
        ).addSnapshotListener { snapshot, exeption ->
            if (exeption != null) {
                Toast.makeText(requireContext(), exeption.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            } else {
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        val documents = snapshot.documents
                        postListesi.clear()
                        for (document in documents) {
                            val gezilenYer = document.get("gezilenyer") as String
                            val kullaniciEmail = document.get("kullaniciemail") as String
                            val kullaniciYorum = document.get("kullaniciyorum") as String
                            val gorselUrl = document.get("gorselurl") as String
                            val konum = document.get("konum") as String
                            val indirilenPost = Post(gezilenYer, kullaniciEmail, kullaniciYorum, gorselUrl, konum)
                            postListesi.add(indirilenPost)
                        }
                        firstFragmentAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onDayNightApplied(state: Int) {
        //
    }
}