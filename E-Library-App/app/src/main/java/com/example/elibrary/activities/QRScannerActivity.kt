package com.example.elibrary.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.elibrary.databinding.ActivityQrscannerBinding
import com.example.elibrary.models.Book
import com.example.elibrary.models.Transaction
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import java.util.*

class QRScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrscannerBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var currentBook: Book? = null
    private var isBorrowOperation = true

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show()
        } else {
            handleScannedCode(result.contents)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrscannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        isBorrowOperation = intent.getBooleanExtra("is_borrow", true)
        binding.btnAction.text = if (isBorrowOperation) "Borrow Book" else "Return Book"

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnScan.setOnClickListener {
            val options = ScanOptions().apply {
                setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                setPrompt("Scan a book QR code")
                setCameraId(0)
                setBeepEnabled(true)
                setOrientationLocked(false)
            }
            barcodeLauncher.launch(options)
        }

        binding.btnAction.setOnClickListener {
            currentBook?.let { book ->
                if (isBorrowOperation) {
                    borrowBook(book)
                } else {
                    returnBook(book)
                }
            } ?: run {
                Toast.makeText(this, "Please scan a book first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleScannedCode(contents: String) {
        db.collection("books")
            .whereEqualTo("qrCode", contents)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Book not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                currentBook = documents.documents[0].toObject(Book::class.java)
                currentBook?.let { book ->
                    binding.tvBookTitle.text = book.title
                    binding.tvBookAuthor.text = book.author
                    binding.tvBookStatus.text = if (book.available) "Available" else "Checked Out"
                    binding.btnAction.isEnabled = book.available == isBorrowOperation
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun borrowBook(book: Book) {
        val userId = auth.currentUser?.uid ?: return
        val transaction = Transaction(
            userId = userId,
            bookId = book.id,
            bookTitle = book.title,
            borrowDate = Timestamp.now(),
            dueDate = Timestamp(Date().time + 14 * 24 * 60 * 60 * 1000) // 14 days from now
        )

        db.runTransaction { transaction ->
            val bookRef = db.collection("books").document(book.id)
            val bookDoc = transaction.get(bookRef)
            if (!bookDoc.getBoolean("available")!!) {
                throw Exception("Book is not available")
            }
            transaction.update(bookRef, "available", false)
            db.collection("transactions").add(transaction.toMap())
        }.addOnSuccessListener {
            Toast.makeText(this, "Book borrowed successfully", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun returnBook(book: Book) {
        val userId = auth.currentUser?.uid ?: return

        db.runTransaction { transaction ->
            val bookRef = db.collection("books").document(book.id)
            transaction.update(bookRef, "available", true)

            val query = db.collection("transactions")
                .whereEqualTo("bookId", book.id)
                .whereEqualTo("userId", userId)
                .whereEqualTo("returnDate", null)
                .limit(1)

            val docs = transaction.get(query)
            if (docs.isEmpty) {
                throw Exception("No active transaction found")
            }
            transaction.update(docs.documents[0].reference, "returnDate", Timestamp.now())
        }.addOnSuccessListener {
            Toast.makeText(this, "Book returned successfully", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}