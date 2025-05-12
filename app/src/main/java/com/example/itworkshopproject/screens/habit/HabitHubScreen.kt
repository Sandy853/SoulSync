package com.example.itworkshopproject.screens.habit

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.itworkshopproject.model.Habit
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitHubScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var habitText by remember { mutableStateOf("") }
    var isGoodHabit by remember { mutableStateOf(true) }
    var duration by remember { mutableStateOf("7 Days") }
    var habits by remember { mutableStateOf<List<Habit>>(emptyList()) }
    val motivationalQuotes = listOf(
        "Stay consistent, stay strong.",
        "Small steps every day build big results.",
        "Today's efforts create tomorrow’s success.",
        "Build habits that build you."
    )
    val todayQuote = motivationalQuotes.random()

    LaunchedEffect(Unit) {
        habits = HabitDataStore.loadHabits(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Habit Hub", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFB2EBF2), Color(0xFFFFF9C4), Color(0xFFFFCCBC))
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(todayQuote, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF512DA8))

            OutlinedTextField(
                value = habitText,
                onValueChange = { habitText = it },
                label = { Text("Enter your habit") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Type:", modifier = Modifier.padding(end = 8.dp), fontWeight = FontWeight.SemiBold)
                FilterChip(selected = isGoodHabit, onClick = { isGoodHabit = true }, label = { Text("Good") })
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(selected = !isGoodHabit, onClick = { isGoodHabit = false }, label = { Text("Avoid") })
            }

            DurationDropdownMenu(selectedValue = duration, onValueSelected = { duration = it })

            Button(
                onClick = {
                    if (habitText.isNotBlank()) {
                        val newHabit = Habit(
                            title = habitText,
                            isGood = isGoodHabit,
                            duration = duration,
                            createdDate = System.currentTimeMillis()
                        )
                        habits = habits + newHabit
                        scope.launch {
                            HabitDataStore.saveHabits(context, habits)
                            Toast.makeText(context, "Habit Saved", Toast.LENGTH_SHORT).show()
                        }
                        habitText = ""
                    } else {
                        Toast.makeText(context, "Please enter a habit", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Habit", color = Color.White)
            }

            Divider(thickness = 1.dp, color = Color.Gray)

            Text("Active Habits", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Column {
                habits.filter { !it.isCompletedToday() }.forEachIndexed { index, habit ->
                    AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                        HabitRow(
                            habit = habit,
                            onComplete = {
                                val updated = habit.copy(
                                    currentStreak = habit.currentStreak + 1,
                                    lastCompleted = System.currentTimeMillis()
                                )
                                habits = habits.toMutableList().apply { set(index, updated) }
                                scope.launch { HabitDataStore.saveHabits(context, habits) }
                            },
                            onDelete = {
                                habits = habits.toMutableList().apply { removeAt(index) }
                                scope.launch { HabitDataStore.saveHabits(context, habits) }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Completed Today", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Column {
                habits.filter { it.isCompletedToday() }.forEach { habit ->
                    HabitRowCompleted(habit = habit)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurationDropdownMenu(selectedValue: String, onValueSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("7 Days", "14 Days", "30 Days", "Daily")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text("Duration") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onValueSelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun HabitRow(habit: Habit, onComplete: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        if (habit.isGood) Color(0xFF81D4FA) else Color(0xFFFF8A65),
                        Color.White
                    )
                )
            )
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(habit.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Streak: ${habit.currentStreak}", fontSize = 12.sp, color = Color.Gray)
            }
            Row {
                IconButton(onClick = onComplete, enabled = !habit.isCompletedToday()) {
                    Icon(Icons.Default.Check, contentDescription = "Complete", tint = Color(0xFF388E3C))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F))
                }
            }
        }
    }
}

@Composable
fun HabitRowCompleted(habit: Habit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE1BEE7))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(habit.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("✅ Done today!", fontSize = 12.sp, color = Color.DarkGray)
            }
        }
    }
}

fun Habit.isCompletedToday(): Boolean {
    val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    val lastDone = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(lastCompleted))
    return today == lastDone
}
