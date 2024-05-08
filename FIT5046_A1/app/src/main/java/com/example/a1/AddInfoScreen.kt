package com.example.a1

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

import androidx.compose.ui.text.font.FontWeight
import java.util.*

@Composable
fun AddInfoScreen(navController: NavHostController) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) } //
    var userHasInteracted by remember { mutableStateOf(false) }

    val sexOptions = listOf("Female", "Male", "Other", "Prefer not to say")
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val uid = user?.uid  // Get the current user's UID

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE4E4FC), // Top half background color
                        Color(0xFFFAE8E1)  // Bottom half background color
                    )
                )
            )
    )
    {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        //verticalArrangement = Arrangement.Center
    ) {

        //title
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            "Personalise Fitness and Health Details",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF151C57),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        //sub title
        Text(
            "This information ensures Fitness and Health data are as accurate as possible",
            fontSize = 16.sp,
            color = Color(0xFF8E91B9),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Name input
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                userHasInteracted = true
            },
            label = { Text("Name (letters only)") },

            isError = userHasInteracted && (!name.all { char -> char.isLetter()} || name.length > 20 || name.isBlank()),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            trailingIcon = {
                if (userHasInteracted && (!name.all { char -> char.isLetter()} || name.length > 20 || name.isBlank())) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colors.error
                    )
                }
            }
        )
        if (userHasInteracted && (!name.all { char -> char.isLetter()} || name.length > 20 || name.isBlank())) {
            Text(
                "Please enter only letters, no more than 20 characters",
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        Spacer(Modifier.height(8.dp))

        // Date of Birth input
        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = { /* Read-only field */ },
            label = { Text("Date of Birth") },
            readOnly = true,
            isError = showError && dateOfBirth.isBlank(),
            trailingIcon = {
                IconButton(onClick = {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(
                        context, { _, year, month, day ->
                            dateOfBirth = "$day/${month + 1}/$year"
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).apply {
                        datePicker.maxDate = calendar.timeInMillis  //set the latest time is today
                    }.show()
                }) {
                    Icon(Icons.Filled.CalendarToday, contentDescription = "Select Date")
                }
            }
        )
        Spacer(Modifier.height(8.dp))

        // Sex Dropdown
        Box {
            OutlinedTextField(
                value = sex,
                onValueChange = { /* No action needed here */ },
                label = { Text("Sex") },
                readOnly = true,
                isError = showError && sex.isBlank(),
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")
                    }
                },
                modifier = Modifier.align(Alignment.TopStart)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = 0.dp, y = 0.dp),
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                sexOptions.forEach { label ->
                    DropdownMenuItem(onClick = {
                        sex = label
                        expanded = false
                    }) {
                        Text(text = label)
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))


        // Height input
        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Height (cm)") },
            isError = height.toIntOrNull() !in 1..400 && height.isNotBlank(),  // check input content
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            trailingIcon = {
                if (height.toIntOrNull() !in 1..400 && height.isNotBlank()) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colors.error
                    )
                }
            }
        )
        if (height.toIntOrNull() !in 1..400 && height.isNotBlank()) {
            Text(
                "Please enter a number between 1 and 400",
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        // weight input
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight (kg)") },
            isError = weight.toIntOrNull() !in 1..400 && weight.isNotBlank(),  // check input content
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            trailingIcon = {
                if (weight.toIntOrNull() !in 1..400 && weight.isNotBlank()) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colors.error
                    )
                }
            }
        )
        if (weight.toIntOrNull() !in 1..400 && weight.isNotBlank()) {
            Text(
                "Please enter a number between 1 and 400",
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        //upload user info to firebase
        Button(
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .height(50.dp)
                .width(180.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF776EE3)),
            onClick = {
                showError = true
                if (name.isNotBlank() && dateOfBirth.isNotBlank() && sex.isNotBlank() &&
                    height.toIntOrNull() in 1..400 && weight.toIntOrNull() in 1..400) {
                    showError = false

                    //calculate calorie goal
                    val heightInCm = height.toDoubleOrNull() ?: 0.0
                    val weightInKg = weight.toDoubleOrNull() ?: 0.0
                    val calorieGoal = if (sex == "Male") {
                        ((88.362 + (13.397 * weightInKg) + (4.799 * heightInCm) - (5.677 * 25) + 300).toInt())
                    } else {
                        ((447.593 + (9.247 * weightInKg) + (3.098 * heightInCm) - (4.330 * 25) + 300).toInt())
                    }
                    val calorieGoalStr = calorieGoal.toString()

                    // Firestore instance
                    val db = FirebaseFirestore.getInstance()
                    val userInfo = hashMapOf(
                        "name" to name,
                        "dateOfBirth" to dateOfBirth,
                        "sex" to sex,
                        "height" to height,
                        "weight" to weight,
                        "calorieGoal" to calorieGoalStr,
                    )
                    if (uid != null) {
                        db.collection("usersInfo").document(uid).set(userInfo)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Information saved successfully", Toast.LENGTH_SHORT).show()
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error adding document", e)
                                Toast.makeText(context, "Failed to save information: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Invalid input, please enter again", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text("Fitness Now!", fontSize = 16.sp, color = Color.White)
        }
    }
    }

}