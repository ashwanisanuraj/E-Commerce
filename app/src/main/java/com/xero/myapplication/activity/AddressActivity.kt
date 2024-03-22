package com.xero.myapplication.activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.xero.myapplication.databinding.ActivityAddressBinding

class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var totalCost: String
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = this.getSharedPreferences("user", MODE_PRIVATE)
        totalCost = intent.getStringExtra("totalCost")!!
        loadUserInfo()

        binding.proceedCheckout.setOnClickListener {
            showLoadingDialog()
            validateData(
                binding.userName.text.toString(),
                binding.userPhone.text.toString(),
                binding.userArea.text.toString(),
                binding.userCity.text.toString(),
                binding.userState.text.toString(),
                binding.userPin.text.toString(),
            )
        }

        binding.editProfileFromAddress.setOnClickListener {
            startActivity(Intent(this@AddressActivity, UserProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        dismissLoadingDialog()
    }

    private fun showLoadingDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Loading...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun dismissLoadingDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    private fun validateData(
        name: String,
        number: String,
        area: String,
        city: String,
        state: String,
        pinCode: String
    ) {
        if (name.isEmpty() || number.isEmpty() || area.isEmpty() || city.isEmpty() || state.isEmpty() || pinCode.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            dismissLoadingDialog()
        } else {
            storeData(area, city, state, pinCode)
        }
    }

    private fun storeData(area: String, city: String, state: String, pinCode: String) {
        val map = hashMapOf<String, Any>()
        map["area"] = area
        map["city"] = city
        map["state"] = state
        map["pinCode"] = pinCode

        Firebase.firestore.collection("users")
            .document(preferences.getString("number", "")!!)
            .update(map).addOnSuccessListener {
                dismissLoadingDialog()
                val b = Bundle()
                b.putStringArrayList("productIds", intent.getStringArrayListExtra("productIds"))
                b.putString("totalCost", totalCost)
                val intent = Intent(this, CheckoutActivity::class.java)
                intent.putExtras(b)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show()
                dismissLoadingDialog()
            }
    }

    private fun loadUserInfo() {
        Firebase.firestore.collection("users").document(preferences.getString("number", "")!!)
            .get().addOnSuccessListener {
                binding.userName.setText(it.getString("userName"))
                binding.userPhone.setText(it.getString("userPhone"))
                binding.userArea.setText(it.getString("area"))
                binding.userCity.setText(it.getString("city"))
                binding.userState.setText(it.getString("state"))
                binding.userPin.setText(it.getString("pinCode"))
            }
    }
}
