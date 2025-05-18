package com.example.mockpaymentapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button

class PaymentResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_result)

        val paymentStatus = intent.getStringExtra("resultStatus")
        val statusTextView = findViewById<TextView>(R.id.tvStatusMessage)
        val statusImageView = findViewById<ImageView>(R.id.statusImageView)

        if (paymentStatus == "success") {
            // Show success icon (green check)
            statusImageView.setImageResource(R.drawable.green_check)
            statusTextView.text = "Payment Successful"
        } else {
            // Show failure icon (red cross)
            statusImageView.setImageResource(R.drawable.red_cross)
            statusTextView.text = "Payment Failed"
        }
        val btnMakeAnotherPayment = findViewById<Button>(R.id.btnMakeAnotherPayment)
        btnMakeAnotherPayment.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

    }

}
