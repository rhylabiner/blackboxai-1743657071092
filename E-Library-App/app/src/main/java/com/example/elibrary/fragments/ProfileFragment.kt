package com.example.elibrary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.elibrary.R
import com.example.elibrary.adapters.BorrowHistoryAdapter
import com.example.elibrary.databinding.FragmentProfileBinding
import com.example.elibrary.models.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var historyAdapter: BorrowHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        loadUserData()
        loadBorrowingHistory()
    }

    private fun setupRecyclerView() {
        historyAdapter = BorrowHistoryAdapter(emptyList())
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser
        user?.let {
            binding.tvUserName.text = user.email
            binding.tvUserEmail.text = user.email

            // Load profile picture if available
            user.photoUrl?.let { photoUrl ->
                Picasso.get()
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(binding.ivProfile)
            }
        }
    }

    private fun loadBorrowingHistory() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("transactions")
            .whereEqualTo("userId", userId)
            .orderBy("borrowDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val transactions = result.toObjects(Transaction::class.java)
                historyAdapter.updateTransactions(transactions)
                
                // Update current books count
                val currentBooks = transactions.count { it.returnDate == null }
                binding.tvCurrentBooks.text = getString(R.string.current_books_count, currentBooks)
            }
            .addOnFailureListener { exception ->
                // TODO: Handle error
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}