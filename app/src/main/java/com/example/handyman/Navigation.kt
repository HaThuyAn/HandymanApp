package com.example.handyman

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
//Customer pages
import com.example.handyman.customer_pages.CustomerHome
import com.example.handyman.customer_pages.CustomerLogin
import com.example.handyman.customer_pages.CustomerSignup
import com.example.handyman.customer_pages.CustomerHomeUnverified

@Composable
fun Navigation(modifier: Modifier = Modifier, startDestination: String = "landingPage") {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination, builder = {
        composable("landingPage") {
            LandingPage(navController = navController)
        }
        composable("chooseAccountType") {
            ChooseAccountType(navController = navController)
        }

//        Customer pages

        composable("customerLogin") {
            CustomerLogin(modifier, navController)
        }
        composable("customerSignup") {
            CustomerSignup(modifier, navController)
        }
        composable("customerHome") {
            CustomerHome(modifier, navController)
        }
        composable("customerHomeUnverified") {
            CustomerHomeUnverified(modifier, navController)
        }

    })
    
}
