package com.example.handyman

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class SupportFormFragment : Fragment() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Customer Support Form",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 24.sp
                                        ),
                                        color = Color.Black
                                    )
                                },
                                modifier = Modifier.background(Color(0xFFF6F6FF))
                            )
                        }
                    ) { paddingValues ->
                        SupportForm(paddingValues)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportForm(paddingValues: PaddingValues) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(TextFieldValue()) }
    var email by remember { mutableStateOf(TextFieldValue()) }
    var subject by remember { mutableStateOf(TextFieldValue()) }
    var message by remember { mutableStateOf(TextFieldValue()) }

    val categories = listOf(
        "Technical Issue",
        "Billing & Payments",
        "Account Management",
        "Feature Request",
        "General Inquiry"
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Name", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Alex Johnson") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Email", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("alex@example.com") },
            modifier = Modifier.fillMaxWidth(),
            isError = email.text.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email.text).matches()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Subject", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            placeholder = { Text("Issue with login") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Category", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select a category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Message", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("I can't access my account...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when {
                    name.text.isBlank() || email.text.isBlank() || subject.text.isBlank() || message.text.isBlank() || selectedCategory.isBlank() -> {
                        Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                    }
                    !Patterns.EMAIL_ADDRESS.matcher(email.text).matches() -> {
                        Toast.makeText(context, "Please enter a valid email.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val database = FirebaseDatabase.getInstance("https://customerhandyman.firebaseio.com")
                        val supportRequestsRef = database.getReference("support_requests")
                        val newRequestRef = supportRequestsRef.push()

                        val currentTimestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())

                        val supportRequest = SupportRequest(
                            name = name.text,
                            email = email.text,
                            subject = subject.text,
                            message = message.text,
                            category = selectedCategory,
                            createdAt = currentTimestamp,
                            status = "Open"
                        )

                        newRequestRef.setValue(supportRequest)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Support request sent!", Toast.LENGTH_LONG).show()
                                    name = TextFieldValue()
                                    email = TextFieldValue()
                                    subject = TextFieldValue()
                                    message = TextFieldValue()
                                    selectedCategory = ""
                                } else {
                                    Toast.makeText(context, "Failed to send support request.", Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFB0))
        ) {
            Text(
                text = "Submit",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 20.sp
                )
            )
        }
    }
}

data class SupportRequest(
    val name: String = "",
    val email: String = "",
    val subject: String = "",
    val message: String = "",
    val category: String = "",
    val status: String = "Open",
    val createdAt: String = ""
)
@Preview(showBackground = true)
@Composable
fun SupportFormPreview() {
    MaterialTheme {
        SupportForm(paddingValues = PaddingValues(0.dp)) // Preview with no padding
    }
}