package com.example.a1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import android.app.DatePickerDialog
import android.widget.Toast

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavHostController) {
    val stepsCount = "4390"
    val totalDistance = "3.3 KM"


    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE4E4FC),
                        Color(0xFFFAE8E1)
                    )
                )
            )
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Transparent),
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                painterResource(id = R.drawable.back),
                                contentDescription = "Back",
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(
                                modifier = Modifier
                                    .padding(start = 50.dp)
                            )
                        }

                    },

                    title = {
                        Text(
                            "          ACTIVE REPORT",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF151C57)
                        )
                    },
                    actions = {
                        IconButton(onClick = { showDatePicker(context) { selectedDate ->

                             Toast.makeText(context, "Selected date: $selectedDate", Toast.LENGTH_LONG).show()
                        } }) {
                            //
                            Icon(
                                painter = painterResource(id = R.drawable.date),
                                contentDescription = "Date Picker",
                            )
                        }
                    },
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .background(color = Color.Transparent),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Subtitle section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            stepsCount,
                            color = Color(0xFF151C57),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.width(4.dp)) //
                        Text(
                            "steps",
                            color = Color(0xFF8E91B9),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            totalDistance,
                            color = Color(0xFF151C57),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        //
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "KM",
                            color = Color(0xFF8E91B9),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                // Placeholder for the chart
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),

                    shape = RoundedCornerShape(15.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White  //
                    )

                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(250.dp)
                            .background(Color.Transparent)
                    ) {
                        // Placeholder for future chart
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Row(

                    horizontalArrangement = Arrangement.SpaceBetween,
                ){
                    Text(
                        "Your monthly steps report",
                        color = Color(0xFF151C57),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.share),
                        contentDescription = "Share",
                    )
                }
                // Another subtitle section


                Spacer(modifier = Modifier.height(16.dp))

                // Placeholder for the other chart
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(15.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White  //
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(250.dp)
                            .background(Color.Transparent)
                    ) {
                        // Placeholder for future chart
                    }
                }
            }
        }
    }
}

fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(context, { _, year, month, dayOfMonth ->
        val selectedDate = Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate.time)
        onDateSelected(formattedDate)
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
}
