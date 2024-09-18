package pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
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
import com.example.app1.view.LandmarkViewModel
import com.example.app1.view.User
import com.example.app1.view.UsersViewModel

@Composable
fun UsersPage(
    viewModel: UsersViewModel = viewModel(),
    navController: NavController
) {
    val usersState = viewModel.users.collectAsState()
    val landmarkState = remember { mutableStateMapOf<String, List<Landmark>>() }
    val landmarkViewModel: LandmarkViewModel = viewModel()

    // Sort users by points in descending order
    val sortedUsers = usersState.value.sortedByDescending { it.points }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "Users List",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(sortedUsers) { user ->
                UserItem(
                    user = user,
                    isTopUser = sortedUsers.indexOf(user) < 3,
                    onFetchEvents = { userId ->
                        // Fetch events for the selected user
                        landmarkViewModel.filterLandmarksByUserId(userId) { events ->
                            landmarkState[userId] = events
                        }
                    },
                    navController = navController
                )
            }
            }

        Spacer(modifier = Modifier.height(24.dp))

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
    }
}

@Composable
fun UserItem(
    user: User,
    isTopUser: Boolean,
    onFetchEvents: (String) -> Unit,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color(0xFFF5F5F5), shape = CircleShape)
        ) {
            if (user.photoUrl != null) {
                AsyncImage(
                    model = user.photoUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "No Image",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = "${user.fullName}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isTopUser) Color.Yellow else Color.Red
            )
            Text(
                text = "Phone: ${user.phoneNumber}",
                fontSize = 14.sp,
                color = Color.White
            )
            Text(
                text = "Points: ${user.points}",
                fontSize = 14.sp,
                color = if (isTopUser) Color.Yellow else Color.White
            )

            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                val userId = user.id
                if (userId != null) {
                    navController.navigate("user_profile/$userId")
                } else {
                    Log.e("UserItem", "User ID is null")
                }
            }) {
                Text(text = "Show Profile")
            }
        }
    }
}

@Composable
fun LandmarksList(events: List<Landmark>) {
    Column(modifier = Modifier.padding(start = 16.dp)) {
        Text(
            text = "Events:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        for (event in events) {
            Text(
                text = event.eventName,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
