package com.example.handyman.customer_pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase
import com.example.handyman.R
import java.util.*
import java.text.SimpleDateFormat
import com.example.handyman.utils.getCurrentYearMonth
import com.example.handyman.utils.incrementMetric

@Composable
fun CustomerSignup(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val isValid = firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
            password.length >= 8 &&
            password == confirmPassword

    Log.d("Navigation","CustomerSingup launched")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.arrow_back),
            contentDescription = "Back",
            modifier = Modifier
                .align(Alignment.Start)
                .clickable { navController.popBackStack() }
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text("Create account", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.character_customer),
            contentDescription = "Customer Graphic",
            modifier = Modifier.size(160.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First name") },
            singleLine = true
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last name") },
            singleLine = true
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Icon(
                    painter = painterResource(id = if (passwordVisible) R.drawable.lets_icons_eye_duotone else R.drawable.heroicons_solid_eye_off),
                    contentDescription = null,
                    modifier = Modifier.clickable { passwordVisible = !passwordVisible }.size(20.dp)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            singleLine = true
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm password") },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Icon(
                    painter = painterResource(id = if (confirmPasswordVisible) R.drawable.lets_icons_eye_duotone else R.drawable.heroicons_solid_eye_off),
                    contentDescription = null,
                    modifier = Modifier.clickable { confirmPasswordVisible = !confirmPasswordVisible }.size(20.dp)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val userId = UUID.randomUUID().toString()
                val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date())
                val userRef = FirebaseDatabase.getInstance().getReference("User").child(userId)

                val userData = mapOf(
                    "userId" to userId,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "email" to email,
                    "password" to password,
                    "isPhoneVerified" to false,
                    "verified" to false,
                    "createdAt" to timestamp,
                    "updatedAt" to timestamp,
                    "isPhoneVerified" to false,
                    "verified" to false,
                    "photoIdCard" to "",
                    "houseNumber" to "",
                    "street" to "",
                    "area" to "",
                    "division" to "",
                    "district" to "",
                    "thana" to "",
                    "city" to "",
                    "country" to "",
                    "postcode" to "",
                    "notes" to "",
                    "kycStatus" to "pending",
                    "approvedBy" to "",
                    "createdAt" to timestamp,
                    "updatedAt" to timestamp
                )

                Log.d("Signup", "Attempting to create user: $userData")
                userRef.setValue(userData)
                    .addOnSuccessListener {
                        val (year, month) = getCurrentYearMonth()
                        incrementMetric("serviceAnalytics/2025/$year/$month/newCustomers")
                        incrementMetric("serviceAnalytics/2025/$year/$month/newUsers")

                        Log.d("Signup", "Successfully created user.")
                        Toast.makeText(context, "Account created successfully", Toast.LENGTH_LONG).show()
                        navController.navigate("customerLogin")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Signup", "Error creating account", e)
                        Toast.makeText(context, "Error creating account", Toast.LENGTH_LONG).show()
                    }
            },
            enabled = isValid,
            colors = ButtonDefaults.buttonColors(containerColor = if (isValid) Color(0xFFFFB703) else Color.LightGray),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text("Sign Up", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Already have an account?", fontSize = 14.sp)
        Text(
            text = "Log in",
            color = Color(0xFF7D56F3),
            modifier = Modifier.clickable { navController.navigate("customerLogin") }
        )
    }
}
