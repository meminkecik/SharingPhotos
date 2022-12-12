package com.mek.sharingphotos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.core.View
import com.mek.sharingphotos.R
import com.mek.sharingphotos.model.Post
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_row.view.*
import java.util.ArrayList

class NewsRecyclerAdapter(var postList: ArrayList<Post>) : RecyclerView.Adapter<NewsRecyclerAdapter.PostHolder>() {
    class PostHolder(itemView : android.view.View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
    val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row,parent,false)
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.itemView.recycler_row_email.text = postList[position].kullaniciEmail
        holder.itemView.recycler_row_yorum.text = postList[position].kullaniciYorum
        Picasso.get().load(postList[position].downloadUrl).into(holder.itemView.recycler_row_gorsel)

    }

    override fun getItemCount(): Int {
        return postList.size
    }
}