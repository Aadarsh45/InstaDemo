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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rj.geeksinstademo.databinding.ActivityIntroBinding
import com.rj.geeksinstademo.databinding.ActivityLoginBinding
import com.rj.geeksinstademo.databinding.ActivityRegisterBinding
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var mRootRef:DatabaseReference
    private lateinit var  mAuth: FirebaseAuth
    private lateinit var pd:ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mRootRef = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        pd = ProgressDialog(this)

        binding.loginUser.setOnClickListener(){
            startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
            finish()
        }
        binding.register.setOnClickListener(){
            val txtUserName:String = binding.username.text.toString()
            val txtName:String = binding.name.text.toString()
            val txtEmail:String = binding.email.text.toString()
            val txtPassword:String = binding.password.text.toString()

            if (TextUtils.isEmpty(txtUserName)||TextUtils.isEmpty(txtName)||
                TextUtils.isEmpty(txtEmail)||TextUtils.isEmpty(txtPassword)){
                Toast.makeText(this,"Empty Credentials",Toast.LENGTH_SHORT).show()
            }else if(txtPassword.length<5) {
                Toast.makeText(this,"Password too short!",Toast.LENGTH_SHORT).show()
            }
            else{
                pd.setMessage("Please Wait")
                pd.show()

                mAuth.createUserWithEmailAndPassword(txtEmail,txtPassword)
                    .addOnSuccessListener(object :OnSuccessListener<AuthResult?>{
                        override fun onSuccess(authResult: AuthResult?) {
                         val map = HashMap<String,Any>()
                            map["name"] = txtName
                            map["email"] = txtEmail
                            map["username"]= txtUserName
                            map["id"] = mAuth.currentUser!!.uid
                            map["bio"]=""
                            map["imageurl"]="default"

                            mRootRef.child("Users").child(mAuth.currentUser!!.uid).setValue(map)
                                .addOnCompleteListener(object :OnCompleteListener<Void?>{
                                    override fun onComplete(task: Task<Void?>) {
                                        if (task.isSuccessful){
                                            pd.dismiss()
                                            Toast.makeText(this@RegisterActivity,"Profile created succesfully",Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this@RegisterActivity,MainActivity::class.java))
                                            finish()
                                        }
                                    }

                                })

                        }

                    }).addOnFailureListener(object:OnFailureListener{
                        override fun onFailure(p0: Exception) {
                            pd.dismiss()
                            Toast.makeText(this@RegisterActivity,p0.message,Toast.LENGTH_SHORT).show()
                        }

                    })
            }
        }
    }
}