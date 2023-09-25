package com.rj.geeksinstademo.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rj.geeksinstademo.R
import com.rj.geeksinstademo.adapters.NotificationAdapter
import com.rj.geeksinstademo.databinding.FragmentHomeBinding
import com.rj.geeksinstademo.databinding.FragmentNotificationBinding
import com.rj.geeksinstademo.databinding.FragmentSearchBinding
import com.rj.geeksinstademo.model.Notification
import com.rj.geeksinstademo.model.User
import java.util.Collections

class NotifiactionFragment : Fragment() {
    private lateinit var binding: FragmentNotificationBinding

    private lateinit var notificationAdapter:NotificationAdapter
    private lateinit var notificationlist:ArrayList<Notification>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(layoutInflater,container,false)
        val view = binding.root
        notificationlist =ArrayList<Notification>()
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager=linearLayoutManager
        notificationAdapter = NotificationAdapter(requireActivity(),notificationlist)
        binding.recyclerView.adapter =notificationAdapter
        readNotifications()
        return view
    }

    private fun readNotifications(){
        FirebaseDatabase.getInstance().getReference().child("Notifications")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    notificationlist!!.clear()
                    for (datashot in snapshot.children){
                        Log.i("OKKKKKKK",datashot.toString())

                        val notification: Notification = datashot.getValue(Notification::class.java)!!
                        Log.i("OKKKKKKK",notification.toString())
                        notificationlist.add(notification)
                    }
                    Collections.reverse(notificationlist)
                    notificationAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }
}