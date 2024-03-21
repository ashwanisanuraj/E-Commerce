package com.xero.myapplication.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.xero.myapplication.databinding.ActivityLoginBinding
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var imm: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        binding.registerfromloginBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.logInBtn.setOnClickListener {
            if (binding.userNumber.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please Enter Number", Toast.LENGTH_SHORT).show()
            } else {
                sendOtp(binding.userNumber.text.toString())
            }
        }
    }

    private lateinit var builder: androidx.appcompat.app.AlertDialog

    private fun sendOtp(number: String) {
        hideKeyboard()
        builder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Sending OTP...")
            .setMessage("GOOGLE MAY VERIFY IF YOU ARE A HUMAN!!!")
            .setCancelable(false)
            .create()
        builder.show()

        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber("+91$number") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Auto-retrieval of OTP completed (not usually needed)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            builder.dismiss()
            Toast.makeText(
                this@LoginActivity,
                "Verification failed: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            Log.e("LoginActivity", "Verification failed", e)
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            builder.dismiss()
            val intent = Intent(this@LoginActivity, OTPVerificationActivity::class.java)
            intent.putExtra("verificationId", verificationId)
            intent.putExtra("number", binding.userNumber.text.toString())
            startActivity(intent)
        }
    }
    private fun hideKeyboard() {
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}
