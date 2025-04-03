package com.example.elibrary.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.elibrary.R
import com.example.elibrary.databinding.ItemBookBinding
import com.example.elibrary.models.Book
import com.squareup.picasso.Picasso

class BookAdapter(
    private var books: List<Book>,
    private val onItemClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(private val binding: ItemBookBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(book: Book) {
            binding.bookTitle.text = book.title
            binding.bookAuthor.text = book.author
            binding.bookGenre.text = book.genre
            binding.bookAvailability.text = if (book.available) {
                binding.root.context.getString(R.string.available)
            } else {
                binding.root.context.getString(R.string.unavailable)
            }

            if (book.imageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(book.imageUrl)
                    .placeholder(R.drawable.ic_book_placeholder)
                    .into(binding.bookImage)
            }

            binding.btnAction.setOnClickListener {
                onItemClick(book)
            }
            
            binding.btnAction.text = if (book.available) {
                binding.root.context.getString(R.string.borrow_book)
            } else {
                binding.root.context.getString(R.string.return_book)
            }
            binding.btnAction.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(
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