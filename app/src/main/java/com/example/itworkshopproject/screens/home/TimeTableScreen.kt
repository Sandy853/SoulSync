package com.example.itworkshopproject.screens.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import androidx.compose.runtime.LaunchedEffect


data class TimeSlot(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subtitle: String = "",
    val startTime: String,
    val endTime: String,
    val deadline: String = "",
    val examDate: Date? = null,
    var isDone: Boolean = false,
    val color: Color = getNextColor()
)

// List of attractive colors to cycle through
val attractiveColors = listOf(
    Color(0xFF6A11CB),  // Purple
    Color(0xFF2575FC),  // Blue
    Color(0xFF4ECDC4),  // Teal
    Color(0xFF06D6A0),  // Green
    Color(0xFFFFD166),  // Yellow
    Color(0xFFFF6B6B),  // Red
    Color(0xFFEF476F)   // Pink
)

private var colorIndex = 0

fun getNextColor(): Color {
    val color = attractiveColors[colorIndex % attractiveColors.size]
    colorIndex++
    return color
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeTableScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val timeFormat = remember { SimpleDateFormat("HH:mm") }
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy") }
    val dateTimeFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm") }

    // Current time state that updates every minute
    val currentTime by produceState(initialValue = Date()) {
        val timer = kotlin.concurrent.timer(period = 60000) { // Update every minute
            value = Date()
        }
        awaitDispose { timer.cancel() }
    }

    // Task states
    var showAddDialog by remember { mutableStateOf(false) }
    var showExamDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TimeSlot?>(null) }
    var newTitle by remember { mutableStateOf("") }
    var newSubtitle by remember { mutableStateOf("") }
    var newStartTime by remember { mutableStateOf("") }
    var newEndTime by remember { mutableStateOf("") }
    var newDeadline by remember { mutableStateOf("") }
    var examDate by remember { mutableStateOf<Date?>(null) }

    // Load saved tasks
    var timeTable by remember { mutableStateOf<List<TimeSlot>>(emptyList()) }

    LaunchedEffect(Unit) {
        timeTable = StoreTimeTable.loadTimeSlots(context)
    }

    val upcomingExams by derivedStateOf {
        timeTable.filter { it.examDate != null && it.examDate.after(currentTime) }
    }

    // Calculate countdowns for each exam
    val examCountdowns by derivedStateOf {
        upcomingExams.associate { exam ->
            val diff = exam.examDate!!.time - currentTime.time
            val days = diff / (1000 * 60 * 60 * 24)
            val hours = (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
            val minutes = (diff % (1000 * 60 * 60)) / (1000 * 60)
            exam to "${days}d ${hours}h ${minutes}m"
        }
    }

    // Date pickers
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, day)
            }
            examDate = calendar.time
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    // Handle task operations
    fun deleteTask(taskId: String) {
        timeTable = timeTable.filter { it.id != taskId }
        scope.launch { StoreTimeTable.saveTimeSlots(context, timeTable) }
    }

    fun saveTask(task: TimeSlot) {
        timeTable = if (editingTask != null) {
            timeTable.map { if (it.id == editingTask!!.id) task else it }
        } else {
            timeTable + task
        }
        scope.launch { StoreTimeTable.saveTimeSlots(context, timeTable) }
    }

    fun markTaskDone(taskId: String) {
        timeTable = timeTable.map {
            if (it.id == taskId) it.copy(isDone = true) else it
        }
        scope.launch { StoreTimeTable.saveTimeSlots(context, timeTable) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text("TIMETABLE PRO", fontWeight = FontWeight.Black)
                        Text(dateFormat.format(currentTime), style = MaterialTheme.typography.bodySmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    if (upcomingExams.isNotEmpty()) {
                        IconButton(onClick = { showExamDialog = true }) {
                            BadgedBox(badge = { Badge { Text(upcomingExams.size.toString()) } }) {
                                Icon(Icons.Default.Timer, contentDescription = "Exams")
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingTask = null
                    newTitle = ""
                    newSubtitle = ""
                    newStartTime = ""
                    newEndTime = ""
                    newDeadline = ""
                    examDate = null
                    showAddDialog = true
                },
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Current time display
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
            ) {
                Text(
                    text = timeFormat.format(currentTime),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Tasks List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp)
            ) {
                if (timeTable.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Add your first task!",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                items(timeTable.filter { !it.isDone }) { slot ->
                    TaskCard(
                        slot = slot,
                        onEditClicked = {
                            editingTask = slot
                            newTitle = slot.title
                            newSubtitle = slot.subtitle
                            newStartTime = slot.startTime
                            newEndTime = slot.endTime
                            newDeadline = slot.deadline
                            examDate = slot.examDate
                            showAddDialog = true
                        },
                        onDoneClicked = { markTaskDone(slot.id) },
                        onDeleteClicked = { deleteTask(slot.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    // Exam Countdown Dialog
    if (showExamDialog) {
        AlertDialog(
            onDismissRequest = { showExamDialog = false },
            title = { Text("Upcoming Exams") },
            text = {
                Column {
                    upcomingExams.forEach { exam ->
                        val countdown = examCountdowns[exam] ?: ""
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                exam.title,
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                countdown,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            "On ${dateTimeFormat.format(exam.examDate!!)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Divider()
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showExamDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    // Add/Edit Task Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(if (editingTask == null) "Add New Task" else "Edit Task") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Task Title*") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newSubtitle,
                        onValueChange = { newSubtitle = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            value = newStartTime,
                            onValueChange = { newStartTime = it },
                            label = { Text("Start Time*") },
                            modifier = Modifier.weight(1f),
                            leadingIcon = { Icon(Icons.Default.AccessTime, null) }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = newEndTime,
                            onValueChange = { newEndTime = it },
                            label = { Text("End Time*") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newDeadline,
                        onValueChange = { newDeadline = it },
                        label = { Text("Deadline") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Event, null) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { datePickerDialog.show() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            if (examDate == null) "Set Exam Date"
                            else "Exam: ${dateFormat.format(examDate!!)}"
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank() && newStartTime.isNotBlank() && newEndTime.isNotBlank()) {
                            val task = TimeSlot(
                                id = editingTask?.id ?: UUID.randomUUID().toString(),
                                title = newTitle,
                                subtitle = newSubtitle,
                                startTime = newStartTime,
                                endTime = newEndTime,
                                deadline = newDeadline,
                                examDate = examDate,
                                color = getNextColor()
                            )
                            saveTask(task)
                            showAddDialog = false
                        }
                    },
                    enabled = newTitle.isNotBlank() && newStartTime.isNotBlank() && newEndTime.isNotBlank()
                ) {
                    Text(if (editingTask == null) "Add" else "Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TaskCard(
    slot: TimeSlot,
    onEditClicked: () -> Unit,
    onDoneClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = slot.color.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "S", // "S" for Study
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Study",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        slot.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = onDoneClicked) {
                    Icon(Icons.Default.Check, contentDescription = "Complete", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (slot.subtitle.isNotBlank()) {
                Text(
                    slot.subtitle,
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = "Time",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "${slot.startTime} - ${slot.endTime}",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (slot.deadline.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Event,
                        contentDescription = "Deadline",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        slot.deadline,
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (slot.examDate != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = "Exam",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Exam: ${SimpleDateFormat("MMM dd, yyyy").format(slot.examDate!!)}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDeleteClicked) {
                    Text("Delete", color = Color.White)
                }
                Button(
                    onClick = onEditClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = Color.White
                    )
                ) {
                    Text("Edit")
                }
            }
        }
    }
}