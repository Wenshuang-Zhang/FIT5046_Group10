package com.example.a1

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.DpOffset
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.time.LocalDate


data class HistoryItem(val type: String, val kcal: Int, val trainingDate: LocalDate, val trainingTime: Int)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(navController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }
    val historyItems = remember { mutableStateListOf<HistoryItem>() }

    if (showDialog) {
        AddDialog(historyItems) { showDialog = false }
    }

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid


    LaunchedEffect(key1 = uid) {
        if (uid != null) {
            db.collection("trainingHistory").document(uid).collection("cards").get().addOnSuccessListener { collection ->
                if (!collection.isEmpty) {
                    historyItems.clear()
                    for (document in collection.documents) {
                        val type = document.getString("type") ?: "Unknown"
                        val kcal = document.getLong("kcal")?.toInt() ?: 0
                        val trainingTime = document.getLong("trainingTime")?.toInt() ?: 0

                        // Assuming trainingDate is stored in a map-like structure in Firestore
                        val dateMap = document.get("trainingDate") as? Map<String, Any> ?: continue
                        val year = (dateMap["year"] as? Long)?.toInt() ?: continue
                        val month = (dateMap["monthValue"] as? Long)?.toInt()
                            ?: continue  // monthValue is 1-based
                        val dayOfMonth = (dateMap["dayOfMonth"] as? Long)?.toInt() ?: continue

                        val trainingDate = LocalDate.of(year, month, dayOfMonth)

                        val newItem = HistoryItem(type, kcal, trainingDate, trainingTime)
                        historyItems.add(newItem)
                    }
                }
            }.addOnFailureListener { e ->
                Log.w("Firestorm", "Error fetching document", e)
            }
        }
    }

//    val historyItems = listOf(
//        HistoryItem("CYCLING", 400, LocalDateTime.parse("12 Feb 2024", DateTimeFormatter.ofPattern("dd MMM yyyy"))),
//        HistoryItem("HIIT", 500, LocalDateTime.parse("11 Feb 2024", DateTimeFormatter.ofPattern("dd MMM yyyy"))),
//        HistoryItem("YOGA", 350, LocalDateTime.parse("10 Feb 2024", DateTimeFormatter.ofPattern("dd MMM yyyy"))),
//        HistoryItem("TREADMILL", 420, LocalDateTime.parse("12 May 2023", DateTimeFormatter.ofPattern("dd MMM yyyy"))),
//        HistoryItem("PILATES", 530, LocalDateTime.parse("11 May 2023", DateTimeFormatter.ofPattern("dd MMM yyyy")))
//    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE4E4FC),
                            Color(0xFFEAE8E1),
                        )
                    )
                )
        ) {
            Text(
                text = "History",
                color = Color(0xFF151C57),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 0.dp)
            )

            LazyColumn(contentPadding = PaddingValues(all = 16.dp)) {
                val groupedByMonth = historyItems.groupBy {
                    it.trainingDate.format(DateTimeFormatter.ofPattern("MMM yyyy"))
                }
                groupedByMonth.forEach { (month, itemsInMonth) ->
                    stickyHeader {
                        Text(text = month, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Gray)
                    }
                    items(itemsInMonth) { item ->
                        HistoryCard(item) //需要修改传入的参数
                    }
                }
            }
        }
        // 添加 FloatingActionButton
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 15.dp, end = 16.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add"
            )
        }
    }
}




@Composable
fun AddDialog(historyItems: SnapshotStateList<HistoryItem>, onDismiss: () -> Unit) {
    val trainingTypes = listOf("CYCLING", "HIIT", "YOGA", "TREADMILL", "PILATES")
    var selectedTrainingType by remember { mutableStateOf(trainingTypes[0]) }
    var time by remember { mutableStateOf("") }
    var trainingDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                var expanded by remember { mutableStateOf(false) }
                //val trainingTypes = listOf("CYCLING", "HIIT", "YOGA", "TREADMILL", "PILATES")

                if (showDatePicker) {
                    DatePickerDialog(
                        date = trainingDate,
                        onDateSelected = { date ->
                            trainingDate = date
                            showDatePicker = false
                        },
                        onDismissRequest = { showDatePicker = false }
                    )
                }

                ClickableText(
                    text = AnnotatedString(trainingDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))),
                    onClick = { showDatePicker = true },
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth()){
                    OutlinedTextField(
                        value = selectedTrainingType,
                        onValueChange = { selectedTrainingType = it },
                        label = { Text("Training Type") },
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowDropDown, "Drop Down", Modifier.clickable { expanded = true })
                        },
                        readOnly = true
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset = DpOffset(x = 114.dp, y = 0.dp),
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        trainingTypes.forEach { label ->
                            DropdownMenuItem(onClick = {
                                selectedTrainingType = label
                                expanded = false
                            }) {
                                Text(label)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Training Time") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if (selectedTrainingType.isNotEmpty() && time.isNotEmpty()) {
                        val trainingTime = time.toIntOrNull() ?: 0
                        val kcal = calculateKcal(selectedTrainingType, trainingTime)
                        val newItem = HistoryItem(selectedTrainingType, kcal, trainingDate, trainingTime)
                        historyItems.add(newItem)
                        uploadToFirebase(newItem)
                        onDismiss()
                    } }) {
                        Text("Add", color = Color.White)
                    }
                }
            }
        }
    }
}

fun uploadToFirebase(item: HistoryItem) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    val db = FirebaseFirestore.getInstance()
    if (uid != null) {
        db.collection("trainingHistory").document(uid).collection("cards").add(item)
    }
}

@Composable
fun DatePickerDialog(
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val currentYear = date.year
    val currentMonth = date.monthValue - 1 // Calendar.MONTH is zero-based in Java
    val currentDay = date.dayOfMonth

    // Remember a DatePickerDialog to avoid recreation on recompositions
    val datePickerDialog = remember {
        DatePickerDialog(context, { _: DatePicker, year: Int, month: Int, day: Int ->
            // Update the date when a new date is picked
            val newDate = LocalDate.of(year, month + 1, day)
            onDateSelected(newDate)
        }, currentYear, currentMonth, currentDay).apply {
            // Dismiss listener
            setOnDismissListener {
                onDismissRequest()
            }
            // Set the max date to the current date
            datePicker.maxDate = System.currentTimeMillis()
        }
    }

    // Handle dialog showing and updates
    LaunchedEffect(date) {
        datePickerDialog.updateDate(currentYear, currentMonth, currentDay)
        datePickerDialog.show()
    }
}

fun calculateKcal(trainingType: String, trainingTime: Int): Int {
    return when (trainingType) {
        "CYCLING" -> 12 * trainingTime
        "HIIT" -> 8 * trainingTime
        "YOGA" -> 3 * trainingTime
        "TREADMILL" -> 10 * trainingTime
        "PILATES" -> 6 * trainingTime
        else -> 0 // 如果输入的训练类型不匹配任何已知类型，则返回0
    }
}


@Composable
fun HistoryCard(item: HistoryItem) {
    val kcal = calculateKcal(item.type, item.trainingTime)
    val formattedDate = item.trainingDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .heightIn(min = 50.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.type,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF7FB2EF)
                )
                Text(
                    text = formattedDate,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF7FB2EF)
                )
            }
            Text(
                text = "$kcal KCAL",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF151C57),
                fontSize = 20.sp
            )
            Text(
                text = "${item.trainingTime} Minutes",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF151C57),
                fontSize = 20.sp
            )
        }
    }
}
