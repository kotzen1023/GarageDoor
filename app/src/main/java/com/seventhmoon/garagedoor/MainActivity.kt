package com.seventhmoon.garagedoor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    // Write a message to the database
    private lateinit var database: DatabaseReference
    private val mTag = MainActivity::class.java.name

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val myRef = Firebase.database.reference

        val imgBtnUp = findViewById<ImageButton>(R.id.btnUp)
        val imgBtnStop = findViewById<ImageButton>(R.id.btnStop)
        val imgBtnDown = findViewById<ImageButton>(R.id.btnDown)

        imgBtnUp.setOnClickListener {
            Log.d(mTag, "imgBtnUp click!")

            myRef.child("door").child("up").child("on").setValue(true)
            imgBtnUp.isEnabled = false
            imgBtnStop.isEnabled = false
            imgBtnDown.isEnabled = false
        }

        imgBtnStop.setOnClickListener {
            Log.d(mTag, "imgBtnStop click!")

            myRef.child("door").child("stop").child("on").setValue(true)
            imgBtnUp.isEnabled = false
            imgBtnStop.isEnabled = false
            imgBtnDown.isEnabled = false
        }

        imgBtnDown.setOnClickListener {
            Log.d(mTag, "imgBtnDown click!")

            myRef.child("door").child("down").child("on").setValue(true)
            imgBtnUp.isEnabled = false
            imgBtnStop.isEnabled = false
            imgBtnDown.isEnabled = false
        }



        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val upOnOff = p0.child("door").child("up").child("on").value as Boolean
                val stopOnOff = p0.child("door").child("stop").child("on").value as Boolean
                val downOnOff = p0.child("door").child("down").child("on").value as Boolean


                if (!upOnOff && !stopOnOff && !downOnOff) {
                    imgBtnUp.isEnabled = true
                    imgBtnStop.isEnabled = true
                    imgBtnDown.isEnabled = true
                }


                Log.d(mTag, "upOnOff = $upOnOff")
                Log.d(mTag, "stopOnOff = $stopOnOff")
                Log.d(mTag, "downOnOff = $downOnOff")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(mTag, "onCancelled")
            }
        })


        if(auth.currentUser == null){
            Log.e(mTag, "Not Log in!")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }else{
            Log.e(mTag, "Already logged in")
        }
    }
}