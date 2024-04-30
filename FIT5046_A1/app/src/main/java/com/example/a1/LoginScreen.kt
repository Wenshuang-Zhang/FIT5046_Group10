import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.material3.Button

import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import com.example.a1.R
import com.example.a1.SignUpScreen
import com.example.a1.showDatePicker



@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        //back icon
        Box(
            contentAlignment = Alignment.TopStart, // 将内容对齐到左上角
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp, start = 18.dp) // 在顶部和开始（左边）添加内边距
        ) {
            androidx.compose.material3.Icon(
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
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Spacer(modifier = Modifier.height(50.dp)) //
            Text(
                text = "Welcome to FitnessHub",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center, //
                style = MaterialTheme.typography.h4,

            )
            Spacer(modifier = Modifier.height(40.dp))
            androidx.compose.material3.OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))
            androidx.compose.material3.OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            //login button
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(30),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6E14FF))
            ) {
                Text(text = "Sign in", color = Color.White)
            }

            //google sign in icon
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Or continue with")

            Spacer(modifier = Modifier.height(10.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(width = 60.dp, height = 60.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(20.dp))
                    .clickable {  }
            ){
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.g),
                        contentDescription = "Google Login",
                        modifier = Modifier
                            .size(48.dp)
                    )
                }

            }

            //jump to Sign up
//            Spacer(modifier = Modifier.height(16.dp))
//
//            //sign up button
//            Text(
//                text = "Don't have account yet? "
//            )
//            Spacer(modifier = Modifier.height(10.dp))
//            Button(
//                onClick = { navController.navigate("signup") },
//                modifier = Modifier
//                    .width(200.dp)
//                    .height(48.dp),
//                shape = RoundedCornerShape(30),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF909EEA))
//            ) {
//                Text(text = "Sign up now", color = Color.White)
//            }
        }

    }
}

//@Preview(showBackground = true)
//@Composable
//fun LoginScreenPreview() {
//    LoginScreen(navController)
//}
