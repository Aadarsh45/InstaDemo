package com.rj.geeksinstademo.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rj.geeksinstademo.R
import com.rj.geeksinstademo.model.Notification
import com.rj.geeksinstademo.model.Post
import com.rj.geeksinstademo.model.User
import com.squareup.picasso.Picasso

class NotificationAdapter(private val mContext: Context, mNotifications: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder?>() {
    private val mNotifications: List<Notification>

    init {
        this.mNotifications = mNotifications
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val  notification:Notification = mNotifications[position]
        getUser(holder.imageProfile,holder.username,notification.userid!!)
        holder.comment.setText(notification.text)
        Log.i("OKKKKKKKKKK",notification.isPost.toString())
        if (notification.getIsPost()!!){
            holder.postImage.visibility= View.VISIBLE
            getPostImage(holder.postImage,notification.postid!!)
        }
        else{
            holder.postImage.visibility= View.GONE

        }
    }

    override fun getItemCount(): Int {
        return mNotifications.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageProfile: ImageView
        var postImage: ImageView
        var username: TextView
        var comment: TextView

        init {
            imageProfile = itemView.findViewById<ImageView>(R.id.image_profile)
            postImage = itemView.findViewById<ImageView>(R.id.post_image)
            username = itemView.findViewById<TextView>(R.id.username)
            comment = itemView.findViewById<TextView>(R.id.comment)
        }
    }

    private fun getPostImage(imageView: ImageView,postId:String){
        Log.i("OKKKKKKKKK","HERE1")

        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i("OKKKKKKKKK","HERE3")

                    val post: Post = snapshot.getValue(Post::class.java)!!
                    Log.i("OKKKKK",post.imageurl.toString())
                    Picasso.get().load(post.imageurl).placeholder(R.drawable.placeholder).into(imageView)

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }
    private fun getUser(imageView: ImageView,textView: TextView,userId:String){

        FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: User = snapshot.getValue(User::class.java)!!
                    if (user.imageurl.equals("default")){
                        imageView.setImageResource(R.drawable.profile_placeholder)
                    }else{
                        Picasso.get().load(user.imageurl).placeholder(R.drawable.profile_placeholder).into(imageView)

                    }
                    textView.setText(user.name)

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

}