package pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app1.data.Landmark
import com.example.app1.view.LandmarkViewModel
import com.example.app1.view.MarkerViewModel
import com.example.app1.data.Resource
import com.example.app1.view.User
import com.example.app1.view.UsersViewModel
import com.google.firebase.firestore.GeoPoint


@Composable
fun LandmarkFilterDialog(
    onDismiss: () -> Unit,
    centerPoint: GeoPoint, // Added center point parameter
    landmarkViewModel: LandmarkViewModel = viewModel(),
    usersViewModel: UsersViewModel = viewModel()
) {
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var isUserDropdownExpanded by remember { mutableStateOf(false) }
    var isEventNameDropdownExpanded by remember { mutableStateOf(false) }
    var chooseUser by remember { mutableStateOf<User?>(null) }
    var chooseEventName by remember { mutableStateOf("Select Landmark Name") }
    val usersState by usersViewModel.users.collectAsState()
    var isCrowdLevelDropdownExpanded by remember { mutableStateOf(false) }
    var selectedCrowdLevel by remember { mutableStateOf(0) }
    var category by remember { mutableStateOf("Select Category") }
    var radius by remember { mutableStateOf(1f) } // Initialize with a default value

    // Inspecting eventsResource and creating eventsState
    // Prikupite podatke o događajima
    val landmarksResource by landmarkViewModel.landmark.collectAsState()
    val landmarksState = when (landmarksResource) {
        is Resource.Success -> (landmarksResource as Resource.Success<List<Landmark>>).result // Uzimamo listu događaja
        is Resource.Failure -> emptyList() // Ili neka druga logika za greške
        is Resource.loading -> emptyList() // U slučaju učitavanja
    }

    val markerViewModel: MarkerViewModel = viewModel()

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Select Landmark Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                // Dropdown for categories
                TextButton(onClick = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded }) {
                    Text(category)
                }

                DropdownMenu(
                    expanded = isCategoryDropdownExpanded,
                    onDismissRequest = { isCategoryDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            category = "Crkva"
                            isCategoryDropdownExpanded = false
                        },
                        text = { Text("Crkva") }
                    )
                    DropdownMenuItem(
                        onClick = {
                            category = "Spomenik"
                            isCategoryDropdownExpanded = false
                        },
                        text = { Text("Spomenik") }
                    )
                    DropdownMenuItem(
                        onClick = {
                            category = "Arheolosko nalaziste"
                            isCategoryDropdownExpanded = false
                        },
                        text = { Text("Arheolosko nalaziste") }
                    )
                    DropdownMenuItem(
                        onClick = {
                            category = "Park"
                            isCategoryDropdownExpanded = false
                        },
                        text = { Text("Park") }
                    )
                }

                // Spacer
                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown for event names (landmarks)
                TextButton(onClick = { isEventNameDropdownExpanded = !isEventNameDropdownExpanded }) {
                    Text(chooseEventName)
                }

                DropdownMenu(
                    expanded = isEventNameDropdownExpanded,
                    onDismissRequest = { isEventNameDropdownExpanded = false }
                ) {
                    if (landmarksState.isEmpty()) {
                        DropdownMenuItem(
                            onClick = { /* Do nothing */ },
                            text = { Text("No landmarks available") }
                        )
                    } else {
                        landmarksState.forEach { event: Landmark ->
                            if (event.eventName.isNotBlank()) { // Provera da li eventName nije prazan
                                DropdownMenuItem(
                                    onClick = {
                                        chooseEventName = event.eventName
                                        isEventNameDropdownExpanded = false
                                    },
                                    text = { Text(event.eventName) }
                                )
                            }
                        }
                    }
                }

                // Spacer
                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown for users
                TextButton(onClick = { isUserDropdownExpanded = !isUserDropdownExpanded }) {
                    Text(chooseUser?.let { "${it.fullName} " } ?: "Select User")
                }

                DropdownMenu(
                    expanded = isUserDropdownExpanded,
                    onDismissRequest = { isUserDropdownExpanded = false }
                ) {
                    usersState.forEach { user ->
                        DropdownMenuItem(
                            onClick = {
                                chooseUser = user
                                isUserDropdownExpanded = false
                            },
                            text = { Text("${user.fullName}") }
                        )
                    }
                }

                // Spacer
                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown for crowd level
                TextButton(onClick = { isCrowdLevelDropdownExpanded = !isCrowdLevelDropdownExpanded }) {
                    Text("Crowd Level: $selectedCrowdLevel")
                }

                DropdownMenu(
                    expanded = isCrowdLevelDropdownExpanded,
                    onDismissRequest = { isCrowdLevelDropdownExpanded = false }
                ) {
                    (1..5).forEach { level ->
                        DropdownMenuItem(
                            onClick = {
                                selectedCrowdLevel = level
                                isCrowdLevelDropdownExpanded = false
                            },
                            text = { Text("$level") }
                        )
                    }
                }
                // Spacer
                Spacer(modifier = Modifier.height(16.dp))

                // Slider for radius
                Text(text = "Radius: ${radius.toInt()} km")
                Slider(
                    value = radius,
                    onValueChange = { newRadius -> radius = newRadius },
                    valueRange = 1f..50f, // Example range for radius
                    steps = 49 // Number of steps between min and max value
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (chooseUser != null) {
                    markerViewModel.filterMarkersByUserName(chooseUser!!.fullName) { filteredMarkers ->
                        // Handle filtered markers
                    }
                } else {
                    markerViewModel.filterMarkers(category, chooseEventName, selectedCrowdLevel,radius, centerPoint)
                }
                onDismiss()
            }) {
                Text("Filter")
            }
        }
    )
}
