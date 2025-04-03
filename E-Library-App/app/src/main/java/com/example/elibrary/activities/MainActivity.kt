package com.example.elibrary.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.elibrary.R
import com.example.elibrary.databinding.ActivityMainBinding
import com.example.elibrary.fragments.BookListFragment
import com.example.elibrary.fragments.ProfileFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        auth = FirebaseAuth.getInstance()
        
        // Set up bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_books -> {
                    replaceFragment(BookListFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                R.id.nav_admin -> {
                    // Check if user is admin before showing admin panel
                    if (isAdmin()) {
                        // TODO: Implement AdminFragment
                        true
                    } else {
                        false
                    }
                }
                else -> false
            }
        }
        
        // Set default fragment
        replaceFragment(BookListFragment())
    }
    
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    
    private fun isAdmin(): Boolean {
        // TODO: Implement admin check from Firestore
        return false
    }
}