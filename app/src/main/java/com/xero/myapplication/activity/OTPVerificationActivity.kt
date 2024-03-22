package com.xero.myapplication.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.xero.myapplication.MainActivity
import com.xero.myapplication.databinding.ActivityOtpverificationBinding

class OTPVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpverificationBinding
    private lateinit var imm: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpverificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        binding.verifyOtp.setOnClickListener {
            if (binding.userOtp.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please Enter OTP", Toast.LENGTH_SHORT).show()
            } else {
                verifyUser(binding.userOtp.text.toString())
            }
        }
    }

    private lateinit var builder: androidx.appcompat.app.AlertDialog

    private fun verifyUser(otp: String) {
        hideKeyboard()

        builder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Verifying OTP...")
            .setMessage("Please Wait")
            .setCancelable(false)
            .create()
        builder.show()

        val verificationId = intent.getStringExtra("verificationId")
        if (verificationId.isNullOrEmpty()) {
            Toast.makeText(this, "Verification ID is null or empty", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val preferences = this.getSharedPreferences("user", MODE_PRIVATE)
                    val editor = preferences.edit()
                    editor.putString("number", intent.getStringExtra("number")!!)
                    editor.apply()

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
                        Log.e("OTPVerificationActivity", "Invalid OTP", exception)
                    } else {
                        Toast.makeText(
                            this,
                            "Authentication failed: ${exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("OTPVerificationActivity", "Authentication failed", exception)
                    }
                }
            }
    }
    private fun hideKeyboard() {
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}
