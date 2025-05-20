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
import com.example.handyman.chatbox.ui.composables.LoginButton
import com.example.handyman.chatbox.ui.composables.LoginInput
import com.example.handyman.ui.theme.HandymanTheme
import com.google.firebase.auth.FirebaseAuth

//import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

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

//                        Image(
//                            painter = painterResource(id = R.drawable.ic_overflow_dot_menu),
//                            contentDescription = "Overflow menu icon",
//                            modifier = Modifier
//                                .align(alignment = Alignment.TopStart)
//                                .offset(
//                                    x = 375.dp,
//                                    y = 56.dp
//                                )
//                                .requiredSize(size = 32.dp)
//                        )
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
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@MainActivity, ChatListingActivity::class.java)
                    intent.putExtra("userId", auth.currentUser?.email)
//                    getFCMToken()
                    startActivity(intent)
                } else {
                    Log.e("FirebaseAuth", "Sign-in failed", task.exception)
                    Toast.makeText(this@MainActivity, "Cannot login as $email", Toast.LENGTH_LONG).show()
                }
            }
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