package com.example.handyman

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.handyman.customer_pages.CustomerSignup
import com.example.handyman.customer_pages.CustomerLogin
import com.example.handyman.handyman_pages.HandymanSignup
import com.example.handyman.handyman_pages.HandymanLogin



class ChooseAccountTypeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "choose_account_type"
            ) {
                composable("choose_account_type") { ChooseAccountType(navController) }
                composable("customerSignup") { CustomerSignup(Modifier, navController) }
                composable("handymanSignup") { HandymanSignup(Modifier, navController) }
                composable("customerLogin") { CustomerLogin(Modifier, navController) }  // <-- ADD THIS
                composable("handymanLogin") { HandymanLogin(Modifier, navController) }

            }
        }
    }
}

