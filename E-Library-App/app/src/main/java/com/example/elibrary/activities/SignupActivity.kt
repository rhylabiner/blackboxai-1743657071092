package com.example.elibrary.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.elibrary.R
import com.example.elibrary.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSignup.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInput(email, password, confirmPassword)) {
                registerUser(email, password)
            }
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateInput(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (email.isEmpty()) {
            binding.etEmail.error = getString(R.string.error_required_field)
            return false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = getString(R.string.error_required_field)
            return false
        }

        if (password.length < 6) {
            binding.etPassword.error = getString(R.string.error_password_length)
            return false
        }

        if (confirmPassword != password) {
            binding.etConfirmPassword.error = getString(R.string.error_password_mismatch)
            return false
        }

        return true
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        // Create user document in Firestore
                        val userData = hashMapOf(
                            "email" to email,
                            "role" to "user", // Default role
                            "createdAt" to System.currentTimeMillis()
                        )

                        db.collection("users")
                            .document(user.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Registration successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Failed to create user profile: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}