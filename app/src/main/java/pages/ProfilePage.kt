package pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.app1.data.Landmark
import com.example.app1.view.AuthViewModel
import com.example.app1.view.LandmarkViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun UserProfilePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    userId: String?
) {
    var fullName by remember { mutableStateOf<String?>(null) }
    var phoneNumber by remember { mutableStateOf<String?>(null) }
    var photoUrl by remember { mutableStateOf<String?>(null) }
    var points by remember { mutableStateOf<Int?>(null) }

    val landmarkViewModel: LandmarkViewModel = viewModel()
    var landmarks by remember { mutableStateOf<List<Landmark>>(emptyList()) }

    LaunchedEffect(userId) {
        userId?.let {
            val userDocument = FirebaseFirestore.getInstance().collection("users").document(it)
            userDocument.get().addOnSuccessListener { document ->
                if (document != null) {
                    fullName = document.getString("fullName")
                    phoneNumber = document.getString("phoneNumber")
                    photoUrl = document.getString("photoUrl")
                    points = document.getLong("points")?.toInt()
                }
            }

            landmarkViewModel.filterLandmarksByUserId(it) { userLandmarks ->
                landmarks = userLandmarks
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "User Profile",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(150.dp)
                .background(Color.Gray, shape = CircleShape)
        ) {
            if (photoUrl != null) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .align(Alignment.Center)
                )
            } else {
                Text(
                    text = "No Image",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Name: ${fullName ?: "Loading..."}",
            fontSize = 24.sp,
            color = Color.Red
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Phone: ${phoneNumber ?: "Loading..."}",
            fontSize = 24.sp,
            color = Color.Red
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Points: ${points ?: "Loading..."}",
            fontSize = 24.sp,
            color = Color.Red
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "User's Landmarks:",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2589a0)
        )

        Spacer(modifier = Modifier.height(16.dp))

        landmarks.forEach { landmark ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                AsyncImage(
                    model = landmark.mainImage,
                    contentDescription = "Landmark Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = landmark.eventName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    landmark.description?.let {
                        Text(
                            text = it,
                            fontSize = 16.sp,
                            color = Color.LightGray,
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("home") },
            shape = CircleShape,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(180.dp)
        ) {
            Text(text = "Back", fontSize = 18.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                authViewModel.signout()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            shape = CircleShape,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(180.dp)
        ) {
            Text(text = "Log Out", fontSize = 18.sp, color = Color.White)
        }
    }
}
