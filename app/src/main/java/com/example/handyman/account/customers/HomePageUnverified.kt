package com.example.handyman.account.customers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.handyman.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KYCLandingUnverified(modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Section
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.dp), // Replace with your imported DP image
                contentDescription = "Profile",
                modifier = Modifier
                    .size(60.dp)
                    .padding(end = 8.dp)
            )
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Hello, Minnie J.", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Unverified",
                        color = Color(0xFFE8A317),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .background(Color(0x1AFEC260), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.bytesize_location),
                        contentDescription = "Location",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Unverified address", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search Box
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("What services are you looking for?") },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = "Search"
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Welcome Message
        Text("Welcome, Minnie!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Verify your account and unlock access to our trusted Handymen services.",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Illustration
        Image(
            painter = painterResource(id = R.drawable.group_405), // Handyman illustration
            contentDescription = "Illustration",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Verification Button
        Button(
            onClick = {
                navController.navigate("kycLanding")
//                navController.navigate("kycPhoneNumber")
            }
            ,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB703)),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text("Start verification", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom NavBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.home_icon),
                contentDescription = "Home",
                tint = Color(0xFF8A4DFF),
                modifier = Modifier.size(30.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.list_icon),
                contentDescription = "List",
                tint = Color.Gray,
                modifier = Modifier.size(30.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.chat_icon),
                contentDescription = "Chat",
                tint = Color.Gray,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}
