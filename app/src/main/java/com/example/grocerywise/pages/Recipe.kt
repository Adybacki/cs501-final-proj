package com.example.grocerywise.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.grocerywise.AuthViewModel
import com.example.grocerywise.R
import com.example.grocerywise.data.FirebaseDatabaseManager
import com.example.grocerywise.models.Ingredients
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Recipe(
    navController: NavController,
    authViewModel: AuthViewModel,
) {
    val info = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    var searchVal by remember { mutableStateOf("") }
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val categorization: MutableList<String> = remember { mutableStateListOf() }

    LaunchedEffect(userId) {
        if (userId != null) {
            val listener =
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        categorization.clear()
                        snapshot.children.forEach { each ->
                            val igrdnts = each.getValue(Ingredients::class.java)
                            if (igrdnts != null) {
                                ingredients.add(igrdnts)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("InventoryScreen", "Data listener error: ${error.message}")
                    }
                }
            FirebaseDatabaseManager.listenIngredients(userId, listener)
        }
        Log.i("ingredients", ingredients.joinToString())
    }

    when (info) {
        WindowWidthSizeClass.COMPACT -> {
            Column(
                modifier = Modifier.fillMaxSize(1f).padding(vertical = 20.dp, horizontal = 5.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Recipe Finder", fontFamily = FontFamily(Font(R.font.defaultfont)), fontSize = 30.sp, color = Color(0xFF101210))
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(1f).height(60.dp),
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(R.drawable.search),
                                contentDescription = "search",
                            )
                        },
                        textStyle =
                            TextStyle(
                                color = Color.DarkGray,
                                fontSize = 18.sp,
                                fontFamily =
                                    FontFamily(
                                        Font(R.font.defaultfont),
                                    ),
                            ),
                        modifier =
                            Modifier
                                .fillMaxWidth(
                                    0.8f,
                                ).height(50.dp),
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF44B863),
                                focusedLabelColor = Color(0xFF615fd4),
                            ),
                        shape = RoundedCornerShape(50),
                        value = searchVal,
                        onValueChange = { value: String ->
                            searchVal =
                                value
                        },
                        placeholder = {
                            Text(
                                "search your recipe",
                                fontSize = 18.sp,
                                fontFamily =
                                    FontFamily(
                                        Font(R.font.defaultfont),
                                    ),
                                color = Color.LightGray,
                            )
                        },
                        singleLine = true,
                    )
                    Button(
                        onClick = {
                        },
                        modifier =
                            Modifier.fillMaxWidth().height(
                                40.dp,
                            ),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF022e2d)),
                    ) {
                        Text(
                            "Search",
                            softWrap = true,
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.defaultfont)),
                            color = Color.LightGray,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                ) {
                }
            }
        }
    }
}
