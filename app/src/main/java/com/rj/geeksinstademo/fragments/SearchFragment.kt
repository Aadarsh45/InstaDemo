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
import com.rj.geeksinstademo.adapters.UserAdapter
import com.rj.geeksinstademo.databinding.FragmentHomeBinding
import com.rj.geeksinstademo.databinding.FragmentSearchBinding
import com.rj.geeksinstademo.model.Post
import com.rj.geeksinstademo.model.User

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var mUsers:ArrayList<User>
    private lateinit var userAdapter:UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater,container,false)
        val view = binding.root
        mUsers = ArrayList<User>()
        val linearLayoutManager = LinearLayoutManager(context)

        binding.recyclerViewUsers.layoutManager = linearLayoutManager
        userAdapter = UserAdapter(requireActivity(),mUsers,true)
        binding.recyclerViewUsers.adapter = userAdapter
        readUsers()
        return view
    }
    private fun readUsers(){
        FirebaseDatabase.getInstance().getReference().child("Users")
            //.orderByChild("username").startAt("")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    mUsers!!.clear()
                    for (datashot in snapshot.children){
                        val user:User = datashot.getValue(User::class.java)!!
                        mUsers.add(user)
                    }
                    userAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }
}