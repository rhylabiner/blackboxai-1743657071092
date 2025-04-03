package com.example.elibrary.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.elibrary.R
import com.example.elibrary.databinding.ItemAdminBookBinding
import com.example.elibrary.models.Book
import com.squareup.picasso.Picasso

class AdminBookAdapter(
    private var books: List<Book>,
    private val onActionClick: (Book, String) -> Unit
) : RecyclerView.Adapter<AdminBookAdapter.BookViewHolder>() {

    inner class BookViewHolder(private val binding: ItemAdminBookBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(book: Book) {
            binding.tvTitle.text = book.title
            binding.tvAuthor.text = book.author
            binding.tvStatus.text = if (book.available) "Available" else "Checked Out"

            if (book.imageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(book.imageUrl)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(binding.ivBook)
            }

            binding.btnEdit.setOnClickListener {
                onActionClick(book, "edit")
            }

            binding.btnDelete.setOnClickListener {
                onActionClick(book, "delete")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemAdminBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount() = books.size

    fun updateBooks(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}