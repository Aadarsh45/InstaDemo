package com.rj.geeksinstademo

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.rj.geeksinstademo.databinding.ActivityLoginBinding
import com.rj.geeksinstademo.databinding.ActivityPostBinding
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.Locale

class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private lateinit var imageUri: Uri
    private lateinit var imageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.close.setOnClickListener(){
            startActivity(Intent(this@PostActivity,MainActivity::class.java))
            finish()
        }
        binding.post.setOnClickListener(){
            upload()
        }
        CropImage.activity().setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this@PostActivity)
    }

    private fun upload() {
        val pd = ProgressDialog(this)
        pd.setMessage("Uploading")
        pd.show()
        if (imageUri != null) {
            val filePath: StorageReference =
                FirebaseStorage.getInstance().getReference("Posts").child(
                    System.currentTimeMillis().toString() + ".jpg"
                )
            filePath?.putFile(imageUri!!)?.addOnSuccessListener(
                OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                        imageUrl = it.toString()
                        Log.i("OKKKKKKKKKKKKKK",imageUrl)
                        val ref: DatabaseReference =
                            FirebaseDatabase.getInstance().getReference("Posts")
                        val postId: String = ref.push().getKey()!!
                        val map = HashMap<String, Any>()
                        map["postid"] = postId
                        map["imageurl"] = imageUrl!!
                        map["description"] = binding.description.getText().toString()
                        map["publisher"] = FirebaseAuth.getInstance().getCurrentUser()!!.getUid()
                        ref.child(postId).setValue(map)
                        pd.dismiss()
                        startActivity(Intent(this@PostActivity, MainActivity::class.java))
                        finish()
                        pd.dismiss()

                    }
                })

                ?.addOnFailureListener(OnFailureListener { e ->
                    Toast.makeText(
                        this@PostActivity,
                        "Upload failed!",
                        Toast.LENGTH_SHORT
                    ).show()
                })
        } else {
            Toast.makeText(this, "No image was selected!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val  result:CropImage.ActivityResult = CropImage.getActivityResult(data)

            imageUri = result.uri
            binding.imageAdd.setImageURI(imageUri)

        }else{
            Toast.makeText(this@PostActivity,"Try again!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@PostActivity,MainActivity::class.java))
            finish()
        }
    }
}