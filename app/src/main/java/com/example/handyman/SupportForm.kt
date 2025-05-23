package com.example.handyman

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SupportForm : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editSubject: EditText
    private lateinit var editMessage: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSubmit: Button

    private val categories = listOf("Technical Issue", "Billing & Payments", "Account Management", "Feature Request", "General Inquiry")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_form)

        // Initialize views
        editName = findViewById(R.id.editName)
        editEmail = findViewById(R.id.editEmail)
        editSubject = findViewById(R.id.editSubject)
        editMessage = findViewById(R.id.editMessage)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Set up Spinner items
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        btnSubmit.setOnClickListener {
            val name = editName.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val subject = editSubject.text.toString().trim()
            val category = spinnerCategory.selectedItem?.toString() ?: ""
            val message = editMessage.text.toString().trim()

            // Simple validation
            if (name.isEmpty() || email.isEmpty() || subject.isEmpty() || message.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase setup with your Realtime DB URL
            val database = FirebaseDatabase.getInstance("https://customerhandyman.firebaseio.com")
            val supportRequestsRef = database.getReference("support_requests")
            val newRequestRef = supportRequestsRef.push()

            val currentTimestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())

            val supportRequest = SupportRequest(
                name = name,
                email = email,
                subject = subject,
                message = message,
                category = category,
                status = "Open",
                createdAt = currentTimestamp
            )

            newRequestRef.setValue(supportRequest)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Support request sent!", Toast.LENGTH_LONG).show()
                        clearForm()
                    } else {
                        Toast.makeText(this, "Failed to send support request.", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun clearForm() {
        editName.text.clear()
        editEmail.text.clear()
        editSubject.text.clear()
        editMessage.text.clear()
        spinnerCategory.setSelection(0)
    }
}

// SupportRequest data class same as Compose example
data class SupportRequest(
    val name: String = "",
    val email: String = "",
    val subject: String = "",
    val message: String = "",
    val category: String = "",
    val status: String = "Open",
    val createdAt: String = ""
)
