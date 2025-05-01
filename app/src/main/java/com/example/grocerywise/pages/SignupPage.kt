package com.example.grocerywise.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.grocerywise.AuthState
import com.example.grocerywise.AuthViewModel
import com.example.grocerywise.R
import com.example.grocerywise.ui.theme.Sage

@Composable
fun SignupPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value){
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sign up for GroceryWise", fontSize = 25.sp, fontFamily = FontFamily(Font(resId = R.font.nunitobold)),)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it.take(40) },
            label = {
                Text("Email", fontFamily = FontFamily(Font(resId = R.font.nunito)))
            },
            maxLines = 1,
            colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Sage,
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it.take(40) },
            label = {
                Text("Password", fontFamily = FontFamily(Font(resId = R.font.nunito)))
            },
            maxLines = 1,
            colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Sage,
            ),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button( onClick = {
            authViewModel.signup(email, password)
        },  enabled = authState.value != AuthState.Loading && (password != "" && email != ""), colors = ButtonDefaults.buttonColors(containerColor = Sage)) {
            Text("Create account")
        }

        Spacer(modifier = Modifier.height(5.dp))

        TextButton(onClick = {
            navController.navigate("login")
        }) { Text("Already have an account? Login here", color = Sage) }
    }
}