package com.rj.geeksinstademo

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rj.geeksinstademo.databinding.ActivityCommentBinding
import com.rj.geeksinstademo.databinding.ActivityEditProfileBinding
import com.rj.geeksinstademo.model.User
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

//Use to update user details and upload user profile image
class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var fUser: FirebaseUser
    private lateinit var mImageUri: Uri
    private lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Get data for current user and set it on the respective fields
        fUser = FirebaseAuth.getInstance().getCurrentUser()!!
        storageRef = FirebaseStorage.getInstance().getReference().child("Uploads")
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user: User = dataSnapshot.getValue(User::class.java)!!
                    binding.fullname.setText(user.name)
                    binding.username.setText(user.username)
                    binding.bio.setText(user.bio)
                    Picasso.get().load(user.imageurl).into(binding.imageProfile)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        binding.close!!.setOnClickListener { finish() }
        binding.changePhoto.setOnClickListener(View.OnClickListener {
            CropImage.activity().setCropShape(CropImageView.CropShape.OVAL)
                .start(this@EditProfileActivity)
        })
        //Select profile image from gallery with circular shape crop
        binding.imageProfile.setOnClickListener(View.OnClickListener {
            CropImage.activity().setCropShape(CropImageView.CropShape.OVAL)
                .start(this@EditProfileActivity)
        })
        binding.save.setOnClickListener(View.OnClickListener { updateProfile() })
    }

    private fun updateProfile() {
        val map = HashMap<String, Any>()
        map["fullname"] = binding.fullname.getText().toString()
        map["username"] = binding.username.getText().toString()
        map["bio"] = binding.bio.getText().toString()
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid())
            .updateChildren(map)
    }

    //Upload user profile for firebase storage and uploaded in Users folder
    private fun uploadImage() {
        val pd = ProgressDialog(this)
        pd.setMessage("Uploading")
        pd.show()
        if (mImageUri != null) {
            val fileRef: StorageReference =
                storageRef.child(System.currentTimeMillis().toString() + ".jpeg")
            //uploadTask = fileRef.putFile(mImageUri)
            fileRef.putFile(mImageUri).addOnSuccessListener {task->task.storage.downloadUrl.addOnSuccessListener{
                val url = it.toString()
                Log.i("OKKKKKKKKKKKK",url)
                FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(fUser.getUid()).child("imageurl").setValue(url)
                pd.dismiss()
            }
            }.addOnFailureListener(){
                Toast.makeText(
                    this@EditProfileActivity,
                    "Upload failed!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            mImageUri = result.getUri()
            uploadImage()
        } else {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }
}