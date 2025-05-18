package com.example.mockpaymentapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HandymanActivity : AppCompatActivity() {

    private val db = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handyman)

        val etJobId = findViewById<EditText>(R.id.etJobId)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val btnConfirm = findViewById<Button>(R.id.btnConfirm)

        btnConfirm.setOnClickListener {
            val jobId = etJobId.text.toString().trim()
            val expectedAmount = etAmount.text.toString().trim().toIntOrNull()

            if (jobId.isEmpty() || expectedAmount == null) {
                Toast.makeText(this, "Please enter a valid Job ID and amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val jobRef = db.child("Job").child(jobId)

            jobRef.get().addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    Toast.makeText(this, "Job not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val jobStatus = snapshot.child("jobStatus").value?.toString()?.trim()
                if (!jobStatus.equals("Done", ignoreCase = true)) {
                    Toast.makeText(this, "Job is not marked as 'Done' yet", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val salaryFrom = snapshot.child("jobSalaryFrom").value.toString().toIntOrNull()
                val salaryTo = snapshot.child("jobSalaryTo").value.toString().toIntOrNull()

                if (salaryFrom != null && salaryTo != null && (expectedAmount < salaryFrom || expectedAmount > salaryTo)) {
                    val rangeMessage = if (expectedAmount < salaryFrom) {
                        "The amount is LESS than the allowed range ($salaryFrom to $salaryTo)."
                    } else {
                        "The amount is MORE than the allowed range ($salaryFrom to $salaryTo)."
                    }

                    AlertDialog.Builder(this)
                        .setTitle("Amount Outside Range")
                        .setMessage("$rangeMessage\nDo you want to proceed anyway?")
                        .setPositiveButton("Proceed Anyway") { _, _ ->
                            confirmAmount(jobRef, jobId, expectedAmount)
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                } else {
                    confirmAmount(jobRef, jobId, expectedAmount)
                }

            }.addOnFailureListener {
                Toast.makeText(this, "Error accessing database", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmAmount(jobRef: com.google.firebase.database.DatabaseReference, jobId: String, amount: Int) {
        val updates = mapOf(
            "handyver" to true,
            "paymentamt" to amount
        )

        jobRef.updateChildren(updates).addOnSuccessListener {
            Toast.makeText(this, "Amount set successfully!", Toast.LENGTH_SHORT).show()
            findViewById<EditText>(R.id.etJobId).text.clear()
            findViewById<EditText>(R.id.etAmount).text.clear()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to update job", Toast.LENGTH_SHORT).show()
        }
    }
}
