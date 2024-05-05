package com.example.a1

import android.content.Context
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

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
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.material.LinearProgressIndicator
import java.time.LocalDate
import kotlin.math.min

@Composable
fun HomeScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val context = LocalContext.current

    //get health data
    var steps by remember { mutableStateOf("Loading...") }
    var sleepHours by remember { mutableStateOf("Loading...") }
    var userName by remember { mutableStateOf("Loading...") }
    var percentage by remember { mutableStateOf(0.0f) }

    LaunchedEffect(uid) {
        if (uid != null) {
            fetchCalorieData(uid) { calculatedPercentage ->
                percentage = calculatedPercentage
            }
        }
    }


    //get user health data
    LaunchedEffect(user?.uid) {
        user?.uid?.let { uid ->
            db.collection("healthInfo").document(uid).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Assuming 'steps' and 'sleepHours' are stored as Long or similar numeric data
                    val stepsValue = document.getLong("steps") ?: 0L  // Get as Long, default to 0 if null
                    val sleepHoursValue = document.getLong("sleepHours") ?: 0L  // Get as Long, default to 0 if null

                    // Convert numeric values to string for display purposes
                    steps = stepsValue.toString()
                    sleepHours = sleepHoursValue.toString()
                } else {
                    steps = "0"
                    sleepHours = "0"
                }
            }.addOnFailureListener { e ->
                // Log error or handle failure to fetch data
                Log.e("Firestore", "Error fetching document: ${e.message}")
                steps = "Error loading data"
                sleepHours = "Error loading data"
            }
        }
    }

    //get user name
    LaunchedEffect(uid) {
        uid?.let {
            db.collection("usersInfo").document(it).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    userName = document.getString("name") ?: "User"  // Default to "User" if null
                } else {
                    userName = "User"
                }
            }.addOnFailureListener {
                userName = "Error"
            }
        }
    }

    //page
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
                        text = "Hi, $userName",
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
            if (uid != null) {
                CircularProgress(progress = percentage)
            }
        }

        item {
            Text(
                text = "Cheer up, $userName! You have reach ${(percentage * 100).toInt()}% CALORIES GOAL today, Keep moving!",
                color = Color(0xFF8E91B9),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 40.dp)
            )
        }

        //cards
        item(key = "weather") { WeatherCard(context) }
        item(key = "steps") { StepCard(steps) }
        item(key = "calorie") {
            if (uid != null) {
                calorieCard(uid)
            }
        }// calorie card
        item { SleepCard(sleepHours) } // sleep card
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
            .size(180.dp)
            .align(Alignment.Center)
        ) {
            val strokeWidth = 40f
            val radius = size.minDimension / 2 - strokeWidth / 2
            val topLeftOffset = Offset(strokeWidth / 2, strokeWidth / 2)
            val arcSize = Size(radius * 2, radius * 2)
            val progressAngle = 360 * progress
            // Background circle
            drawArc(
                color = Color(0xFFE2DFEB),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeftOffset,
                size = arcSize,
                style = Stroke(width = strokeWidth, pathEffect = PathEffect.cornerPathEffect(radius))
            )
            // Progress circle
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
        // Progress text
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
fun StepCard(steps: String) {
    val goalSteps = 18000
    // Convert safely, provide a fallback value if conversion fails
    val stepCount = steps.toIntOrNull() ?: 0
    val progress = stepCount.toFloat() / goalSteps.toFloat()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .heightIn(min = 100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 14.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.steps),
                    contentDescription = "Step Icon",
                    modifier = Modifier
                        .size(55.dp)
                        .padding(end = 16.dp)
                )

                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Text(
                        text = "Steps",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF151C57),
                        fontSize = 18.sp
                    )
                    Text(
                        text = "$stepCount steps          Goal: $goalSteps steps",
                        color = Color(0xFF8E91B9),
                        fontSize = 14.sp
                    )
                }
            }
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .width(350.dp)
                    .height(30.dp)
                    .padding(top = 10.dp, bottom = 10.dp, start = 30.dp, end = 24.dp)
                    .clip(RoundedCornerShape(15.dp)),
                backgroundColor = Color.LightGray,
                color = Color(0xFF80BDFD),
            )
        }
    }
}

