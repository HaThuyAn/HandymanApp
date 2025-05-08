package com.example.handyman

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.handyman.pages.HomePage
import com.example.handyman.ui.theme.AuthViewModel
import com.example.handyman.pages.LoginPage
import com.example.handyman.pages.SignupPage
import com.example.handyman.pages.KYCLandingUnverified
import com.example.handyman.pages.KYCLanding
import com.example.handyman.pages.KYCCaptureID
import com.example.handyman.pages.KYCAddressForm
import com.example.handyman.pages.KYCCodeOTP
import com.example.handyman.pages.KYCPhoneNumber
import com.example.handyman.pages.KYCSuccess

@Composable
fun Navigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable("signup") {
            SignupPage(modifier, navController, authViewModel)
        }
        composable("home") {
            HomePage(modifier, navController, authViewModel)
        }
        composable("homeUnverified") {
            KYCLandingUnverified(modifier, navController)
        }
        composable("kycLanding") {
            KYCLanding(navController = navController)
        }
        composable("kycCaptureID") {
            KYCCaptureID(navController = navController)
        }
        composable("kycAddressForm") {
            KYCAddressForm(navController = navController)
        }
        composable("kycPhoneNumber") {
            KYCPhoneNumber(navController = navController)
        }
        composable ( "kycCodeOTP" ){
            KYCCodeOTP(navController = navController)
        }
        composable("kycSuccess") {
            KYCSuccess(navController = navController)
        }
    })
    
}