package com.example.itworkshopproject.screens.home

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@kotlinx.serialization.Serializable
data class Task(
    val title: String,
    val subject: String,
    val type: String,
    val deadline: String,
    val priority: String,
    var status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadlineDominatorScreen(navController: NavController, taskDataStore: TaskDataStore) {
    var tasks by remember { mutableStateOf(mutableListOf<Task>()) }
    var showDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        taskDataStore.taskFlow.collect { loadedTasks ->
            tasks = loadedTasks.toMutableList()
        }
    }

    fun saveTasks() {
        coroutineScope.launch {
            taskDataStore.saveTasks(tasks)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Deadline Dominator 💀", color = Color.White, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.EventNote, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6A1B9A)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFFD81B60)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task", tint = Color.White)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF8BBD0),
                            Color(0xFFCE93D8),
                            Color(0xFF9575CD)
                        )
                    )
                )
        ) {
            if (tasks.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Nothing here yet. Add a task 💼", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
                }
            } else {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(tasks) { task ->
                        TaskCard(task = task, onStatusChange = { newStatus ->
                            task.status = newStatus
                            saveTasks()
                        })
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            if (showDialog) {
                AddTaskDialog(
                    onDismiss = { showDialog = false },
                    onAddTask = { newTask ->
                        tasks = tasks.toMutableList().apply { add(newTask) }
                        saveTasks()
                        showDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun TaskCard(task: Task, onStatusChange: (String) -> Unit) {
    val backgroundColor = when (task.priority) {
        "High" -> Color(0xFFFFCDD2)
        "Medium" -> Color(0xFFFFF9C4)
        "Low" -> Color(0xFFC8E6C9)
        else -> Color(0xFFE1BEE7)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(task.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF4A148C))
            Text("${task.subject} • ${task.type}", fontSize = 14.sp, color = Color.DarkGray)
            Text("Due: ${task.deadline}", fontSize = 13.sp, color = Color.Gray)
            Text("Priority: ${task.priority}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Status: ", fontSize = 14.sp)
                DropdownMenuStatus(current = task.status, onSelected = onStatusChange)
            }
        }
    }
}

@Composable
fun DropdownMenuStatus(current: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Not Started", "In Progress", "Done")

    Box(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .clickable { expanded = true }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(current, fontSize = 14.sp)

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { status ->
                DropdownMenuItem(
                    text = { Text(status) },
                    onClick = {
                        onSelected(status)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onAddTask: (Task) -> Unit) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Assignment") }
    var deadline by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (title.isNotBlank() && subject.isNotBlank() && deadline.isNotBlank()) {
                    onAddTask(
                        Task(
                            title = title,
                            subject = subject,
                            type = type,
                            deadline = deadline,
                            priority = priority,
                            status = "Not Started"
                        )
                    )
                }
            }) {
                Text("Add", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Add New Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") })
                OutlinedTextField(value = deadline, onValueChange = { deadline = it }, label = { Text("Deadline (e.g. 18 Apr, 5PM)") })

                DropdownField(label = "Type", options = listOf("Assignment", "Quiz", "Project", "Presentation")) {
                    type = it
                }

                DropdownField(label = "Priority", options = listOf("High", "Medium", "Low")) {
                    priority = it
                }
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}

@Composable
fun DropdownField(label: String, options: List<String>, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(options.first()) }

    Column {
        Text(text = label, fontSize = 13.sp, color = Color.Gray)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(text = selectedText)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {
                DropdownMenuItem(text = { Text(it) }, onClick = {
                    selectedText = it
                    onSelected(it)
                    expanded = false
                })
            }
        }
    }
}