//for calculate the percentage of workout for CircularProgress
fun fetchCalorieData(userId: String, onComplete: (Float) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val today = LocalDate.now().toString()
    var totalKcal = 0
    var calorieGoal = 1  //

    // get goal calorie
    db.collection("usersInfo").document(userId)
        .get()
        .addOnSuccessListener { document ->
            calorieGoal = document.getString("calorieGoal")?.toIntOrNull() ?: 1
            // get the total calories for today from trainingHistory form
            db.collection("trainingHistory").document(userId).collection("cards")
                .whereEqualTo("trainingDate", today)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    totalKcal = querySnapshot.documents.sumOf { doc ->
                        doc.getLong("kcal")?.toInt() ?: 0
                    }
                    val percentage = (totalKcal.toFloat() / calorieGoal.toFloat()).coerceIn(0f, 1f)
                    onComplete(percentage)
                }
                .addOnFailureListener { e ->
                    println("Error fetching training cards: ${e.message}")
                }
        }
        .addOnFailureListener { e ->
            println("Error fetching user info: ${e.message}")
        }
}


@Composable
fun calorieCard(userId: String) {
    val db = FirebaseFirestore.getInstance()
    var totalKcal by remember { mutableStateOf(0) }
    var calorieGoal by remember { mutableStateOf(1) }
    var percentage by remember { mutableStateOf(0.0f) }

    val today = LocalDate.now().toString()

    LaunchedEffect(key1 = userId) {
        // get goal calorie
        db.collection("usersInfo").document(userId)
            .get()
            .addOnSuccessListener { document ->
                calorieGoal = document.getString("calorieGoal")?.toIntOrNull() ?: 1
            }

        // get the total calories for today from trainingHistory form
        db.collection("trainingHistory").document(userId).collection("cards")
            .whereEqualTo("trainingDate", today)
            .get()
            .addOnSuccessListener { querySnapshot ->
                totalKcal = querySnapshot.documents.sumOf { doc -> doc.getLong("kcal")?.toInt() ?: 0 }
                percentage = (totalKcal.toFloat() / calorieGoal.toFloat()).coerceIn(0f, 1f)
            }
    }

    //loading the card
    if (totalKcal != null && calorieGoal != null) {
        // show
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable { /* Handle Click */ }
                .heightIn(min = 100.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 14.dp, start = 20.dp, end = 20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.workout),
                        contentDescription = "Calories Icon",
                        modifier = Modifier
                            .size(55.dp)
                            .padding(end = 16.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        Text(
                            text = "Calories",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF151C57),
                            fontSize = 18.sp
                        )
                        Text(
                            text = "$totalKcal kcal          Goal: $calorieGoal kcal",
                            color = Color(0xFF8E91B9),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    } else {
        // loading
        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
        }
    }

}



@Composable
fun SleepCard(sleepHours: String) {
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 14.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sleep),
                    contentDescription = "Sleep Icon",
                    modifier = Modifier
                        .size(55.dp)
                        .padding(end = 16.dp)
                )
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    Text(
                        text = "Sleep",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF151C57),
                        fontSize = 18.sp)

                    Text(
                        text = "$sleepHours Hours",
                        color = Color(0xFF8E91B9),
                        fontSize = 14.sp)
                }
            }
        }
    }
}


@Composable
fun WeatherCard(context: Context) {
    var weatherInfo by remember { mutableStateOf("Loading weather data...") }

    LaunchedEffect(Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherService::class.java)
        val weatherCall = service.getCurrentWeather("London", "metric", "99310d74bca4fb0e17d121e4150acbf0")

        weatherCall.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        weatherInfo = "Temperature ${it.main.temp}°C, ${it.weather.first().description},\nLowest: ${it.main.temp_min}°C Highest: ${it.main.temp_max}°C"
                    }
                } else {
                    weatherInfo = "Failed to retrieve weather"
                }
            }
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                weatherInfo = "Error: ${t.message}"
            }
        })
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 12.dp)
            .heightIn(min = 50.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = weatherInfo,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF151C57),
                textAlign = TextAlign.Center
            )
        }
    }
}

// API Interface
interface WeatherService {
    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("q") city: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): Call<WeatherResponse>
}

// Data Classes
data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>
) {
    data class Main(
        val temp: Double,
        val temp_min: Double,
        val temp_max: Double
    )

    data class Weather(
        val description: String
    )
}

