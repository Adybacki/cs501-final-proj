package com.example.grocerywise.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.grocerywise.AuthState
import com.example.grocerywise.AuthViewModel
import com.example.grocerywise.R
import com.example.grocerywise.ui.theme.Sage

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var hit by remember { mutableStateOf(false) }
    val authState = authViewModel.authState.observeAsState()

    val context = LocalContext.current
    val loginAni by rememberLottieComposition(LottieCompositionSpec.Asset("animations/signin.json"))
    val animatable = rememberLottieAnimatable()
    val rejection by rememberLottieComposition(LottieCompositionSpec.Asset("animations/reject.json"))

    // Add loading/error animations
    LaunchedEffect(authState.value, loginAni) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                if (loginAni != null) {
                    animatable.animate(
                        composition = loginAni,
                        clipSpec = LottieClipSpec.Progress(0.4f, 0.8f),
                        speed = 1.5f,
                        iterations = 1,
                    )
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            is AuthState.Loading -> {
                if (loginAni != null) {
                    animatable.animate(
                        clipSpec = LottieClipSpec.Progress(0f, 0.4f),
                        composition = loginAni,
                        speed = 1.5f,
                        iterations = LottieConstants.IterateForever,
                    )
                }
            }
            is AuthState.Error -> {
                if (rejection != null) {
                    animatable.animate(
                        clipSpec = LottieClipSpec.Progress(0f, 0.8f),
                        composition = rejection,
                        speed = 1.5f,
                        iterations = 1,
                    )
                }
                Toast
                    .makeText(
                        context,
                        (authState.value as AuthState.Error).message,
                        Toast.LENGTH_SHORT,
                    ).show()

                authViewModel.signout()
            } else -> Unit
        }
    }

    when (authState.value) {
        is AuthState.Authenticated -> {
            Column(
                modifier = Modifier.fillMaxSize(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LottieAnimation(
                    composition = loginAni,
                    progress = { animatable.progress },
                )
            }
        }
        is AuthState.Unauthenticated ->
            Column(
                modifier =
                    modifier
                        .fillMaxSize()
                        .padding(vertical = 30.dp, horizontal = 30.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Welcome to GroceryWise",
                    fontSize = 25.sp,
                    fontFamily = FontFamily(Font(resId = R.font.nunitobold)),
                    fontWeight = FontWeight.W600,
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Email field (limit to 40 characters)
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

                // Password field (limit to 40 characters)
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
                    visualTransformation = PasswordVisualTransformation(),
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Login button
                Button(
                    onClick = {
                        hit = true
                        authViewModel.login(email, password)
                    },
                    enabled = authState.value != AuthState.Loading && (password != "" && email != ""),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Sage,
                        ),
                ) {
                    Text("Login")
                }
                Spacer(modifier = Modifier.height(8.dp))

                // --- Forgot password button ---
                TextButton(onClick = {
                    if (email.isBlank()) {
                        Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                    } else {
                        authViewModel.resetPassword(email) { success, error ->
                            if (success) {
                                Toast.makeText(context, "Password reset email sent", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Failed: $error", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }) {
                    Text("Forgot password?", color = Sage)
                }

                Spacer(Modifier.height(8.dp))

                //Sign up button
                TextButton(onClick = {
                    navController.navigate("signup")
                }) {
                    Text("Don't have an account? Sign up here", color = Sage)
                }
            }

        is AuthState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LottieAnimation(
                    composition = rejection,
                    progress = { animatable.progress },
                )
            }
        }
        else -> {
            Column(
                modifier = Modifier.fillMaxSize(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LottieAnimation(
                    composition = loginAni,
                    progress = { animatable.progress },
                )
            }
        }
    }
}
