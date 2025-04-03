package com.example.elibrary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.elibrary.R
import com.example.elibrary.adapters.AdminBookAdapter
import com.example.elibrary.databinding.FragmentAdminBooksBinding
import com.example.elibrary.models.Book
import com.google.firebase.firestore.FirebaseFirestore

class AdminBooksFragment : Fragment() {
    private var _binding: FragmentAdminBooksBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdminBookAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadBooks()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = AdminBookAdapter(emptyList()) { book, action ->
            when(action) {
                "edit" -> editBook(book)
                "delete" -> deleteBook(book)
            }
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AdminBooksFragment.adapter
        }
    }

    private fun loadBooks() {
        db.collection("books")
            .get()
            .addOnSuccessListener { result ->
                val books = result.toObjects(Book::class.java)
                adapter.updateBooks(books)
            }
    }

    private fun editBook(book: Book) {
        // TODO: Implement book editing
    }

    private fun deleteBook(book: Book) {
        db.collection("books").document(book.id)
            .delete()
            .addOnSuccessListener {
                loadBooks() // Refresh the list
            }
    }

    private fun setupClickListeners() {
        binding.fabAddBook.setOnClickListener {
            // TODO: Implement add book functionality
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}