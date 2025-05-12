package com.example.itworkshopproject.screens.home

import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.itworkshopproject.model.Mood
import com.example.itworkshopproject.navigation.Routes
import com.example.itworkshopproject.screens.home.components.MoodItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(username: String, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedMood by remember { mutableStateOf<Mood?>(null) }
    var showQuoteDialog by remember { mutableStateOf(false) }
    var currentQuote by remember { mutableStateOf("") }
    var showStudentToolsDialog by remember { mutableStateOf(false) }

    val moodQuotes = mapOf(
        Mood.HAPPY to listOf("Spread your light!", "Happiness looks good on you."),
        Mood.SAD to listOf("It's okay to rest.", "You're stronger than you feel."),
        Mood.ANGRY to listOf("Breathe in calm. Let it go.", "Peace starts with you."),
        Mood.CALM to listOf("Stay grounded. Stay glowing.", "Your calm is power."),
        Mood.ANXIOUS to listOf("You're safe. Take deep breaths.", "Worry less, live more."),
        Mood.EXCITED to listOf("Let's turn passion into progress!", "Today is YOUR day!"),
        Mood.NEUTRAL to listOf("Even ordinary days matter.", "Stay steady. You're doing great."),
        Mood.TIRED to listOf("Rest is productive too.", "Slow down. Refuel your soul.")
    )

    val backgroundGradient = when (selectedMood) {
        Mood.HAPPY -> listOf(Color(0xFFFFF176), Color(0xFFFFF9C4))
        Mood.SAD -> listOf(Color(0xFF81D4FA), Color(0xFFB3E5FC))
        Mood.ANGRY -> listOf(Color(0xFFEF5350), Color(0xFFFFCDD2))
        Mood.EXCITED -> listOf(Color(0xFFBA68C8), Color(0xFFE1BEE7))
        Mood.CALM -> listOf(Color(0xFFA5D6A7), Color(0xFFC8E6C9))
        Mood.ANXIOUS -> listOf(Color(0xFF90CAF9), Color(0xFFBBDEFB))
        Mood.TIRED -> listOf(Color(0xFFD1C4E9), Color(0xFFEDE7F6))
        Mood.NEUTRAL -> listOf(Color(0xFFE0E0E0), Color(0xFFFAFAFA))
        else -> listOf(Color.White, Color.White)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Welcome back,", style = MaterialTheme.typography.bodyMedium)
                        Text(username, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.Profile.route) }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundGradient.first())
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(backgroundGradient))
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Adjusted MoodSelectorCard with reduced height
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp) // Reduced height
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE9EFFF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("How are you feeling today?", fontSize = 18.sp, color = Color(0xFF222244))
                        Spacer(modifier = Modifier.height(8.dp)) // Reduced spacing

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp), // Reduced spacing
                            horizontalArrangement = Arrangement.spacedBy(8.dp) // Reduced spacing
                        ) {
                            items(Mood.values()) { mood ->
                                MoodItem(
                                    mood = mood,
                                    isSelected = selectedMood?.label == mood.label,
                                    onSelected = { selectedMood = mood }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp)) // Reduced spacing

                        Button(
                            onClick = {
                                selectedMood?.let { mood ->
                                    scope.launch {
                                        val allowed = MoodTimeStore.canSelectMood(context)
                                        if (allowed) {
                                            MoodDataStore.incrementMoodCount(context, mood)
                                            currentQuote = moodQuotes[mood]?.random() ?: ""
                                            showQuoteDialog = true
                                        } else {
                                            Toast.makeText(context, "Limit reached!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } ?: Toast.makeText(context, "Please select a mood!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save My Mood")
                        }

                        Spacer(modifier = Modifier.height(8.dp)) // Reduced spacing

                        OutlinedButton(
                            onClick = { navController.navigate(Routes.MoodAnalysis.route) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(Icons.Default.ShowChart, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("View Mood Analysis")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                DiaryAndMemoryCards(navController)

                Spacer(modifier = Modifier.height(12.dp))

                HabitHubCard(navController)

                Spacer(modifier = Modifier.height(12.dp))

                // New Student Tools Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .clickable { showStudentToolsDialog = true },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF3949AB), modifier = Modifier.size(30.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Student Tools", fontSize = 16.sp, color = Color(0xFF283593), fontWeight = FontWeight.Bold)
                            Text("Academic productivity tools", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }
            }

            if (showQuoteDialog) {
                AlertDialog(
                    onDismissRequest = { showQuoteDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showQuoteDialog = false }) {
                            Text("Close")
                        }
                    },
                    title = { Text("Your Mood Companion") },
                    text = {
                        Text(text = currentQuote, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
            }

            if (showStudentToolsDialog) {
                AlertDialog(
                    onDismissRequest = { showStudentToolsDialog = false },
                    modifier = Modifier.fillMaxWidth(0.9f),
                    title = {
                        Text(
                            "Student Tools",
                            color = Color(0xFF283593),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Digital Notes
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .clickable {
                                        showStudentToolsDialog = false
                                        navController.navigate(Routes.StudentNotes.route)
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Note, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.size(30.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Digital Notes", fontSize = 16.sp, color = Color(0xFF0D47A1), fontWeight = FontWeight.Bold)
                                        Text("Take and organize your notes", fontSize = 13.sp, color = Color.Gray)
                                    }
                                }
                            }

                            // 🔁 New Timetable Scheduler
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .clickable {
                                        showStudentToolsDialog = false
                                        navController.navigate(Routes.TimeTableScheduler.route)
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF1B5E20), modifier = Modifier.size(30.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Timetable Scheduler", fontSize = 16.sp, color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
                                        Text("Plan your study routine", fontSize = 13.sp, color = Color.Gray)
                                    }
                                }
                            }

                            // Remaining placeholder box
                            // Deadline Dominator - Last Functional Slot
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .clickable {
                                        showStudentToolsDialog = false
                                        navController.navigate(Routes.DeadlineDominator.route)
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFF6A1B9A), modifier = Modifier.size(30.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Deadline Dominator", fontSize = 16.sp, color = Color(0xFF4A148C), fontWeight = FontWeight.Bold)
                                        Text("Manage tasks & stay on track", fontSize = 13.sp, color = Color.Gray)
                                    }
                                }
                            }

                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { showStudentToolsDialog = false },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF283593))
                        ) {
                            Text("Close")
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
            }

        }
    }
}

@Composable
fun DiaryAndMemoryCards(navController: NavController) {
    val cardHeight = 90.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clickable { navController.navigate(Routes.Diary.route) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF558B2F), modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Digital Diary", fontSize = 16.sp, color = Color(0xFF33691E), fontWeight = FontWeight.Bold)
                Text("Write & track your feelings", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clickable { navController.navigate(Routes.MemoryCapsule.route) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF6F00), modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Memory Capsule", fontSize = 16.sp, color = Color(0xFFEF6C00), fontWeight = FontWeight.Bold)
                Text("Store emotional memories", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun HabitHubCard(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { navController.navigate(Routes.HabitHub.route) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Color(0xFF1B5E20), modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Habit Builder", fontSize = 16.sp, color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
                Text("Create & track positive habits", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}