package com.rj.geeksinstademo.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rj.geeksinstademo.EditProfileActivity
import com.rj.geeksinstademo.OthersActivity
import com.rj.geeksinstademo.R
import com.rj.geeksinstademo.adapters.PhotoAdapter
import com.rj.geeksinstademo.databinding.FragmentHomeBinding
import com.rj.geeksinstademo.databinding.FragmentProfileBinding
import com.rj.geeksinstademo.databinding.FragmentSearchBinding
import com.rj.geeksinstademo.model.Post
import com.rj.geeksinstademo.model.User
import com.squareup.picasso.Picasso
import java.util.Collections

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var postAdapterSaves: PhotoAdapter
    private lateinit var mySavedPosts: ArrayList<Post>
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var myPhotoList: ArrayList<Post>
    private lateinit var fUser: FirebaseUser
    var  profileId: String=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater,container,false)
        val view = binding.root
        binding.options.setOnClickListener(){
            startActivity(Intent(requireActivity(),OthersActivity::class.java))
        }
        fUser = FirebaseAuth.getInstance().getCurrentUser()!!
        profileId = fUser.getUid()
        binding.recuclerViewPictures.setHasFixedSize(true)
        binding.recuclerViewPictures.setLayoutManager(GridLayoutManager(context, 3))
        myPhotoList = ArrayList<Post>()
        photoAdapter = PhotoAdapter(requireActivity(), myPhotoList)
        binding.recuclerViewPictures.setAdapter(photoAdapter)
        binding.recuclerViewSaved.setHasFixedSize(true)
        binding.recuclerViewSaved.setLayoutManager(GridLayoutManager(context, 3))
        mySavedPosts = ArrayList<Post>()
        postAdapterSaves = PhotoAdapter(requireActivity(), mySavedPosts)
        binding.recuclerViewSaved.setAdapter(postAdapterSaves)
        //Fetch User Info
        userInfo()
        //Fetch Follower & following count
        followersAndFollowingCount()
        //Fetch no of post count
        postCount()
        //Fetch no post posted by user
        myPhotos()
        //Fetch all saved post
        getSavedPost()
        binding.recuclerViewPictures.setVisibility(View.VISIBLE)
        binding.recuclerViewSaved.setVisibility(View.GONE)
        binding.myPictures.setOnClickListener(View.OnClickListener {
            binding.recuclerViewPictures.setVisibility(View.VISIBLE)
            binding.recuclerViewSaved.setVisibility(View.GONE)
        })
        binding.savedPictures.setOnClickListener(View.OnClickListener {
            binding.recuclerViewPictures.setVisibility(View.GONE)
            binding.recuclerViewSaved.setVisibility(View.VISIBLE)
        })
        binding.editProfile.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, EditProfileActivity::class.java))
        })
        return view

    }

    private fun getSavedPost(){
        val savedIds: MutableList<String> = ArrayList()
        FirebaseDatabase.getInstance().getReference().child("Saves").child(fUser.getUid())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.getChildren()) {
                        savedIds.add(snapshot.getKey()!!)
                    }
                    FirebaseDatabase.getInstance().getReference().child("Posts")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot1: DataSnapshot) {
                                mySavedPosts!!.clear()
                                for (snapshot1 in dataSnapshot1.getChildren()) {
                                    val post: Post = snapshot1.getValue(Post::class.java)!!
                                    for (id in savedIds) {
                                        if (post.postid.equals(id)) {
                                            mySavedPosts!!.add(post)
                                        }
                                    }
                                }
                                postAdapterSaves.notifyDataSetChanged()
                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        })
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

    }

    private fun myPhotos() {
        FirebaseDatabase.getInstance().getReference().child("Posts")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    myPhotoList!!.clear()
                    for (snapshot in dataSnapshot.getChildren()) {
                        val post: Post = snapshot.getValue(Post::class.java)!!
                        if (post.publisher.equals(profileId)) {
                            myPhotoList!!.add(post)
                        }
                    }
                    Collections.reverse(myPhotoList)
                    photoAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }


    private fun postCount(){
            FirebaseDatabase.getInstance().getReference().child("Posts")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var counter = 0
                        for (snapshot in dataSnapshot.getChildren()) {
                            val post: Post = snapshot.getValue(Post::class.java)!!
                            if (post.publisher.equals(profileId)) counter++
                        }
                        binding.posts.setText(counter.toString())
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        }
    private fun followersAndFollowingCount() {
            val ref: DatabaseReference =
                FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
            ref.child("Followers").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.i("OKKKKKKKKKKK",dataSnapshot.toString())
                    binding.followers.setText("" + dataSnapshot.getChildrenCount())
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
            ref.child("Following").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.i("OKKKKKKKKKKK2",dataSnapshot.toString())

                    binding.following.setText("" + dataSnapshot.getChildrenCount())
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    private fun userInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user: User = dataSnapshot.getValue(User::class.java)!!
                    Picasso.get().load(user.imageurl).placeholder(R.drawable.profile_placeholder).into(binding.imageProfile)
                    binding.username.setText(user.username)
                    binding.fullname.setText(user.name)
                    binding.bio.setText(user.bio)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

}