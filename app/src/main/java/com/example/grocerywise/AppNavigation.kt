package com.example.grocerywise

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.grocerywise.BuildConfig
import com.example.grocerywise.pages.AddItemScreen
import com.example.grocerywise.pages.HomePage
import com.example.grocerywise.pages.LoginPage
import com.example.grocerywise.pages.Recipe
import com.example.grocerywise.pages.SignupPage
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
) {
    val dburl = BuildConfig.DB
    val database = Firebase.database
    val ref = database.getReferenceFromUrl(dburl)
    ref.get().addOnSuccessListener { /* … */ }

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier,
    ) {
        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable("signup") {
            SignupPage(modifier, navController, authViewModel)
        }
        composable("home") {
            HomePage(modifier, navController, authViewModel)
        }
        composable("recipe") {
            Recipe()
        }

        // 匹配 HomePage 和 getProductDetails 中的跳转
        composable(
            route = "add_item?productName={productName}&productUpc={productUpc}&productPrice={productPrice}&productImageUri={productImageUri}",
            arguments =
                listOf(
                    navArgument("productName") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("productUpc") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("productPrice") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("productImageUri") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                ),
        ) { backStack ->
            val args = backStack.arguments!!
            AddItemScreen(
                navController = navController,
                productName = args.getString("productName"),
                productUpc = args.getString("productUpc"),
                productPrice = args.getString("productPrice"),
                productImageUri = args.getString("productImageUri"),
            )
        }
    }
}
