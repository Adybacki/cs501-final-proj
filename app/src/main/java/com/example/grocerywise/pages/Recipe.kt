package com.example.grocerywise.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.grocerywise.R

@Composable
fun Recipe() {
    val info = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    var searchVal by remember { mutableStateOf("") }

    when (info) {
        WindowWidthSizeClass.COMPACT -> {
            Column(
                modifier = Modifier.fillMaxSize(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Recipe Finder", fontFamily = FontFamily(Font(R.font.defaultfont)), fontSize = 30.sp, color = Color(0xFF101210))
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(1f).height(60.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
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
                    Button(onClick = { }, modifier = Modifier.fillMaxWidth().fillMaxHeight()) { Text("Search", softWrap = true) }
                }
            }
        }
    }
}
