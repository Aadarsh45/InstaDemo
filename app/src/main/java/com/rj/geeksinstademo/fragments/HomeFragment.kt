package com.rj.geeksinstademo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rj.geeksinstademo.R
import com.rj.geeksinstademo.adapters.PostAdapter
import com.rj.geeksinstademo.databinding.FragmentHomeBinding
import com.rj.geeksinstademo.model.Post

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var postAdapter: PostAdapter
    private lateinit var postList:ArrayList<Post>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        val view = binding.root
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerViewPosts.layoutManager=linearLayoutManager
        postList = ArrayList<Post>()
        postAdapter = PostAdapter(requireActivity(),postList)
        binding.recyclerViewPosts.adapter = postAdapter
        readPost()
        return view

    }


    private fun readPost(){
        FirebaseDatabase.getInstance().getReference().child("Posts")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    postList!!.clear()
                    for (datashot in snapshot.children){
                        val post:Post = datashot.getValue(Post::class.java)!!
                        postList.add(post)
                    }
                    postAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

}