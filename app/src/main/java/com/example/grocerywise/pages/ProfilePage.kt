package com.example.grocerywise.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.grocerywise.ProfileViewModel
import com.example.grocerywise.R
import com.example.grocerywise.AuthViewModel
import com.example.grocerywise.ui.theme.Sage

@Composable
fun ProfilePage(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel(),
    authViewModel: AuthViewModel,
) {
    // 1) Obtain the avatar URL from ViewModel
    val avatarUrl by profileViewModel.avatarUrl.collectAsState()

    // 2) Launcher to pick an image from the gallery
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { profileViewModel.uploadAvatar(it) }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            TextButton(modifier = Modifier.size(100.dp), onClick = {
                navController.popBackStack()
            }) {
                Icon(Icons.Default.Close, contentDescription = "Close Screen")
            }
        }
        Spacer(Modifier.height(200.dp))
        // -- Avatar image (click to change) --
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Profile Avatar",
            placeholder = painterResource(R.drawable.default_avatar),
            error       = painterResource(R.drawable.default_avatar),
            fallback    = painterResource(R.drawable.default_avatar),
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .clickable { picker.launch("image/*") }
        )
        Spacer(Modifier.height(16.dp))

        // -- Button to change avatar --
        Button(onClick = { picker.launch("image/*") }, colors = ButtonDefaults.buttonColors(containerColor = Sage)) {
            Text("Change Avatar")
        }
        Spacer(Modifier.height(24.dp))

        // -- Back button --
        Button(onClick = {
            navController.popBackStack()   // go back to the previous screen
        }, colors = ButtonDefaults.buttonColors(containerColor = Sage)) {
            Text("Back")
        }
        Spacer(Modifier.height(16.dp))

        // -- Log out button --
        Button(onClick = {
            // 1) Sign the user out
            authViewModel.signout()
            // 2) Navigate back to the login screen and clear this Profile from the backstack
            navController.navigate("login") {
                popUpTo("profile") { inclusive = true }
            }
        }, colors = ButtonDefaults.buttonColors(containerColor = Sage)) {
            Text("Sign Out")
        }
    }
}

