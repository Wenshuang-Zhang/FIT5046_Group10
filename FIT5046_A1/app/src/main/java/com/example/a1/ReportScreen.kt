package com.example.a1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import android.app.DatePickerDialog
import android.widget.Toast


import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.text.style.TextAlign
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun ReportScreen(navController: NavHostController) {
    val stepsCount = "790"
    val totalDistance = "85"


    val context = LocalContext.current

    LazyColumn(
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
        item{
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE4E4FC),
                                Color(0xFFFAE8E1)
                            )
                        )
                    ),



                ) {

                Spacer(modifier = Modifier.height(16.dp))
                //top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "         ACTIVE REPORT",
                        modifier = Modifier.weight(1f),
                        color = Color(0xFF151C57),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    //8E91B9

                    //date picker
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(width = 46.dp, height = 34.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(10.dp))
                            .clickable {
                                showDatePicker(context) { selectedDate ->
                                    Toast.makeText(context, "Selected date: $selectedDate", Toast.LENGTH_LONG).show()
                                }
                            }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.date),
                            contentDescription = "Date Picker",
                            modifier = Modifier.size(26.dp),
                            tint = Color.Unspecified
                            //A2A8BE
                        )
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 10.dp))//gap
                }

                //below top bar
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Kcal and min
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
                                "Kcal",
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
                                "Min",
                                color = Color(0xFF8E91B9),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))//gap

                    //report 1 text
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ){
                        Text(
                            "Your activity report",
                            color = Color(0xFF151C57),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 58.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.export),
                            contentDescription = "Share",
                            tint = Color(0xFFA2A8BE)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp)) //gap

                    // report 1 card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 2.dp),

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

                    Spacer(modifier = Modifier.height(22.dp))//gap

                    //report 2 text
                    Row(

                        horizontalArrangement = Arrangement.SpaceBetween,
                    ){
                        Text(
                            "Your steps report",
                            color = Color(0xFF151C57),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 58.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.export),
                            contentDescription = "Share",
                            tint = Color(0xFFA2A8BE)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))//gap

                    // report 2 card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 2.dp),

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

                    // Placeholder for the other chart

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
