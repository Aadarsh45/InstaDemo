package com.rj.geeksinstademo.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hendraanggrian.appcompat.widget.SocialTextView
import com.rj.geeksinstademo.CommentActivity
import com.rj.geeksinstademo.R
import com.rj.geeksinstademo.model.Post
import com.rj.geeksinstademo.model.User
import com.squareup.picasso.Picasso

class PostAdapter(private val mContext: Context, mPosts: List<Post>) :
    RecyclerView.Adapter<PostAdapter.Viewholder?>() {
    private val mPosts: List<Post>
    private val firebaseUser: FirebaseUser

    init {
        this.mPosts = mPosts
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser()!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false)
        return Viewholder(view)
    }
    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val post:Post = mPosts[position]
        Picasso.get().load(post.imageurl).into(holder.postImage)
        holder.description.setText(post.description)
        FirebaseDatabase.getInstance().getReference().child("Users").child(post.publisher!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: User = snapshot.getValue(User::class.java)!!
                    if (user.imageurl.equals("default")){
                        holder.imageProfile.setImageResource(R.drawable.profile_placeholder)
                    }else{
                        Picasso.get().load(user.imageurl).placeholder(R.drawable.profile_placeholder).into(holder.imageProfile)

                    }
                    holder.username.setText(user.username)
                    holder.author.setText(user.name)

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        isLiked(post.postid!!,holder.like)
        isSaved(post.postid!!,holder.save)

        holder.like.setOnClickListener(){

            if (holder.like.tag=="like"){
                FirebaseDatabase.getInstance().getReference().child("Likes")
                    .child(post.postid!!).child(firebaseUser.uid).setValue(true)
                addNotification(post.postid,post.publisher)
            }
            else{
                FirebaseDatabase.getInstance().getReference().child("Likes")
                    .child(post.postid!!).child(firebaseUser.uid).removeValue()
            }
        }

        holder.save.setOnClickListener(){

            if (holder.save.tag=="save"){
                FirebaseDatabase.getInstance().getReference().child("Saves")
                    .child(firebaseUser.uid).child(post.postid!!).setValue(true)
            }
            else{
                FirebaseDatabase.getInstance().getReference().child("Saves")
                    .child(firebaseUser.uid).child(post.postid!!).removeValue()
            }
        }

        holder.comment.setOnClickListener(){
            val intent = Intent(mContext,CommentActivity::class.java)
            intent.putExtra("postId",post.postid)
            intent.putExtra("authorId",post.publisher)
            mContext.startActivity(intent)
        }




    }
    override fun getItemCount(): Int {
        return mPosts.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageProfile: ImageView
        var postImage: ImageView
        var like: ImageView
        var comment: ImageView
        var save: ImageView
        var more: ImageView
        var username: TextView
        var noOfLikes: TextView
        var author: TextView
        var noOfComments: TextView
        var description: SocialTextView

        init {
            imageProfile = itemView.findViewById<ImageView>(R.id.image_profile)
            postImage = itemView.findViewById<ImageView>(R.id.post_image)
            like = itemView.findViewById<ImageView>(R.id.like)
            comment = itemView.findViewById<ImageView>(R.id.comment)
            save = itemView.findViewById<ImageView>(R.id.save)
            more = itemView.findViewById<ImageView>(R.id.more)
            username = itemView.findViewById<TextView>(R.id.username)
            noOfLikes = itemView.findViewById<TextView>(R.id.no_of_likes)
            author = itemView.findViewById<TextView>(R.id.author)
            noOfComments = itemView.findViewById<TextView>(R.id.no_of_comments)
            description = itemView.findViewById(R.id.description)
        }
    }

    private fun isLiked(postId:String,imageView: ImageView){
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                   if (snapshot.child(firebaseUser.uid).exists()){
                       imageView.setImageResource(R.drawable.ic_liked)
                       imageView.tag="liked"
                   }
                    else{
                       imageView.setImageResource(R.drawable.ic_like)
                       imageView.tag="like"
                   }

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun isSaved(postId:String,imageView: ImageView){
        FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(postId).exists()){
                        imageView.setImageResource(R.drawable.ic_save_black)
                        imageView.tag="saved"
                    }
                    else{
                        imageView.setImageResource(R.drawable.ic_save)
                        imageView.tag="save"
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun addNotification(postId: String,publisherId:String){
        val  map= HashMap<String,Any>()
        map["userid"]= firebaseUser.uid
        map["text"]= "liked you post"
        map["postid"]=postId
        map["isPost"]= true
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(publisherId).push().setValue(map)
    }
}