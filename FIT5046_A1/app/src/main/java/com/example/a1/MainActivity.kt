package com.example.a1

import LoginScreen
import LoginScreenPreview
import android.media.Image
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
import androidx.navigation.NavHostController
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
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.navigation.compose.currentBackStackEntryAsState


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            A1Theme {
                val navController = rememberNavController()
                val mealManager = remember { MealManager() }
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController, height = 65.dp) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { HomeScreen(navController) }
                        composable("history") { LoginScreen() }
                        composable("account") { ReportScreen(navController) }
                        composable("reportScreen") { ReportScreen(navController)}
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
            BottomNavigationItem(

                modifier = Modifier
                    .padding(top = 6.dp),
                icon = {
                    if (currentRoute != "history" && currentRoute != "account") {
                        Icon(
                            painterResource(id = R.drawable.home2), // 选中时的图标
                            contentDescription = "Home2",
                            modifier = Modifier.size(24.dp)
                        )
                    }else {
                        Icon(
                            painterResource(id = R.drawable.home), // 未选中时的图标
                            contentDescription = "Home",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = { Text("Home", fontSize = 12.sp) },
                selected = navController.currentDestination?.route == "home",
                onClick = {
                    navController.navigate("home")
                }
            )
            BottomNavigationItem(
                modifier = Modifier
                    .padding(top = 6.dp),
                icon = {
                    if (currentRoute == "history") {
                        Icon(
                            painterResource(id = R.drawable.history2), // 选中时的图标
                            contentDescription = "History2",
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            painterResource(id = R.drawable.history), // 未选中时的图标
                            contentDescription = "History",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = { Text("History", fontSize = 12.sp) },
                selected = navController.currentDestination?.route == "history",
                onClick = {
                    navController.navigate("history")
                }
            )

            BottomNavigationItem(
                modifier = Modifier
                    .padding(top = 6.dp),
                icon = {
                    if (currentRoute == "account") {
                        Icon(
                            painterResource(id = R.drawable.account2), // 选中时的图标
                            contentDescription = "Account2",
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            painterResource(id = R.drawable.account), // 未选中时的图标
                            contentDescription = "Account",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = { Text("Account", fontSize = 12.sp) },
                selected = navController.currentDestination?.route == "account",
                onClick = {
                    navController.navigate("account")
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