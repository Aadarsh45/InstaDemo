package com.rj.geeksinstademo.adapters

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rj.geeksinstademo.R
import com.rj.geeksinstademo.model.Comment
import com.rj.geeksinstademo.model.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(private val mContext: Context, mComments: List<Comment>, postId: String) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder?>() {
    private val mComments: List<Comment>
    var postId: String
    private var fUser: FirebaseUser? = null

    init {
        this.mComments = mComments
        this.postId = postId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        fUser = FirebaseAuth.getInstance().getCurrentUser()
        val comment:Comment = mComments[position]
        holder.comment.setText(comment.comment)
        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.publisher!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: User = snapshot.getValue(User::class.java)!!
                    if (user.imageurl.equals("default")){
                        holder.imageProfile.setImageResource(R.drawable.profile_placeholder)
                    }else{
                        Picasso.get().load(user.imageurl).placeholder(R.drawable.profile_placeholder).into(holder.imageProfile)

                    }
                    holder.username.setText(user.username)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    override fun getItemCount(): Int {
        return mComments.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageProfile: CircleImageView
        var username: TextView
        var comment: TextView

        init {
            imageProfile = itemView.findViewById(R.id.image_profile)
            username = itemView.findViewById<TextView>(R.id.username)
            comment = itemView.findViewById<TextView>(R.id.comment)
        }
    }
}