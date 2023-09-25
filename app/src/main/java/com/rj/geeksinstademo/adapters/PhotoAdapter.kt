package com.rj.geeksinstademo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.rj.geeksinstademo.R
import com.rj.geeksinstademo.model.Post

import com.squareup.picasso.Picasso

class PhotoAdapter(private val mContext: Context, mPosts: List<Post>) :
    RecyclerView.Adapter<PhotoAdapter.ViewHolder?>() {
    private val mPosts: List<Post>

    init {
        this.mPosts = mPosts
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.photo_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post: Post = mPosts[position]
        Picasso.get().load(post.imageurl).placeholder(R.mipmap.ic_launcher)
            .into(holder.postImage)

    }

    override fun getItemCount(): Int {
        return mPosts.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var postImage: ImageView

        init {
            postImage = itemView.findViewById<ImageView>(R.id.post_image)
        }
    }
}