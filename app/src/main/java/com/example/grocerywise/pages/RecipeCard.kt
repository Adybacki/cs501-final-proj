package com.example.grocerywise.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.grocerywise.R
import com.example.grocerywise.RecipeResponse

@Composable
fun RecipeCard(Info: RecipeResponse) {
    ElevatedCard(
        modifier = Modifier.heightIn(min = 200.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE9EDCA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AsyncImage(
                model = Info.image,
                contentDescription = Info.title,
                modifier = Modifier.width(120.dp).clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = Info.title,
                fontSize = 20.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.W600,
                softWrap = true,
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))

            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Ingredients: ",
                    color = Color.DarkGray,
                    fontWeight = FontWeight.W400,
                    fontFamily = FontFamily(Font(R.font.defaultfont)),
                )
                Info.usedIngredients.forEachIndexed { idx, ing ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = ing.name,
                            color = Color(0xFFCCD5AE),
                            fontWeight = FontWeight.W400,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.defaultfont)),
                        )
                        Row {
                            Text(
                                text =
                                    if (ing.amount % 1.0 ==
                                        0.0
                                    ) {
                                        (ing.amount.toInt().toString())
                                    } else {
                                        ing.amount.toString()
                                    },
                                color = Color(0xFF07b56d),
                                fontSize = 15.sp,
                                fontFamily = FontFamily(Font(R.font.defaultfont)),
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = ing.unit.toString(),
                                color = Color(0xFF07b56d),
                                fontSize = 15.sp,
                                fontFamily = FontFamily(Font(R.font.defaultfont)),
                            )
                        }
                    }
                    if (idx != Info.usedIngredientsCount - 1) Spacer(modifier = Modifier.height(3.dp))
                }
                Info.missedIngredients.forEachIndexed { idx, ing ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = ing.name,
                            color = Color(0xFFD4A373),
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.defaultfont)),
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text(
                                text =
                                    if (ing.amount % 1.0 ==
                                        0.0
                                    ) {
                                        (ing.amount.toInt().toString())
                                    } else {
                                        ing.amount.toString()
                                    },
                                color = Color(0xFFD4A373),
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.defaultfont)),
                            )
                            if (ing.unit != null) {
                                Text(
                                    text = ing.unit,
                                    color = Color(0xFFD4A373),
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.defaultfont)),
                                )
                            }
                        }
                    }
                    if (idx != Info.missedIngredientsCount - 1) Spacer(modifier = Modifier.height(3.dp))
                }
            }
        }
    }
}
