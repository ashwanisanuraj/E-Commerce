package com.xero.myapplication.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.xero.myapplication.R
import com.xero.myapplication.databinding.ActivityEditUserProfileBinding

class EditUserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditUserProfileBinding
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = this.getSharedPreferences("user", MODE_PRIVATE)
        loadUserInfo(preferences.getString("number", "")!!)

        binding.editProfileButton.setOnClickListener {
            val updatedUserInfo = hashMapOf<String, Any>(
                "area" to binding.userArea.text.toString(),
                "city" to binding.userCity.text.toString(),
                "state" to binding.userState.text.toString(),
                "pinCode" to binding.userPin.text.toString()
            )
            updateUserInfo(preferences.getString("number", "")!!, updatedUserInfo)
            startActivity(Intent(this@EditUserProfileActivity, UserProfileActivity::class.java))
        }
    }


    private fun loadUserInfo(phoneNumber: String) {

        Firebase.firestore.collection("users").document(phoneNumber)
            .get()
            .addOnSuccessListener {
                binding.userName.setText(it.getString("userName"))
                binding.userPhone.setText(it.getString("userPhone"))
                binding.userArea.setText(it.getString("area"))
                binding.userCity.setText(it.getString("city"))
                binding.userState.setText(it.getString("state"))
                binding.userPin.setText(it.getString("pinCode"))
                /*
                 val userPhone = document.getString("userPhone")
                 val userArea = document.getString("area")
                 val userCity = document.getString("city")
                 val userState = document.getString("state")
                 val userPin = document.getString("pinCode")
                 */
            }
    }

    private fun updateUserInfo(phoneNumber: String, updatedUserInfo: Map<String, Any>) {
        Firebase.firestore.collection("users").document(phoneNumber)
            .update(updatedUserInfo)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile Update Successful", Toast.LENGTH_SHORT).show()
            }
    }
}