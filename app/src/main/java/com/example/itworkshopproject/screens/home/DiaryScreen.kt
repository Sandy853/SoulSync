package com.example.itworkshopproject.screens.home

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.itworkshopproject.screens.home.components.DiaryStore
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(navController: NavController) {
    // Berry & Cream Color Palette
    val primaryColor = Color(0xFF6D214F)       // Deep berry
    val secondaryColor = Color(0xFFB33771)     // Raspberry
    val accentColor = Color(0xFFFC427B)        // Pink
    val backgroundColor = Color(0xFFF8EFBA)    // Pale yellow
    val cardColor = Color(0xFFFFF9E7)          // Cream
    val textColor = Color(0xFF182C61)          // Navy
    val lightTextColor = Color(0xFF6D214F)     // Berry

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.time) }
    var entry by remember { mutableStateOf("") }
    var emotion by remember { mutableStateOf("Happy") }

    val emotions = listOf("Happy", "Sad", "Anxious", "Excited", "Grateful", "Angry")
    var emotionDropdownExpanded by remember { mutableStateOf(false) }

    // Load entry when date changes
    LaunchedEffect(selectedDate) {
        entry = DiaryStore.getEntry(context, DiaryStore.formatDate(selectedDate))
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Digital Diary",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    DiaryStore.saveEntry(
                        context,
                        DiaryStore.formatDate(selectedDate),
                        entry
                    )
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                },
                containerColor = accentColor
            ) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = "Save",
                    tint = Color.White
                )
            }
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Date Picker Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = primaryColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        DiaryStore.formatDate(selectedDate),
                        color = textColor,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = { datePickerDialog.show() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = primaryColor
                        )
                    ) {
                        Text("Change Date")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Emotion Selector Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "How are you feeling today?",
                        color = textColor,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box {
                        OutlinedButton(
                            onClick = { emotionDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = cardColor,
                                contentColor = primaryColor
                            )
                        ) {
                            Text(emotion)
                        }
                        DropdownMenu(
                            expanded = emotionDropdownExpanded,
                            onDismissRequest = { emotionDropdownExpanded = false }
                        ) {
                            emotions.forEach { mood ->
                                DropdownMenuItem(
                                    text = { Text(mood) },
                                    onClick = {
                                        emotion = mood
                                        emotionDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Journal Entry Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Your Thoughts",
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = entry,
                        onValueChange = { entry = it },
                        label = { Text("Write here...", color = lightTextColor) },
                        placeholder = { Text("Today was...", color = lightTextColor) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = secondaryColor,
                            cursorColor = primaryColor,
                            focusedLabelColor = primaryColor
                        ),
                        maxLines = 10
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Journaling helps organize thoughts and emotions",
                        color = lightTextColor,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}