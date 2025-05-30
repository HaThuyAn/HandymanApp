package com.example.handyman.chatbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.handyman.MainJobBoard
import com.example.handyman.chatbox.ui.composables.LoginButton
import com.example.handyman.chatbox.ui.composables.LoginInput
import com.example.handyman.ui.theme.HandymanTheme
import com.example.handyman.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HandymanTheme {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                val handleLoginSubmission: () -> Unit = { Login(email, password) }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .requiredHeight(height = 190.dp)
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .align(alignment = Alignment.TopStart)
                                .offset(
                                    x = 32.dp,
                                    y = 56.dp,
                                )
                        ) {
//                            BackButton(size = 35.dp, onClick = {})
                        }

                        Text(
                            text = "User Log In",
                            color = Color(0xff4d4d4d),
                            textAlign = TextAlign.Center,
                            lineHeight = 1.12.em,
                            style = TextStyle(
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier
                                .align(alignment = Alignment.TopCenter)
                                .offset(
                                    y = 60.dp
                                )
                        )
                    }

                    Column(
                        modifier = Modifier.align(alignment = Alignment.Center).requiredWidth(320.dp)
                    ) {
                        LoginInput(
                            email = email,
                            onEmailChange = { text -> email = text },
                            password = password,
                            onPasswordChange = { text -> password = text },
                        )

                        Spacer(modifier = Modifier.height(25.dp))

                        LoginButton(
                            onClick = handleLoginSubmission,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    private fun Login(email: String, password: String) {
        // Login function using credentials stored in Realtime Database
        val database = FirebaseDatabase.getInstance()

        var isLogined = false

        val userRef = database.getReference("User")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    if (email.trim() == userSnapshot.child("email").value && password.trim() == userSnapshot.child("password").value) {
                        isLogined = true
                        SessionManager.currentUserID = userSnapshot.key
                        SessionManager.currentUserEmail = email.trim()
                        SessionManager.currentUserName = userSnapshot.child("firstName").value as String?
                        SessionManager.currentUserType = "customer"
                        val intent = Intent(this@MainActivity, MainJobBoard::class.java)
                        startActivity(intent)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DatabaseError", "$error")
                Toast.makeText(
                    this@MainActivity,
                    "An error occurred while login with ${email.trim()}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        if (isLogined) return

        val handymanRef = database.getReference("Handyman")
        handymanRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (handymanSnapshot in snapshot.children) {
                    if (email.trim() == handymanSnapshot.child("email").value && password.trim() == handymanSnapshot.child(
                            "password"
                        ).value
                    ) {
                        isLogined = true
                        SessionManager.currentUserID = handymanSnapshot.key
                        SessionManager.currentUserEmail = email.trim()
                        SessionManager.currentUserName = handymanSnapshot.child("firstName").value as String?
                        SessionManager.currentUserType = "handyman"
                        val intent = Intent(this@MainActivity, MainJobBoard::class.java)
                        startActivity(intent)
                    }
                }
                if (!isLogined) {
                    Toast.makeText(this@MainActivity, "Cannot find provided login information", Toast.LENGTH_LONG)
                        .show()
                    Toast.makeText(this@MainActivity, "Please recheck your email and password", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DatabaseError", "$error")
                Toast.makeText(
                    this@MainActivity,
                    "An error occurred while login with ${email.trim()}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })


// Login function using Firebase Authentication (completed except distinguishing between handyman and customer)
//        val auth = FirebaseAuth.getInstance()
//        auth.signInWithEmailAndPassword(email.trim(), password.trim())
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
////                    val intent = Intent(this@MainActivity, ChatListingActivity::class.java)
//                    val intent = Intent(this@MainActivity, MainJobBoard::class.java).apply {
//                        putExtra("user_type", "handyman")
//                    }
////                    getFCMToken()
//                    startActivity(intent)
//                } else {
//                    Log.e("FirebaseAuth", "Sign-in failed", task.exception)
//                    Toast.makeText(this@MainActivity, "Failed to login as $email", Toast.LENGTH_LONG).show()
//                }
//            }
    }

//    private fun getFCMToken() {
//        FirebaseMessaging.getInstance().token.addOnSuccessListener(this::pushFCMToken)
//    }

//    private fun pushFCMToken(token: String) {
//        val auth: FirebaseAuth = FirebaseAuth.getInstance()
//        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
//        val docRef: DocumentReference? = auth.currentUser?.let { database.collection("users").document(it.uid) }
//        docRef?.update("fcmToken", token)
//    }
}