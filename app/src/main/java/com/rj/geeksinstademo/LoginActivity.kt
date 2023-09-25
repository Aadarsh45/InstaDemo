package com.rj.geeksinstademo

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.rj.geeksinstademo.databinding.ActivityIntroBinding
import com.rj.geeksinstademo.databinding.ActivityLoginBinding
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth:FirebaseAuth
    private lateinit var pd: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        pd = ProgressDialog(this)

        binding.registerUser.setOnClickListener(){
            startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
            finish()
        }
        binding.login.setOnClickListener(){

            val txtEmail:String = binding.email.text.toString()
            val txtPassword:String = binding.password.text.toString()

            if (TextUtils.isEmpty(txtEmail)|| TextUtils.isEmpty(txtPassword)){
                Toast.makeText(this,"Empty Credentials", Toast.LENGTH_SHORT).show()
            }else if(txtPassword.length<5) {
                Toast.makeText(this,"Password too short!", Toast.LENGTH_SHORT).show()
            }
            else{
                pd.setMessage("Please Wait")
                pd.show()

                mAuth.signInWithEmailAndPassword(txtEmail,txtPassword)
                    .addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                        override fun onComplete(task: Task<AuthResult?>) {
                            if (task.isSuccessful){
                                pd.dismiss()
                                Toast.makeText(this@LoginActivity,"Login succesfully",Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                                finish()
                            }
                        }


                    }).addOnFailureListener(object: OnFailureListener {
                        override fun onFailure(p0: Exception) {
                            pd.dismiss()
                            Toast.makeText(this@LoginActivity,p0.message, Toast.LENGTH_SHORT).show()
                        }

                    })
            }
        }
    }
}