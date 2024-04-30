package com.example.a1

import androidx.compose.material.ButtonDefaults
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun WelcomeScreen(navController: NavController) {

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        //background png
        Image(
            painter = painterResource(id = R.drawable.welcome),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // 根据需要调整缩放方式
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //title fitness hub
            Spacer(modifier = Modifier.height(80.dp))
            Text(
                text = "FitnessHub",
                fontSize = 46.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {

                //login button
                Spacer(modifier = Modifier.height(450.dp))
                Button(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier
                        .height(50.dp)
                        .width(180.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                ) {
                    Text("Login", color = Color(0xFF776EE3), modifier = Modifier.padding(6.dp), fontSize = 16.sp)
                }

                //sign up button
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("signup") },
                    modifier = Modifier
                        .height(50.dp)
                        .width(180.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                ) {
                    Text("Sign Up", color = Color(0xFF776EE3), modifier = Modifier.padding(6.dp), fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}