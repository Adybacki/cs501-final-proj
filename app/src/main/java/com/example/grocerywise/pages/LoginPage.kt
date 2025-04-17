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
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.window.core.layout.WindowWidthSizeClass
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
    val info = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }
   //when (info) {
       // WindowWidthSizeClass.COMPACT -> {
            Column(
                modifier =
                    modifier
                        .fillMaxSize()
                        .padding(vertical = 30.dp, horizontal = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Welcome to Grocery Wise",
                    fontSize = 30.sp,
                    fontFamily = FontFamily(Font(resId = R.font.defaultfont)),
                    fontWeight = FontWeight.W600,
                )

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text("Email", fontFamily = FontFamily(Font(resId = R.font.defaultfont)))
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
                    onValueChange = { password = it },
                    label = {
                        Text("Password", fontFamily = FontFamily(Font(resId = R.font.defaultfont)))
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
                    authViewModel.login(email, password)
                }, enabled = authState.value != AuthState.Loading && (password != "" && email != "")) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(5.dp))

                TextButton(onClick = {
                    navController.navigate("signup")
                }) { Text("Don't have an account? Sign up here") }
            }
        //}

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
    //}
}
