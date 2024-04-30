package com.example.a1


import androidx.compose.material3.*
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(navController: NavHostController) {
    // Prepare states for date picker and dropdowns
    val context = LocalContext.current
    var dateOfBirth by remember { mutableStateOf("11 Jan 2000") }
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val validHeightRange = 50..300
    val validWeightRange = 10..300
    val heightErrorState = remember { mutableStateOf(false) }
    val weightErrorState = remember { mutableStateOf(false) }

    // States for height and weight
    var heightInput by remember { mutableStateOf("170") }
    var weightInput by remember { mutableStateOf("65") }



    // Show DatePickerDialog
    val showDatePickerDialog: () -> Unit = {

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                dateOfBirth = dateFormat.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

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
    ) {
        Scaffold(containerColor = Color.Transparent) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Bella Z",
                    color = Color(0xFF151C57),
                    fontWeight = FontWeight.Bold,
                    fontSize = 45.sp
                )
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Detail",
                    color = Color(0xFF8E91B9),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "This information ensures Fitness and Health data are accurate as possible",
                    color = Color(0xFF8E91B9),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                DetailItemWithDropdown(
                    label = "     Date of Birth",
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    onClick = { showDatePickerDialog() }
                )
                InputFieldItem(
                    label = "Height (cm)",
                    value = heightInput,
                    range = validHeightRange,
                    onValueChange = { heightInput = it },
                    errorState = heightErrorState
                )

                // Weight input field with validation
                InputFieldItem(
                    label = "Weight (kg)",
                    value = weightInput,
                    range = validWeightRange,
                    onValueChange = { weightInput = it },
                    errorState = weightErrorState
                )

                //logout button
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // Firebase Auth instance
                        val auth = FirebaseAuth.getInstance()

                        // Sign out the current user
                        auth.signOut()

                        // Navigate to the welcome screen
                        navController.navigate("welcome") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .width(180.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF776EE3))
                ) {
                    Text("Logout", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailItemWithDropdown(
    label: String,
    value: String,
    options: List<String> = emptyList(),
    expanded: Boolean = false,
    onValueChange: (String) -> Unit = {},
    onExpandedChange: (Boolean) -> Unit = {},
    onClick: (() -> Unit)? = null
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (options.isNotEmpty()) {
            // It's a dropdown menu for height or weight
            var selectedIndex by remember { mutableIntStateOf(options.indexOf(value)) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = onExpandedChange
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(label) },
                    trailingIcon = {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                            contentDescription = if (expanded) "Close menu" else "Open menu"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { onExpandedChange(false) }
                ) {
                    options.forEachIndexed { index, option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onValueChange(option)
                                selectedIndex = index
                                onExpandedChange(false)
                            }
                        )
                    }
                }
            }
        } else if (onClick != null) {
            // It's the date picker for date of birth
            OutlinedButton(onClick = onClick) {
                Text(value)
            }
        }
    }
}






@Composable
fun InputFieldItem(
    label: String,
    value: String,
    range: IntRange,
    onValueChange: (String) -> Unit,
    errorState: MutableState<Boolean>
) {
    val isError = errorState.value
    val errorMessage = "Enter a number between ${range.first} and ${range.last}"

    Column {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // 更新状态值，无论它是否在范围内
                onValueChange(newValue)
                // 更新错误状态
                errorState.value = newValue.toIntOrNull()?.let { it !in range } ?: true
            },
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            isError = isError
        )
        // 显示错误消息（如果有）
        if (isError && value.isNotEmpty()) { // 添加了 value.isNotEmpty() 来防止在输入为空时显示错误消息
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall, // 这里使用了 MaterialTheme.typography.bodySmall 替代了 TextStyle
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
