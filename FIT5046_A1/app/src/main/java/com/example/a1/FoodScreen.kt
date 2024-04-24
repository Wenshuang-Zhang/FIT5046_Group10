package com.example.a1


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import java.util.*


data class FoodItem(val name: String, val calories: Int)

class MealManager {
    var breakfastItems = mutableStateListOf<FoodItem>()
    var lunchItems = mutableStateListOf<FoodItem>()
    var dinnerItems = mutableStateListOf<FoodItem>()

    val totalCalories: Int
        get() = (breakfastItems.sumOf { it.calories } +
                lunchItems.sumOf { it.calories } +
                dinnerItems.sumOf { it.calories })

    fun addFoodToMeal(meal: String, foodItem: FoodItem) {
        when (meal) {
            "Breakfast" -> breakfastItems.add(foodItem)
            "Lunch" -> lunchItems.add(foodItem)
            "Dinner" -> dinnerItems.add(foodItem)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FoodScreen(mealManager: MealManager) {
    var showDialog by remember { mutableStateOf(false) }
    var currentMealType by remember { mutableStateOf("") }
    val totalCalories = 1800
    val consumedCalories = mealManager.totalCalories
    val remainingCalories = totalCalories - consumedCalories

    Scaffold(
        content = { padding ->
            Column(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE4E4FC), // Top color
                                Color(0xFFFAE8E1)  // Bottom color
                            )
                        )
                    )
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                CaloriesCard(remainingCalories)
                Spacer(modifier = Modifier.height(16.dp))
                MealCategory("Breakfast", mealManager.breakfastItems) { currentMealType = "Breakfast"; showDialog = true }
                MealCategory("Lunch", mealManager.lunchItems) { currentMealType = "Lunch"; showDialog = true }
                MealCategory("Dinner", mealManager.dinnerItems) { currentMealType = "Dinner"; showDialog = true }
                if (showDialog) {
                    AddFoodDialog(
                        mealType = currentMealType,
                        onAddFood = { foodItem ->
                            mealManager.addFoodToMeal(currentMealType, foodItem)
                            showDialog = false
                        },
                        onDismiss = { showDialog = false } // Add this line to handle dialog dismissal
                    )
                }

            }
        }
    )
}

@Composable
fun CaloriesCard(remainingCalories: Int) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth() // Ensure the Column fills the Card's width
                .background(Color.White) // Set the background color of the Column to white
                .padding(16.dp)
        ) {
            Text(text = "Calories Remaining", style = MaterialTheme.typography.titleLarge,fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End // This pushes the content to the end (right)
            ) {
                Text(
                    text = "$remainingCalories",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealCategory(mealType: String, foodItems: List<FoodItem>, onAddFoodClick: () -> Unit) {
    val totalCaloriesForMeal = foodItems.sumOf { it.calories }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // For a slight shadow
    ) {
        Column(
            modifier = Modifier
                .background(Color.White) // Apply background color here
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = mealType,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 20.sp, // Adjust font size as needed
                        fontWeight = FontWeight.Medium // Adjust font weight as needed
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "$totalCaloriesForMeal",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 18.sp // Adjust the font size as needed
                    )
                )

            }

            foodItems.forEach { foodItem ->
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = foodItem.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "${foodItem.calories} kcal",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            TextButton(
                onClick = onAddFoodClick
            ) {
                Text("Add Food",style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 17.sp,fontWeight = FontWeight.Bold))// Adjust the font size as needed)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddFoodDialog(mealType: String, onAddFood: (FoodItem) -> Unit, onDismiss: () -> Unit) {
    var foodName by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        text = {
            Column (modifier = Modifier.padding(12.dp).widthIn(max = 280.dp)){
                // Food Type input with half-width field and underline
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Food Type",
                        modifier = Modifier.width(100.dp), // Adjust width as necessary
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
                    )
                    Box(modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)) {
                        BasicTextField(
                            value = foodName,
                            onValueChange = { foodName = it },
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Column {
                                    innerTextField() // 这里显示输入框
                                    Divider(color = Color.Gray, thickness = 1.dp) // 紧接着显示下划线
                                }
                            },
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start),
                            cursorBrush = SolidColor(Color.Black)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Calories input with half-width field and underline
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Calories",
                        modifier = Modifier.width(100.dp), // Adjust width as necessary
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
                    )
                    Box(modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)) {
                        BasicTextField(
                            value = calories,
                            onValueChange = { calories = it },
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Column {
                                    innerTextField() // 这里显示输入框
                                    Divider(color = Color.Gray, thickness = 1.dp) // 紧接着显示下划线
                                }
                            },
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start),
                            cursorBrush = SolidColor(Color.Black)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (foodName.isNotBlank() && calories.toIntOrNull() != null) {
                        onAddFood(FoodItem(foodName, calories.toInt()))
                        onDismiss() // Dismiss the dialog after adding the food item
                    }
                },
                colors = buttonColors(
                    containerColor = Color(0xFF6200EE) // Example of setting to a purple color
                )
            ) {
                Text("Add", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() },
                colors = ButtonDefaults.textButtonColors( // Use TextButton for the dismiss for a different style
                    contentColor = Color(0xFF6200EE) // Purple color for the text button
                )
            ) {
                Text("Cancel")
            }
        },
        containerColor = Color.White, // Ensure your theme supports this or use a custom theme if needed
        properties = DialogProperties(usePlatformDefaultWidth = false) // To manage dialog width or other properties
    )
}


