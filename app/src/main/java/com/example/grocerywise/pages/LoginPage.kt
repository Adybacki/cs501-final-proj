package com.example.grocerywise.pages
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
//    val info = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    var hit by remember { mutableStateOf(false) }
    val authState = authViewModel.authState.observeAsState()

    val context = LocalContext.current
    val loginAni by rememberLottieComposition(LottieCompositionSpec.Asset("animations/signin.json"))
    val Animatable = rememberLottieAnimatable()
    val rejection by rememberLottieComposition(LottieCompositionSpec.Asset("animations/reject.json"))
    var rejected by remember { mutableStateOf(false) }
    LaunchedEffect(authState.value, loginAni) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                if (loginAni != null) {
                    Animatable.animate(
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
                    Animatable.animate(
                        clipSpec = LottieClipSpec.Progress(0f, 0.4f),
                        composition = loginAni,
                        speed = 1.5f,
                        iterations = LottieConstants.IterateForever,
                    )
                }
            }
            is AuthState.Error -> {
                if (rejection != null) {
                    Animatable.animate(
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
    // when (info) {
    // WindowWidthSizeClass.COMPACT -> {
    when (authState.value) {
        is AuthState.Authenticated -> {
            Column(
                modifier = Modifier.fillMaxSize(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LottieAnimation(
                    composition = loginAni,
                    progress = { Animatable.progress },
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

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it.take(40) },
                    label = {
                        Text("Email", fontFamily = FontFamily(Font(resId = R.font.nunito)))
                    },
                    maxLines = 1,
                    colors =
                        TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFF44B863),
                            focusedLabelColor = Color(0xFF615fd4),
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
                        TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFF44B863),
                            focusedLabelColor = Color(0xFF615fd4),
                        ),
                    visualTransformation = PasswordVisualTransformation(),
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = {
                    hit = true
                    authViewModel.login(email, password)
                }, enabled = authState.value != AuthState.Loading && (password != "" && email != "")) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(5.dp))

                TextButton(onClick = {
                    navController.navigate("signup")
                }) { Text("Don't have an account? Sign up here") }
            }

        is AuthState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LottieAnimation(
                    composition = rejection,
                    progress = { Animatable.progress },
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
                    progress = { Animatable.progress },
                )
            }
        }
    }

    // }

//        WindowWidthSizeClass.EXPANDED -> {
//            Column(
//                modifier =
//                    Modifier
//                        .fillMaxSize()
//                        .padding(vertical = 10.dp, horizontal = 20.dp),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                Text(
//                    "Welcome to Grocery Wise",
//                    fontSize = 30.sp,
//                    fontFamily = FontFamily(Font(resId = R.font.defaultfont)),
//                    fontWeight = FontWeight.W600,
//                )
//                Spacer(modifier = Modifier.height(20.dp))
//                OutlinedTextField(
//                    value = email,
//                    onValueChange = { email = it },
//                    label = {
//                        Text("Email", fontFamily = FontFamily(Font(resId = R.font.defaultfont)))
//                    },
//                    maxLines = 1,
//                    colors =
//                        TextFieldDefaults.colors(
//                            focusedIndicatorColor = Color(0xFF44B863),
//                            focusedLabelColor = Color(0xFF615fd4),
//                        ),
//                )
//                Spacer(modifier = Modifier.height(10.dp))
//                OutlinedTextField(
//                    value = password,
//                    onValueChange = { password = it },
//                    label = {
//                        Text("Password", fontFamily = FontFamily(Font(resId = R.font.defaultfont)))
//                    },
//                    maxLines = 1,
//                    colors =
//                        TextFieldDefaults.colors(
//                            focusedIndicatorColor = Color(0xFF44B863),
//                            focusedLabelColor = Color(0xFF615fd4),
//                        ),
//                    visualTransformation = PasswordVisualTransformation(),
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Button(onClick = {
//                    authViewModel.login(email, password)
//                }, enabled = (email != "" && password != "")) {
//                    Text("Login")
//                }
//                Spacer(modifier = Modifier.height(5.dp))
//                TextButton(onClick = {
//                    navController.navigate("signup")
//                }) { Text("Don't have an account? Sign up here") }
//            }
//        }
    // }
}
