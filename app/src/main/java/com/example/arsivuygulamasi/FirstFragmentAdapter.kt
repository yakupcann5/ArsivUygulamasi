package com.example.arsivuygulamasi

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.first_fregament_recycler_row.view.*

class FirstFragmentAdapter(val postList: ArrayList<Post>, val context: Context) :
    RecyclerView.Adapter<FirstFragmentAdapter.PostHolder>() {
    class PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.first_fregament_recycler_row, parent, false)
        return PostHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.itemView.gezilen_yer_isim.text = postList[position].gezilenYer
        holder.itemView.gezilen_yer_isim_acıklama.text = postList[position].kullaniciYorum
        holder.itemView.gezilen_yer_kullanici_adi.text = postList[position].kullaniciEmail
        Picasso.get().load(postList[position].gorselUrl).into(holder.itemView.gezilen_yer_image)
        holder.itemView.setOnClickListener {
            replaceFragment(DetayFragment(postList[position],context))
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    private fun replaceFragment(fragment: Fragment) {
        val changer: FragmentTransaction =
            (context as AppCompatActivity?)?.supportFragmentManager!!.beginTransaction()
        changer.addToBackStack(null)
        changer.replace(R.id.flFragment, fragment)
        changer.commit()
    }
}