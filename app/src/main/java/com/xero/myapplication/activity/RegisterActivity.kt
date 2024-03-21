package com.xero.myapplication.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firestore.v1.FirestoreGrpc.FirestoreBlockingStub
import com.xero.myapplication.R
import com.xero.myapplication.databinding.ActivityRegisterBinding
import com.xero.myapplication.model.UserModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginfromregisterBtn.setOnClickListener {
            openLogin()
        }

        binding.registerBtn.setOnClickListener {
            validateUser()
        }

    }

    private fun validateUser() {
        if (binding.userNameReg.text!!.isEmpty() || binding.userNumberReg.text!!.isEmpty()) {
            Toast.makeText(this, "please Fill All The Fields", Toast.LENGTH_SHORT).show()
        } else {
            storeData()
        }
    }

    private fun storeData() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Loading...")
            .setMessage("Please Wait")
            .setCancelable(false)
            .create()
        builder.show()

        val preferences = this.getSharedPreferences("user", MODE_PRIVATE)
        val editor = preferences.edit()

        editor.putString("name", binding.userNameReg.text.toString())
        editor.putString("number", binding.userNumberReg.text.toString())
        editor.apply()

        val data = UserModel(
            userName = binding.userNameReg.text.toString(),
            userPhone = binding.userNumberReg.text.toString()
        )

        Firebase.firestore.collection("users").document(binding.userNumberReg.text.toString())
            .set(data).addOnSuccessListener {
                Toast.makeText(this, "User Registered", Toast.LENGTH_SHORT).show()
                builder.dismiss()
                openLogin()

            }
            .addOnFailureListener {
                builder.dismiss()
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()

    }
}