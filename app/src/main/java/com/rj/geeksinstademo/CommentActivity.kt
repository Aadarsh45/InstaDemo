package com.rj.geeksinstademo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rj.geeksinstademo.adapters.CommentAdapter
import com.rj.geeksinstademo.databinding.ActivityCommentBinding
import com.rj.geeksinstademo.databinding.ActivityLoginBinding
import com.rj.geeksinstademo.model.Comment
import com.rj.geeksinstademo.model.User

class CommentActivity : AppCompatActivity() {

    private lateinit var binding:ActivityCommentBinding
    private lateinit var postId:String
    private lateinit var authorId:String
    private lateinit var fUser:FirebaseUser

    private lateinit var commentList:ArrayList<Comment>
    private lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title="Comments"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener(){
            finish()
        }
        commentList= ArrayList<Comment>()
        val  intent:Intent = getIntent()
        postId = intent.getStringExtra("postId")!!
        authorId = intent.getStringExtra("authorId")!!
        fUser = FirebaseAuth.getInstance().currentUser!!
        val linearLayoutManager = LinearLayoutManager(this@CommentActivity)

        binding.recyclerView.layoutManager = linearLayoutManager

        commentAdapter = CommentAdapter(this@CommentActivity,commentList,postId)
        binding.recyclerView.adapter= commentAdapter
        readComment()
        binding.post.setOnClickListener(){
            putComment()
        }
    }

    private fun readComment(){
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    commentList!!.clear()
                    for (datashot in snapshot.children){
                        val comment: Comment = datashot.getValue(Comment::class.java)!!
                        commentList.add(comment)
                    }
                    commentAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun putComment(){

        val  map= HashMap<String,Any>()
        val ref:DatabaseReference = FirebaseDatabase.getInstance().getReference().child("Comments")
            .child(postId)
        val id:String = ref.push().key!!

        map["id"]= id
        map["comment"]= binding.addComment.text.toString()
        map["publisher"]=fUser.uid
        binding.addComment.setText("")

        ref.child(id).setValue(map).addOnCompleteListener(object : OnCompleteListener<Void?> {
            override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful){
                    Toast.makeText(this@CommentActivity,"Comment added!", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@CommentActivity,task.exception!!.message, Toast.LENGTH_SHORT).show()

                }
            }

        })
    }
}