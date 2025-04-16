package com.example.grocerywise

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.grocerywise.BuildConfig
import com.example.grocerywise.pages.HomePage
import com.example.grocerywise.pages.LoginPage
import com.example.grocerywise.pages.SignupPage
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
) {
    val dburl = BuildConfig.DB
//    Log.i("DBInfo.", dburl.toString())
    val database = Firebase.database
    val ref = database.getReferenceFromUrl(dburl)
    ref.get().addOnSuccessListener { snst ->
        val data = snst.value
//        Log.i("db data:", data.toString())
    }

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
    })
}
