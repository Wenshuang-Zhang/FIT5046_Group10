package com.example.a1

import androidx.compose.runtime.Composable

import androidx.navigation.compose.rememberNavController
//noinspection UsingMaterialAndMaterial3Libraries

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import androidx.navigation.compose.rememberNavController
import com.example.a1.ui.theme.A1Theme


@Composable
fun ProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var heightError by remember { mutableStateOf(false) }
    var weightError by remember { mutableStateOf(false) }

    val sexOptions = listOf("Female", "Male", "Other", "Prefer not to say")

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val uid = user?.uid
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(key1 = uid) {
        if (uid != null) {
            db.collection("usersInfo").document(uid).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    name = document.getString("name") ?: ""
                    dateOfBirth = document.getString("dateOfBirth") ?: ""
                    sex = document.getString("sex") ?: ""
                    height = document.getString("height") ?: ""
                    weight = document.getString("weight") ?: ""
                }
            }.addOnFailureListener { e ->
                Log.w("Firestorm", "Error fetching document", e)
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color(0xFFE4E4FC), Color(0xFFFAE8E1))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,  // Adjust this to start from the top
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            //title
            Text(
                "Account",
                modifier = Modifier.weight(1f),
                color = Color(0xFF151C57),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            //Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                "This information ensures Fitness and Health data are as accurate as possible",
                fontSize = 16.sp,
                color = Color(0xFF8E91B9),
                modifier = Modifier.padding(20.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Card(
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, bottom = 25.dp, start = 12.dp, end = 12.dp),
                elevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 10.dp, start = 24.dp, end = 24.dp)
                ) {
                    InformationRow("Name", name, onValueChange = {
                        name = it
                        nameError = it.any { char -> !char.isLetter() } || it.length > 20
                    }, error = nameError)
                    Spacer(modifier = Modifier.height(16.dp))

                    //date of birth
                    InformationRow(
                        label = "Date of Birth",
                        value = dateOfBirth,
                        readOnly = true,
                        onClick = {
                            showDatePicker(context) { newDate -> dateOfBirth = newDate }
                        },
                        iconType = "calendar"
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    //sex
                    Box(modifier = Modifier.fillMaxWidth()) {
                        InformationRow(
                            label = "Sex",
                            value = sex,
                            readOnly = true,
                            onClick = { expanded = !expanded },
                            iconType = "dropdown"
                        )
                        // display the DropdownMenu when click expended button
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                offset = DpOffset(x = 114.dp, y = 0.dp),
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
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                //Height
                    InformationRow("Height (cm)", height, onValueChange = {
                        height = it
                        heightError = it.toIntOrNull()?.let { num -> num < 1 || num > 400 } ?: true
                    }, error = heightError)

                    Spacer(modifier = Modifier.height(16.dp))

                //Weight
                    InformationRow("Weight (kg)", weight, onValueChange = {
                        weight = it
                        weightError = it.toIntOrNull()?.let { num -> num < 1 || num > 400 } ?: true
                    }, error = weightError)

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    ) {
                        // save changes button
                        Button(
                            onClick = {
                                saveUserInfo(
                                    db,
                                    uid,
                                    name,
                                    dateOfBirth,
                                    sex,
                                    height,
                                    weight,
                                    context
                                )
                            },
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .width(180.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF776EE3)),
                        ) {
                            Text("Save Changes", color = Color.White, fontSize = 16.sp)

                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        //logout button
                        Button(
                            onClick = {
                                // Handle logout logic
                                auth.signOut()
                                navController.navigate("welcome") {
                                    popUpTo("welcome") {
                                        inclusive = true
                                    } // Clear back stack up to profile screen
                                }
                            },
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .width(180.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF776EE3))
                        ) {
                            Text("Log out", color = Color.White, fontSize = 16.sp)
                        }


                    }
                }
            }

        }



    }
}

@Composable
fun InformationRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    readOnly: Boolean = false,
    error: Boolean = false,
    onClick: (() -> Unit)? = null,
    iconType: String = "calendar"
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f))

        if (readOnly) {
            if (onClick != null) {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
                    modifier = Modifier.weight(2f)  // Ensures the button expands to fill space
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(value, color = Color.Black, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))

                        Icon(
                            imageVector = if (iconType == "calendar") Icons.Filled.CalendarToday else Icons.Filled.ArrowDropDown,
                            contentDescription = if (iconType == "calendar") "Select Date" else "Expand",
                            modifier = Modifier.padding(start = 4.dp) // Optional padding between text and icon
                        )
                    }
                }
            } else {
                Text(value, modifier = Modifier.weight(2f))
            }
        } else {
            TextField(
                value = value,
                onValueChange = onValueChange,
                isError = error,
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    errorIndicatorColor = if (error) Color.Red else Color.Transparent
                ),
                modifier = Modifier.weight(2f)
            )
            if (error) {
                Text("Invalid input", color = Color.Red, style = MaterialTheme.typography.caption)
            }
        }
    }
}



fun saveUserInfo(db: FirebaseFirestore, uid: String?, name: String, dateOfBirth: String, sex: String, height: String, weight: String, context: Context) {
    if (uid != null) {
        val heightInCm = height.toDoubleOrNull() ?: 0.0
        val weightInKg = weight.toDoubleOrNull() ?: 0.0

        //calculate calorie goal
        val calorieGoal = if (sex == "Male") {
            ((88.362 + (13.397 * weightInKg) + (4.799 * heightInCm) - (5.677 * 25) + 300).toInt())
        } else {
            ((447.593 + (9.247 * weightInKg) + (3.098 * heightInCm) - (4.330 * 25) + 300).toInt())
        }
        val calorieGoalStr = calorieGoal.toString()

        val userInfo = hashMapOf(
            "name" to name,
            "dateOfBirth" to dateOfBirth,
            "sex" to sex,
            "height" to height,
            "weight" to weight,
            "calorieGoal" to calorieGoalStr,
        )
        db.collection("usersInfo").document(uid).set(userInfo)
            .addOnSuccessListener {
                Toast.makeText(context, "Information saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to save information: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }else {
        Toast.makeText(context, "User ID is null, cannot save information.", Toast.LENGTH_LONG).show()
    }
}


