package com.rj.geeksinstademo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.rj.geeksinstademo.databinding.ActivityCommentBinding
import com.rj.geeksinstademo.databinding.ActivityOthersBinding

class OthersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOthersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOthersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logout.setOnClickListener(){
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@OthersActivity,LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()
        }
        binding.close.setOnClickListener(){
            finish()
        }
    }
}