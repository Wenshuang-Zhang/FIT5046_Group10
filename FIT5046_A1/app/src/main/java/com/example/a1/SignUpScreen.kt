package com.example.a1

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons


import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController


@Composable
fun SignUpScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) } // 管理密码可见性的状态
    var triedToSubmit by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? ComponentActivity
    Box(
        modifier = Modifier
        .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE4E4FC),
                        Color(0xFFFAE8E1)
                    )
                )
        )
    ){
        //back icon
    Box(
        contentAlignment = Alignment.TopStart, // 将内容对齐到左上角
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 22.dp, start = 18.dp) // 在顶部和开始（左边）添加内边距
    ) {
        Icon(
            painter = painterResource(id = R.drawable.back),
            contentDescription = "Back Icon",
            tint = Color.Unspecified,
            modifier = Modifier
                .size(30.dp)
                .clickable {
                    navController.popBackStack()
                }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Sign up",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        //email input box
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = email.isNotEmpty() && !isValidEmail(email)
        )

        //password input box
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = password.isNotEmpty() && password.length < 8,
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(image, "Toggle password visibility")
                }
            }
        )

        //sign up button
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                if (isValidEmail(email) && password.length >= 8) {
                    Toast.makeText(context, "Successfully Sign Up!", Toast.LENGTH_LONG).show()

                    navController.navigate("addinfo") {
                        popUpTo("addinfo") { inclusive = true }
                    }
                } else {
                    Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6E14FF)),
            enabled = isValidEmail(email) && password.length >= 8
        ) {
            Text(text = "Sign up", color = Color.White)
        }
        Spacer(modifier = Modifier.height(24.dp))
        //TermsAndConditionsText()
    }
    }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{3,}"
    return email.matches(emailRegex.toRegex())
}



@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {

}