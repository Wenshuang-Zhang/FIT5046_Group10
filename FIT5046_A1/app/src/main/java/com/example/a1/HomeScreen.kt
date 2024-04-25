package com.example.a1

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.PathEffect


@Composable
fun HomeScreen(navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE4E4FC), // background color
                        Color(0xFFFAE8E1)
                    )
                )
            )
    ) {
        item {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 15.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today",
                    color = Color(0xFF151C57),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 14.dp)
                )
                Row(
                    modifier = Modifier
                        .padding(start = 180.dp)
                ) {
                    Text(
                        text = "Hi, Bella",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF151C57),
                        modifier = Modifier
                            .padding(vertical = 14.dp)
                    )
                }

            }
        }
        item {
            CircularProgress(progress = 0.8f)
        }
//        item {
//            Text(
//                text = "85%",
//                color = Color(0xFF151C57),
//                fontSize = 50.sp,
//                textAlign = TextAlign.Center,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier
//                    .fillMaxWidth()
//            )
//        }
        item {
            // 提示信息
            Text(
                text = "Cheer up, Bella! You have reach 80% CALORIES GOAL today, Keep moving!",
                color = Color(0xFF8E91B9),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 40.dp)
            )
        }
        item { WeatherCard() }
        item { StepCard(navController = navController)} // step card
        item { CalorieCard() } // calorie card
        item { SleepCard() } // sleep card
        item { HeartRateCard() } //
    }
}

@Composable
fun CircularProgress(progress: Float, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(IntrinsicSize.Min)
    ) {

        Canvas(modifier = Modifier
            .size(180.dp) //
            .align(Alignment.Center)
        ) {

            val strokeWidth = 40f
            val radius = size.minDimension / 2 - strokeWidth / 2
            val topLeftOffset = Offset(strokeWidth / 2, strokeWidth / 2)
            val arcSize = Size(radius * 2, radius * 2)
            val progressAngle = 360 * progress
            // Draw the background circle
            drawArc(
                color = Color(0xFFE2DFEB),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeftOffset,
                size = arcSize,
                style = Stroke(width = strokeWidth, pathEffect = PathEffect.cornerPathEffect(radius))
            )

            // Draw the progress circle
            drawArc(
                color = Color(0xFFAB93EF),
                startAngle = -90f,
                sweepAngle = progressAngle,
                useCenter = false,
                topLeft = topLeftOffset,
                size = arcSize,
                style = Stroke(width = strokeWidth, pathEffect = PathEffect.cornerPathEffect(radius))
            )
        }

        // Draw the progress text
        Text(
            text = "${(progress * 100).toInt()}%",
            color = Color(0xFF151C57),
            fontSize = 46.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun WeatherCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 12.dp)
            .heightIn(min = 50.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Temperature 18, mostly sunny, L: 14 H: 21",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF151C57),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StepCard(navController: NavHostController) {
    //
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { /* */ }
            .heightIn(min = 100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text("Steps", fontWeight = FontWeight.Bold, color = Color(0xFF151C57))
            Text("4320 steps, Goal: 8000 steps", color = Color(0xFF8E91B9))
            //
        }
    }
}

@Composable
fun CalorieCard() {
    //
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { /*  */ }
            .heightIn(min = 100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text("Calories", fontWeight = FontWeight.Bold, color = Color(0xFF151C57))
            Text("290 kcal", color = Color(0xFF8E91B9))
            //
        }
    }
}

@Composable
fun SleepCard(
    cardBackgroundColor: Color = Color.White,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .heightIn(min = 100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)

        ) {
            Text("Sleep", fontWeight = FontWeight.Bold, color = Color(0xFF151C57))
            Text("6.5 Hours", color = Color(0xFF8E91B9))
            //
        }
    }
}

@Composable
fun HeartRateCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { /*  */ }
            .heightIn(min = 100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text("Heart Rate", fontWeight = FontWeight.Bold, color = Color(0xFF151C57))
            Text("75 bpm, 3h ago", color = Color(0xFF8E91B9))
            // UI
        }
    }
}


