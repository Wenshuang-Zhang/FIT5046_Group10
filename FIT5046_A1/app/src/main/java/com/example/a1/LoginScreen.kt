import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Button

import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.a1.R

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider


private lateinit var googleSignInClient: SignInClient
private lateinit var signInRequest: BeginSignInRequest

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) } // To handle login errors
    var passwordVisible by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    //val clientId = stringResource(id = R.string.default_web_client_id)


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
            contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp, start = 18.dp)
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

            //title
            //Spacer(modifier = Modifier.height(1.dp)) //
            Text(
                text = "Welcome to FitnessHub",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center, //
                style = MaterialTheme.typography.h4,
            )

            //email input
            Spacer(modifier = Modifier.height(30.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            //password input
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    androidx.compose.material3.IconButton(onClick = {
                        passwordVisible = !passwordVisible
                    }) {
                        androidx.compose.material3.Icon(image, "Toggle password visibility")
                    }
                }
            )

            if (loginError) {
                Text("Invalid email or password. Please try again.", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(26.dp))

            //sign in button
            Button(
                onClick = {
                    val auth1 = FirebaseAuth.getInstance()
                    auth1.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid
                                if (userId != null) {
                                    val db = FirebaseFirestore.getInstance()
                                    val docRef = db.collection("healthInfo").document(userId)
                                    docRef.get().addOnSuccessListener { document ->
                                        if (!document.exists()) {
                                            // Generate random sleep hours and steps
                                            val sleepHours = Random.nextInt(6, 11)  // Random number between 6 and 10
                                            val steps = Random.nextInt(2200, 11001) // Random number between 2200 and 11000

                                            // Create a new health info record
                                            val healthInfo = hashMapOf(
                                                "sleepHours" to sleepHours,
                                                "steps" to steps
                                            )

                                            // Set the new data in Firestore
                                            docRef.set(healthInfo).addOnSuccessListener {
                                                // Successfully created health info
                                            }.addOnFailureListener {
                                                // Handle failure
                                            }
                                        }
                                    }.addOnFailureListener {
                                        // Handle failure to get document
                                    }
                                }

                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                loginError = true
                            }
                        }
                },
                modifier = Modifier
                    .height(50.dp)
                    .width(180.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF776EE3))
            ) {
                Text(text = "Sign in", color = Color.White, fontSize = 16.sp)
            }


            //google sign in icon
            Spacer(modifier = Modifier.height(26.dp))
            Text(text = "Or continue with")

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(width = 60.dp, height = 60.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(20.dp))
                    .clickable {

                    }
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
        }
    }
}

fun firebaseAuthWithGoogle(idToken: String, auth: FirebaseAuth, navController: NavHostController) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    auth.signInWithCredential(credential).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            navController.navigate("YourHomeScreen")
        } else {
            // Handle the sign-in failure
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun LoginScreenPreview() {
//    LoginScreen(navController)
//}
