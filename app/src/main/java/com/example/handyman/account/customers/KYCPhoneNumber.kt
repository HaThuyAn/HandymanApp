package com.example.handyman.account.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.handyman.R
import com.example.handyman.account.components.DividerLine
import com.example.handyman.account.components.StepCircle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KYCPhoneNumber(navController: NavController) {
    var phoneNumber by remember { mutableStateOf("") }
    val textFieldModifier = Modifier
        .fillMaxWidth()
        .height(56.dp)

    val isValidPhone = phoneNumber.matches(Regex("^\\+880\\s?1[3-9][0-9]{2}-?[0-9]{6}$"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        // Top bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "Back",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Account verification", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Step indicator
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            StepCircle(stepNumber = 1, isActive = true)
            DividerLine()
            StepCircle(stepNumber = 2, isActive = true)
            DividerLine()
            StepCircle(stepNumber = 3, isActive = true)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Header
        Text("Verify your phone number", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Get a one-time-passcode (OTP) to verify your mobile number",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text("Mobile", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholder = { Text("+880 0000-000000") },
            modifier = textFieldModifier,
            isError = phoneNumber.isNotBlank() && !isValidPhone
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                navController.navigate("kycCodeOTP")
            },
            enabled = isValidPhone,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isValidPhone) Color(0xFFFFB703) else Color(0xFFB0B0B0)
            )
        ) {
            Text("Get OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        }
    }
}