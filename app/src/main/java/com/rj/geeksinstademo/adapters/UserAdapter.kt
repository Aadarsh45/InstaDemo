package com.rj.geeksinstademo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.rj.geeksinstademo.R
import com.rj.geeksinstademo.model.User
import com.squareup.picasso.Picasso

class UserAdapter (private val mContext: Context, mUser: List<User>,isFragment:Boolean) :
    RecyclerView.Adapter<UserAdapter.Viewholder?>() {
    private val mUser: List<User>
    private val firebaseUser: FirebaseUser
    private val isFragment:Boolean

    init {
        this.mUser = mUser
        this.isFragment = isFragment
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser()!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.Viewholder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false)
        return Viewholder(view)
    }
    override fun onBindViewHolder(holder: UserAdapter.Viewholder, position: Int) {
        val  user:User = mUser[position]
        holder.username.setText(user.username)
        holder.fullname.setText(user.name)
        holder.btnFollow.visibility = View.VISIBLE

        Picasso.get().load(user.imageurl).placeholder(R.drawable.profile_placeholder).into(holder.imageProfile)
        isFollowed(user.id!!,holder.btnFollow)
        if (user.id.equals(firebaseUser.uid)){
            holder.btnFollow.visibility = View.GONE
        }
        holder.btnFollow.setOnClickListener {
            if (holder.btnFollow.text.toString()=="follow"){
                FirebaseDatabase.getInstance().getReference().child("Follow")
                    .child(firebaseUser.uid).child("Following").child(user.id)
                    .setValue(true)
                FirebaseDatabase.getInstance().getReference().child("Follow")
                    .child(user.id).child("Followers").child(firebaseUser.uid)
                    .setValue(true)
                addNotification(user.id)
            }
            else{
                FirebaseDatabase.getInstance().getReference().child("Follow")
                    .child(firebaseUser.uid).child("Following").child(user.id)
                    .removeValue()
                FirebaseDatabase.getInstance().getReference().child("Follow")
                    .child(user.id).child("Followers").child(firebaseUser.uid)
                    .removeValue()
            }
        }

    }
    override fun getItemCount(): Int {
        return mUser.size
    }
    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageProfile: ImageView
        var username: TextView
        var fullname: TextView
        var btnFollow: Button


        init {
            imageProfile = itemView.findViewById<ImageView>(R.id.image_profile)
            username = itemView.findViewById<TextView>(R.id.username)
            fullname = itemView.findViewById<TextView>(R.id.fullname)
            btnFollow = itemView.findViewById<Button>(R.id.btn_follow)

        }
    }

    private fun isFollowed(id:String, btnFollow:Button){
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.uid)
            .child("Following")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(id).exists()){
                      btnFollow.text="following"
                    }else{
                        btnFollow.text="follow"

                    }


                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun addNotification(userId: String){
        val  map= HashMap<String,Any>()
        map["userid"]= firebaseUser.uid
        map["text"]= "started following you"
        map["postid"]=""
        map["isPost"]= false
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(userId).push().setValue(map)
    }
}