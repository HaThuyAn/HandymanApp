package com.example.mockpaymentapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private val db = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etJobId = findViewById<EditText>(R.id.etJobId)
        val etPaymentAmount = findViewById<EditText>(R.id.etPaymentAmount)
        val btnPayCash = findViewById<Button>(R.id.btnPayCash)
        val btnPayBkash = findViewById<Button>(R.id.btnPayBkash)

        fun checkPaymentStatus(jobId: String) {
            val jobRef = db.child("Job").child(jobId)

            jobRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val paymentStatus = snapshot.child("paymentStatus").value?.toString() ?: ""

                    if (paymentStatus == "success") {
                        Toast.makeText(this, "Payment already completed for this job.", Toast.LENGTH_LONG).show()
                        btnPayCash.isEnabled = false
                        btnPayBkash.isEnabled = false
                    } else {
                        btnPayCash.isEnabled = true
                        btnPayBkash.isEnabled = true
                    }
                } else {
                    Toast.makeText(this, "Job not found", Toast.LENGTH_SHORT).show()
                    btnPayCash.isEnabled = false
                    btnPayBkash.isEnabled = false
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error checking payment status", Toast.LENGTH_SHORT).show()
                btnPayCash.isEnabled = false
                btnPayBkash.isEnabled = false
            }
        }

        fun handlePayment(jobId: String, amount: Int, paymentMethod: String) {
            val jobRef = db.child("Job").child(jobId)

            jobRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val jobStatus = snapshot.child("jobStatus").value?.toString()?.trim()
                    val handyVerified = snapshot.child("handyver").value as? Boolean ?: false
                    val expectedAmount = snapshot.child("paymentamt").value.toString().toIntOrNull()
                    val paymentStatus = snapshot.child("paymentStatus").value?.toString()

                    if (paymentStatus == "success") {
                        Toast.makeText(this, "Payment already completed.", Toast.LENGTH_LONG).show()
                        return@addOnSuccessListener
                    }

                    if (jobStatus != "Done") {
                        Toast.makeText(this, "Job is not marked as completed yet.", Toast.LENGTH_LONG).show()
                        return@addOnSuccessListener
                    }

                    if (!handyVerified || expectedAmount == null) {
                        Toast.makeText(this, "Handyman has not verified the payment yet.", Toast.LENGTH_LONG).show()
                        return@addOnSuccessListener
                    }

                    if (amount != expectedAmount) {
                        Toast.makeText(this, "You must pay exactly $expectedAmount as confirmed by the handyman.", Toast.LENGTH_LONG).show()
                        return@addOnSuccessListener
                    }

                    // ✅ All conditions met — proceed with payment
                    jobRef.child("paidAmount").setValue(amount)
                    jobRef.child("paymentStatus").setValue("success")
                    jobRef.child("jobPaymentOption").setValue(paymentMethod).addOnSuccessListener {
                        Toast.makeText(this, "Payment Successful via ${if (paymentMethod == "cash") "Cash" else "bKash"}", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, PaymentResultActivity::class.java)
                        intent.putExtra("resultStatus", "success")
                        intent.putExtra("jobId", jobId)
                        startActivity(intent)
                        finish()
                    }

                } else {
                    Toast.makeText(this, "Job record not found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show()
            }
        }

        etJobId.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val jobId = etJobId.text.toString().trim()
                if (jobId.isNotEmpty()) {
                    checkPaymentStatus(jobId)
                }
            }
        }

        btnPayCash.setOnClickListener {
            val jobId = etJobId.text.toString().trim()
            val amountText = etPaymentAmount.text.toString().trim()
            val amount = amountText.toIntOrNull()

            if (jobId.isNotEmpty() && amount != null) {
                handlePayment(jobId, amount, "cash")
            } else {
                Toast.makeText(this, "Please enter a valid Job ID and amount", Toast.LENGTH_SHORT).show()
            }
        }

        btnPayBkash.setOnClickListener {
            val jobId = etJobId.text.toString().trim()
            val amountText = etPaymentAmount.text.toString().trim()
            val amount = amountText.toIntOrNull()

            if (jobId.isNotEmpty() && amount != null) {
                handlePayment(jobId, amount, "mobile")
            } else {
                Toast.makeText(this, "Please enter a valid Job ID and amount", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
