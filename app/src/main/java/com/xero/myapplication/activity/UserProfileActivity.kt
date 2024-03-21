package com.xero.myapplication.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.xero.myapplication.databinding.ActivityUserProfileBinding

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var preferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = this.getSharedPreferences("user", MODE_PRIVATE)

        loadUserInfo(preferences.getString("number", "")!!)

        binding.goToEditProfile.setOnClickListener{
            startActivity(Intent(this@UserProfileActivity, EditUserProfileActivity::class.java))
        }

    }

    private fun loadUserInfo(phoneNumber: String) {

        Firebase.firestore.collection("users").document(phoneNumber)
            .get()
            .addOnSuccessListener { document ->
                if(document.exists()){
                    val userName = document.getString("userName")
                    binding.textView15.text = "Name: $userName"

                    val userPhone = document.getString("userPhone")
                    binding.textView16.text = "Phone No: $userPhone"

                }
            }
    }
}