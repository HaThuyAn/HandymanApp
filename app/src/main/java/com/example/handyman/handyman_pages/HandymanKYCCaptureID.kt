package com.example.handyman.handyman_pages

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.handyman.R
import com.example.handyman.components.DividerLine
import com.example.handyman.components.StepCircle
import com.example.handyman.utils.SessionManager
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

@Composable
fun HandymanKYCCaptureID(modifier: Modifier = Modifier, navController: NavController) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showCamera by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    val showActionIcons = selectedImageUri == null

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        showCamera = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Top Bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.navigate("handymanKycLanding") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("ID Card Photo", fontSize = 20.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Step Indicator
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            StepCircle(stepNumber = 1, isActive = true)
            DividerLine()
            StepCircle(stepNumber = 2, isActive = false)
            DividerLine()
            StepCircle(stepNumber = 3, isActive = false)
            DividerLine()
            StepCircle(stepNumber = 4, isActive = false)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Header
        Text("Photo ID Card", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Please place your national ID card inside the frame.",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Preview Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(Color(0xFFE8E8E8), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Selected ID",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.id_card_icon_handyman),
                    contentDescription = "Default ID",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        if (showActionIcons) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.image_icon),
                    contentDescription = "Gallery",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            galleryLauncher.launch("image/*")
                        }
                )

                Icon(
                    painter = painterResource(id = R.drawable.camera_shutter_button),
                    contentDescription = "Capture",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(72.dp)
                )

                Icon(
                    painter = painterResource(id = R.drawable.bin_icon),
                    contentDescription = "Upload Placeholder",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(42.dp)
                        .alpha(0f)
                )
            }
        } else {
            Spacer(modifier = Modifier.height(48.dp))
            Column {
                Button(
                    onClick = {
                        if (selectedImageUri != null) {
                            val currentEmail = SessionManager.getLoggedInEmail(context)
                            val handymanRef = FirebaseDatabase.getInstance().getReference("Handyman")
                            val query = handymanRef.orderByChild("email").equalTo(currentEmail)

                            val storageRef = FirebaseStorage.getInstance().reference
                            val fileName = "photo_id_cards/${System.currentTimeMillis()}_${selectedImageUri!!.lastPathSegment}"
                            val photoRef = storageRef.child(fileName)

                            photoRef.putFile(selectedImageUri!!)
                                .addOnSuccessListener {
                                    photoRef.downloadUrl.addOnSuccessListener { uri ->
                                        val downloadUrl = uri.toString()

                                        query.get().addOnSuccessListener { snapshot ->
                                            for (child in snapshot.children) {
                                                child.ref.child("photoIdCard").setValue(downloadUrl)
                                                    .addOnSuccessListener {
                                                        navController.navigate("handymanKYCCertificates")
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.e("KYC", "Failed to save photo URL: ${e.message}")
                                                    }
                                            }

                                            if (!snapshot.exists()) {
                                                Log.e("KYC", "No handyman found with email: $currentEmail")
                                            }
                                        }.addOnFailureListener { e ->
                                            Log.e("KYC", "Query failed: ${e.message}")
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("KYC", "Upload failed: ${e.message}")
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F3367))
                ) {
                    Text("Submit ID Card", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { selectedImageUri = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Try again", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                }
            }
        }
    }
}
