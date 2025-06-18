package com.example.handyman.chatbox

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.handyman.chatbox.ui.composables.ChatClientScreen
import com.example.handyman.ui.theme.HandymanTheme
import com.example.handyman.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ChatClientActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HandymanTheme {
                val chatID = intent.getStringExtra("chatID")
                val receiverUID = intent.getStringExtra("uid")
                val receiverName = intent.getStringExtra("username")

                val handleSendButtonClick: (String) -> Unit = {
                    if (chatID != null && receiverUID != null) {
                        sendMessage(chatID, receiverUID, content = it)
                    }
                }

                if (receiverName != null) {
                    ChatClientScreen(
                        chatID = chatID,
                        receiverName = receiverName,
                        onSendButtonClick = handleSendButtonClick
                    )
                }
            }
        }
    }
}

internal fun sendMessage(chatID: String, receiverID: String, content: String = "") {
    val message = hashMapOf(
//            "senderId" to auth.currentUser?.uid,  // Use this line when integrated with Firebase Auth
        "senderId" to SessionManager.currentUserID,
        "receiverId" to receiverID,
        "timestamp" to FieldValue.serverTimestamp(),  // Use Firebase server time for more consistency
        "content" to content
    )

    val database = FirebaseFirestore.getInstance()
    database.collection("chats")
        .document(chatID)
        .collection("messages")
        .add(message)
        .addOnSuccessListener { docRef ->
            Log.d("Message", "Successfully sent message with ID: ${docRef.id}")
        }
        .addOnFailureListener { e ->
            Log.e("Message", "Error sending message: ${e}")
        }
}
