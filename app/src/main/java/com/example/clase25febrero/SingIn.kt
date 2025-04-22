package com.example.clase25febrero

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clase25febrero.databinding.ActivitySingInBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class SingIn : AppCompatActivity() {
    lateinit var binding: ActivitySingInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.buttonSINGIN.setOnClickListener {
            signInUser(binding.editTextText2.text.toString(),binding.editTextTextPassword.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("user", currentUser.email)
            startActivity(intent)
        } else {
            binding.editTextText2.setText("")
            binding.editTextTextPassword.setText("")
        }
    }

    private fun validateForm(): Boolean {
        var valid = true
        val email = binding.editTextText2.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.editTextText2.error = "Required."
            valid = false
        } else {
            binding.editTextText2.error = null
        }
        val password = binding.editTextTextPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.editTextTextPassword.error = "Required."
            valid = false
        } else {
            binding.editTextTextPassword.error = null
        }
        return valid
    }

    private fun signInUser(email: String, password: String){
        if(validateForm() && isEmailValid(email)){
            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                    // Sign in success, update UI
                        Log.d("Singin", "signInWithEmail:success:")
                        val user = auth.currentUser
                        updateUI(auth.currentUser)
                    } else {
                        Log.w("Singin", "signInWithEmail:failure", task.exception)
                        Toast.makeText(this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        if (!email.contains("@") ||
            !email.contains(".") ||
            email.length < 5)
            return false
        return true
    }

}