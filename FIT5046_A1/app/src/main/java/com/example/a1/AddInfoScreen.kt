package com.example.a1

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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

    val sexOptions = listOf("Female", "Male", "Other", "Prefer not to say")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Personalise Fitness and Health Details",
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Name input
        OutlinedTextField(
            value = name,
            onValueChange = { if (it.all { char -> char.isLetter()} && it.length <= 20) name = it },
            label = { Text("Name (letters only)") },
            isError = showError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            trailingIcon = {
                if (showError) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colors.error
                    )
                }
            }
        )
        if (showError) {
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
                        datePicker.maxDate = calendar.timeInMillis  // 设置最大日期为当前日期
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
                offset = DpOffset(x = 0.dp, y = 0.dp), // 根据需要调整这个位置
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
            isError = height.toIntOrNull() !in 1..400 && height.isNotBlank(),  // 检查数值是否在允许的范围之外
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
            isError = weight.toIntOrNull() !in 1..400 && weight.isNotBlank(),  // 检查数值是否在允许的范围之外
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

        Button(
            modifier = Modifier
                .height(50.dp)
                .width(200.dp),
            onClick = {
                showError = true // Show errors if any validation fails
                if (name.isNotBlank() && dateOfBirth.isNotBlank() && sex.isNotBlank() &&
                    height.toIntOrNull() in 1..400 && weight.toIntOrNull() in 1..400) {
                    showError = false
                    // Implement navigation or further processing logic here
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }else {
                    Toast.makeText(context, "Invalid input, please enter again", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text("Fitness Now!", fontSize = 16.sp)
        }
    }

}