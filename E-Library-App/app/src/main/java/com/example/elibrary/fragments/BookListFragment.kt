package com.example.elibrary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.elibrary.R
import com.example.elibrary.adapters.BookAdapter
import com.example.elibrary.databinding.FragmentBookListBinding
import com.example.elibrary.models.Book
import com.google.firebase.firestore.FirebaseFirestore

class BookListFragment : Fragment() {
    private var _binding: FragmentBookListBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookAdapter: BookAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadBooks()
        setupSearch()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(emptyList()) { book ->
            val intent = Intent(requireContext(), QRScannerActivity::class.java).apply {
                putExtra("is_borrow", book.available)
                putExtra("book_id", book.id)
            }
            startActivity(intent)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookAdapter
        }
    }

    private fun loadBooks() {
        db.collection("books")
            .get()
            .addOnSuccessListener { result ->
                val books = result.toObjects(Book::class.java)
                bookAdapter.updateBooks(books)
            }
            .addOnFailureListener { exception ->
                // TODO: Handle error
            }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchBooks(it) }
                return true
            }
        })
    }

    private fun searchBooks(query: String) {
        db.collection("books")
            .orderBy("title")
            .startAt(query)
            .endAt("$query\uf8ff")
            .get()
            .addOnSuccessListener { result ->
                val books = result.toObjects(Book::class.java)
                bookAdapter.updateBooks(books)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}