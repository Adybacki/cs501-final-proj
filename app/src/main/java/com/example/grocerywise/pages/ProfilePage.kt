package com.example.grocerywise.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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

@Composable
fun ProfilePage(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // -- Display current avatar or default, clickable to change --
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Profile Avatar",
            placeholder = painterResource(R.drawable.default_avatar),
            error = painterResource(R.drawable.default_avatar),
            fallback = painterResource(R.drawable.default_avatar),
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .clickable { picker.launch("image/*") }
        )
        Spacer(Modifier.height(16.dp))

        // -- Button to change avatar --
        Button(onClick = { picker.launch("image/*") }) {
            Text("Change Avatar")
        }

        Spacer(Modifier.height(32.dp))

        // -- Log out button --
        Button(onClick = {
            profileViewModel.signOut()
            navController.navigate("login") {
                popUpTo("profile") { inclusive = true }
            }
        }) {
            Text("Sign Out")
        }
    }
}
