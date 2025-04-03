package com.example.elibrary.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.elibrary.R
import com.example.elibrary.databinding.ItemBorrowHistoryBinding
import com.example.elibrary.models.Transaction
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class BorrowHistoryAdapter(
    private var transactions: List<Transaction>,
) : RecyclerView.Adapter<BorrowHistoryAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(private val binding: ItemBorrowHistoryBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(transaction: Transaction) {
            binding.tvBookTitle.text = transaction.bookTitle
            binding.tvBorrowDate.text = formatDate(transaction.borrowDate)
            binding.tvDueDate.text = formatDate(transaction.dueDate)
            
            if (transaction.returnDate != null) {
                binding.tvReturnDate.text = formatDate(transaction.returnDate)
                binding.tvStatus.text = binding.root.context.getString(R.string.returned)
                binding.tvStatus.setTextColor(binding.root.context.getColor(R.color.green))
            } else {
                binding.tvReturnDate.text = binding.root.context.getString(R.string.not_returned)
                binding.tvStatus.text = binding.root.context.getString(R.string.borrowed)
                binding.tvStatus.setTextColor(binding.root.context.getColor(R.color.blue))
            }
        }

        private fun formatDate(timestamp: Timestamp?): String {
            if (timestamp == null) return ""
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            return sdf.format(timestamp.toDate())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemBorrowHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount() = transactions.size

    fun updateTransactions(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}