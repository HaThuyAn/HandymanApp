package com.example.mockpaymentapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomePageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val btnCustomer = findViewById<Button>(R.id.btnCustomer)
        val btnHandyman = findViewById<Button>(R.id.btnHandyman)

        btnCustomer.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnHandyman.setOnClickListener {
            val intent = Intent(this, HandymanActivity::class.java)
            startActivity(intent)
        }
    }
}
