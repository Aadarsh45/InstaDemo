package com.rj.geeksinstademo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.rj.geeksinstademo.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        binding.login.setOnClickListener(){
        startActivity(Intent(this@IntroActivity,LoginActivity::class.java))
            finish()
        }
        binding.register.setOnClickListener(){
            startActivity(Intent(this@IntroActivity,RegisterActivity::class.java))
            finish()

        }
    }

    private fun init(){
        Handler(Looper.getMainLooper()).postDelayed({

            if (FirebaseAuth.getInstance().currentUser!=null){
                startActivity(Intent(this@IntroActivity,MainActivity::class.java))
                finish()
            }else{
                binding.login.visibility = View.VISIBLE
                binding.register.visibility = View.VISIBLE
            }


        },3000)
    }
}