package com.example.itworkshopproject.screens.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.itworkshopproject.R
import kotlinx.coroutines.delay

@Composable
fun PremiumProfileScreen(navController: NavController, viewModel: ProfileViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val name by viewModel.name.collectAsState()
    val location by viewModel.location.collectAsState()
    val about by viewModel.about.collectAsState()
    val selectedAvatar by viewModel.selectedAvatar.collectAsState()
    val avatarUri by viewModel.avatarUri.collectAsState()

    var showThemeDialog by remember { mutableStateOf(false) }
    var showAvatarDialog by remember { mutableStateOf(false) }
    var showDeveloperDialog by remember { mutableStateOf(false) }
    var showEditPersonalDialog by remember { mutableStateOf(false) }
    var selectedInterests by remember { mutableStateOf(setOf<String>()) }

    val allInterests = listOf("🎮 Gaming", "🎨 Design", "💻 Coding", "🎧 Music", "📚 Reading", "🏃‍♂️ Fitness")

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        viewModel.updateAvatarUri(uri)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showThemeDialog = true }) {
                Icon(Icons.Default.Palette, contentDescription = "Change Theme")
            }
        },
        bottomBar = {
            Button(
                onClick = { showDeveloperDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
            ) {
                Text("Developed by", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
                .background(Color(0xFF1A1A2E))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color.Magenta, Color.Transparent)))
                ) {
                    if (avatarUri != null) {
                        AsyncImage(
                            model = avatarUri,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = selectedAvatar),
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Edit Avatar",
                    tint = Color.White,
                    modifier = Modifier
                        .offset((-8).dp, (-8).dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable { showAvatarDialog = true }
                        .padding(6.dp)
                )
            }

            // Display username below avatar
            Text(
                text = name, // Displaying the username here
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            // Personal Info Section
            Text(
                text = "Personal Information",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF512DA8), Color(0xFF673AB7))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                ProfileField("Name", name, viewModel::updateName)
                ProfileField("Location", location, viewModel::updateLocation)
                ProfileField("About Me", about, viewModel::updateAbout)
            }

            // Button to edit personal info
            Button(
                onClick = { showEditPersonalDialog = true },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Edit Personal Details", color = Color.White)
            }

            if (showEditPersonalDialog) {
                AlertDialog(
                    onDismissRequest = { showEditPersonalDialog = false },
                    title = { Text("Edit Personal Information", fontSize = 18.sp) },
                    text = {
                        Column {
                            ProfileField("Name", name, viewModel::updateName)
                            ProfileField("Location", location, viewModel::updateLocation)
                            ProfileField("About Me", about, viewModel::updateAbout)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showEditPersonalDialog = false }) {
                            Text("Save")
                        }
                    }
                )
            }

            // Interests Section
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Your Interests",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                allInterests.chunked(2).forEach { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        rowItems.forEach { interest ->
                            val isSelected = selectedInterests.contains(interest)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedInterests = if (isSelected) selectedInterests - interest else selectedInterests + interest
                                },
                                label = { Text(interest, fontSize = 16.sp) },
                                modifier = Modifier.padding(4.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF7C4DFF),
                                    containerColor = Color.DarkGray,
                                    labelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Inspirational Thoughts
            val inspirationalThoughts = listOf(
                "“The best way to predict the future is to create it.”",
                "“Happiness is not something ready made. It comes from your own actions.”",
                "“Success is not the key to happiness. Happiness is the key to success.”"
            )
            var currentThought by remember { mutableStateOf(inspirationalThoughts[0]) }
            var thoughtIndex by remember { mutableStateOf(0) }

            LaunchedEffect(currentThought) {
                delay(5000)
                thoughtIndex = (thoughtIndex + 1) % inspirationalThoughts.size
                currentThought = inspirationalThoughts[thoughtIndex]
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        brush = Brush.horizontalGradient(listOf(Color.Magenta, Color.Cyan)),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = currentThought,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }

    // Developer Info Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Select Theme", fontSize = 20.sp) },
            text = { Text("Coming soon: Mood-based themes 🎫", fontSize = 16.sp) },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("OK", fontSize = 16.sp)
                }
            }
        )
    }

    // Developer Info Dialog
    if (showDeveloperDialog) {
        AlertDialog(
            onDismissRequest = { showDeveloperDialog = false },
            title = {
                Text(
                    "Developer Info",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFF7C4DFF), Color(0xFF512DA8)))
                        )
                        .fillMaxWidth()
                        .padding(12.dp),
                    color = Color.White
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .heightIn(min = 300.dp, max = 600.dp)
                        .padding(8.dp)
                ) {
                    items(
                        listOf(
                            Developer("SUPRIYA", "2301CS73", "Backend Developer, ML Engineer", R.drawable.img1),
                            Developer("K HARI PRASAD", "2310AI05", "Project Manager, Frontend Developer", R.drawable.img2),
                            Developer("VIVEK KATURI K", "2301AI36", "UI/UX Designer, Frontend Developer", R.drawable.img3),
                            Developer("HEMANTH KATARIYA", "2301CS22", "Frontend Developer, Ideologist", R.drawable.img4),
                            Developer("J KAVYA", "2301AI11", "Backend Developer, Firebase Authentication", R.drawable.img5),
                            Developer("BLESSY NIRIKSHINA", "2301AI06", "Backend Developer", R.drawable.img6)
                        )
                    ) { developer ->
                        DeveloperInfo(developer)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDeveloperDialog = false }) {
                    Text("Close", fontSize = 16.sp)
                }
            },
            containerColor = Color(0xFF1A1A2E)
        )
    }
}

@Composable
fun DeveloperInfo(developer: Developer) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .background(Color(0xFF2C2C3E), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = developer.imageRes),
                contentDescription = "Developer Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(developer.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Roll No: ${developer.rollNo}", color = Color(0xFFCCCCCC), fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(developer.role, color = Color(0xFFBB86FC), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

data class Developer(
    val name: String,
    val rollNo: String,
    val role: String,
    val imageRes: Int
)

@Composable
fun AvatarChoiceOption(resId: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(Color.Gray.copy(alpha = 0.2f))
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = "Avatar Option",
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Text(label, color = Color.White, fontSize = 16.sp)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(Color(0xFF512DA8), RoundedCornerShape(8.dp))
                .padding(12.dp)
        )
    }
}
