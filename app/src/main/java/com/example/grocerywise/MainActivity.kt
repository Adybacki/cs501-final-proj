package com.example.grocerywise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.grocerywise.ui.theme.GroceryWiseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel: AuthViewModel by viewModels()
        setContent {
            GroceryWiseTheme {
                AppNavigation(
                    modifier     = Modifier.fillMaxSize(),
                    authViewModel = authViewModel
                )
            }
        }
    }
}
