package com.example.a1

import LoginScreen

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.a1.ui.theme.A1Theme

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem

import androidx.compose.material3.Icon

import androidx.compose.material3.contentColorFor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : ComponentActivity() {
    val db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            A1Theme {
                val navController = rememberNavController()
                val mealManager = remember { MealManager() }

                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                Scaffold(
                    bottomBar = {
                        if (currentRoute != "login" && currentRoute != "signup" && currentRoute != "welcome" && currentRoute != "addinfo") {
                            BottomNavigationBar(navController, height = 65.dp)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "welcome",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("welcome") { WelcomeScreen(navController) }
                        composable("login") { LoginScreen(navController) }
                        composable("signup") { SignUpScreen(navController) }
                        composable("addinfo") { AddInfoScreen(navController) }

                        composable("home") { HomeScreen(navController) }
                        composable("report") { ReportScreen(navController)}
                        composable("activity") { }
                        composable("profile") { ProfileScreen(navController) }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, height: Dp) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        BottomNavigation(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            backgroundColor = Color.White,
            contentColor = contentColorFor(backgroundColor = Color.White)
        ) {
            //nav bar items and click logic
            //home page
            BottomNavigationItem(
                modifier = Modifier
                    .padding(top = 6.dp),
                icon = {
                    if (currentRoute == "home") {
                        Icon(
                            painterResource(id = R.drawable.home2), // 选中时的图标
                            contentDescription = "Home2",
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF1B225C)
                        )
                    }else {
                        Icon(
                            painterResource(id = R.drawable.home), // 未选中时的图标
                            contentDescription = "Home",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    }
                },
                label = { Text("Home", fontSize = 12.sp) },
                selected = navController.currentDestination?.route == "home",
                onClick = {
                    navController.navigate("home")
                }
            )
            //report page
            BottomNavigationItem(
                modifier = Modifier
                    .padding(top = 6.dp),
                icon = {
                    if (currentRoute == "report") {
                        Icon(
                            painterResource(id = R.drawable.report2), // 选中时的图标
                            contentDescription = "History2",
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF1B225C)
                        )
                    } else {
                        Icon(
                            painterResource(id = R.drawable.report), // 未选中时的图标
                            contentDescription = "History",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    }
                },
                label = { Text("Report", fontSize = 12.sp) },
                selected = navController.currentDestination?.route == "report",
                onClick = {
                    navController.navigate("report")
                }
            )
            //history page
            BottomNavigationItem(
                modifier = Modifier
                    .padding(top = 6.dp),
                icon = {
                    if (currentRoute == "activity") {
                        Icon(
                            painterResource(id = R.drawable.activity2), // 选中时的图标
                            contentDescription = "History2",
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF1B225C)
                        )
                    } else {
                        Icon(
                            painterResource(id = R.drawable.activity), // 未选中时的图标
                            contentDescription = "History",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    }
                },
                label = { Text("Activity", fontSize = 12.sp) },
                selected = navController.currentDestination?.route == "activity",
                onClick = {
                    navController.navigate("activity")
                }
            )
            //account page
            BottomNavigationItem(
                modifier = Modifier
                    .padding(top = 6.dp),
                icon = {
                    if (currentRoute == "profile") {
                        Icon(
                            painterResource(id = R.drawable.account2), // 选中时的图标
                            contentDescription = "Profile2",
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF1B225C)
                        )
                    } else {
                        Icon(
                            painterResource(id = R.drawable.account), // 未选中时的图标
                            contentDescription = "Profile",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    }
                },
                label = { Text("Profile", fontSize = 12.sp) },
                selected = navController.currentDestination?.route == "profile",
                onClick = {
                    navController.navigate("profile")
                }

            )

        }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    A1Theme {
        Greeting("Android")
    }
}