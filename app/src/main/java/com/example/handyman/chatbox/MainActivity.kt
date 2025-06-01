package com.example.handyman.chatbox

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.firebase.database.*
import com.example.handyman.Navigation
import com.example.handyman.ui.theme.HandymanTheme
import com.example.handyman.utils.SessionManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionEmail = SessionManager.getLoggedInEmail(this)
        Log.d("Session", "Restoring session for: $sessionEmail")

        setContent {
            HandymanTheme {
                var startDestination by remember { mutableStateOf<String?>("landingPage") }

                LaunchedEffect(sessionEmail) {
                    if (sessionEmail.isNotBlank()) {
                        val ref = FirebaseDatabase.getInstance().getReference("User")
                        val query = ref.orderByChild("email").equalTo(sessionEmail)
                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                Log.d("Firebase", "Snapshot exists? ${snapshot.exists()}")

                                for (child in snapshot.children) {
                                    val emailFromDB = child.child("email").getValue(String::class.java)
                                    val verifiedRaw = child.child("verified").value
                                    val verified = child.child("verified").getValue(Boolean::class.java) == true

                                    Log.d("Firebase", "User email: $emailFromDB")
                                    Log.d("Firebase", "Raw verified value: $verifiedRaw")
                                    Log.d("Firebase", "Parsed verified == true? $verified")

                                    startDestination = if (verified) {
                                        "customerHome"
                                    } else {
                                        "customerHomeUnverified"
                                    }
                                }
                                if (!snapshot.exists()) {
                                    startDestination = "landingPage" // fallback
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Firebase", "Error: ${error.message}")
                                startDestination = "landingPage"
                            }
                        })
                    } else {
                        startDestination = "landingPage"
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    startDestination?.let {
                        Navigation(
                            modifier = Modifier.padding(innerPadding),
                            startDestination = it
                        )
                    }
                }
            }
        }
    }
}
