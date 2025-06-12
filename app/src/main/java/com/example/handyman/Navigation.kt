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
import com.example.handyman.customer_pages.CustomerKYCCodeOTP
import com.example.handyman.customer_pages.CustomerKYCSubmitted
import com.example.handyman.customer_pages.CustomerHomeKYCProcessing
import com.example.handyman.customer_pages.CustomerKYCLanding
import com.example.handyman.customer_pages.CustomerKYCCaptureID
import com.example.handyman.customer_pages.CustomerKYCAddressForm
import com.example.handyman.customer_pages.CustomerKYCPhoneNumber

//Handyman pages
import com.example.handyman.handyman_pages.HandymanSignup
import com.example.handyman.handyman_pages.HandymanLogin
import com.example.handyman.handyman_pages.HandymanHomeUnverified
import com.example.handyman.handyman_pages.HandymanKYCLanding
import com.example.handyman.handyman_pages.HandymanKYCCaptureID
import com.example.handyman.handyman_pages.HandymanKYCAddressForm
import com.example.handyman.handyman_pages.HandymanKYCPhoneNumber
import com.example.handyman.handyman_pages.HandymanKYCCodeOTP
import com.example.handyman.handyman_pages.HandymanKYCSubmitted
import com.example.handyman.handyman_pages.HandymanKYCProcessing
import com.example.handyman.handyman_pages.HandymanKYCCertificates


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

//        Handyman pages

        composable("handymanSignup") {
            HandymanSignup(modifier, navController)
        }
        composable("handymanLogin") {
            HandymanLogin(modifier, navController)
        }
        composable("handymanHomeUnverified") {
            HandymanHomeUnverified(modifier, navController)
        }
        composable("handymanKYCLanding") {
            HandymanKYCLanding(modifier, navController)
        }
        composable("handymanKYCCaptureID") {
            HandymanKYCCaptureID(modifier, navController)
        }
        composable("handymanKYCAddressForm") {
            HandymanKYCAddressForm(modifier, navController)
        }
        composable("handymanKycPhoneNumber") {
            HandymanKYCPhoneNumber(modifier, navController)
        }
        composable("handymanKycCodeOTP") {
            HandymanKYCCodeOTP(modifier, navController)
        }
        composable("handymanKycSubmitted") {
            HandymanKYCSubmitted(modifier, navController)
        }
        composable("handymanHomeKYCProcessing") {
            HandymanKYCProcessing(modifier, navController)
        }
        composable("handymanKYCCertificates") {
            HandymanKYCCertificates(modifier, navController)
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
        composable("customerKycLanding") {
            CustomerKYCLanding(navController = navController)
        }
        composable("customerKycCaptureID") {
            CustomerKYCCaptureID(navController = navController)
        }
        composable("customerKycAddressForm") {
            CustomerKYCAddressForm(navController = navController)
        }
        composable("customerKycPhoneNumber") {
            CustomerKYCPhoneNumber(navController = navController)
        }
        composable ("customerKycCodeOTP" ){
            CustomerKYCCodeOTP(navController = navController)
        }
        composable("customerKycSubmitted") {
            CustomerKYCSubmitted(navController = navController)
        }
        composable ("customerHomeKYCProcessing"){
            CustomerHomeKYCProcessing(navController = navController)
        }

    })
    
}
