package com.example.itworkshopproject.screens.home
import androidx.compose.foundation.shape.CircleShape

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.itworkshopproject.screens.home.components.MemoryStore
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

data class MemoryEntry(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val emotion: String,
    val date: String,
    val imageUri: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryCapsuleScreen(navController: NavController) {
    val context = LocalContext.current
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()

    var memoryList by remember { mutableStateOf(MemoryStore.getEntries(context)) }
    var filteredMemoryList by remember { mutableStateOf<List<MemoryEntry>>(emptyList()) }
    var selectedFilterDate by remember { mutableStateOf<String?>(null) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var emotion by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(Date()) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Vibrant color palette
    val colors = listOf(
        Color(0xFF6200EE),  // Deep Purple
        Color(0xFF03DAC6),  // Teal
        Color(0xFFFF4081),  // Pink
        Color(0xFF536DFE),  // Indigo
        Color(0xFFFF6D00),  // Orange
        Color(0xFF00C853),  // Green
        Color(0xFFAA00FF),  // Purple
        Color(0xFF0091EA),  // Blue
        Color(0xFFFFD600),  // Yellow
        Color(0xFFD50000)   // Red
    )
    val currentColor = remember { mutableStateOf(colors.random()) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            selectedDate = calendar.time
            currentColor.value = colors.random()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val filterDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            val dateString = formatter.format(calendar.time)
            selectedFilterDate = dateString
            filteredMemoryList = memoryList.filter { it.date == dateString }
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
        currentColor.value = colors.random()
    }

    fun deleteMemory(memoryId: String) {
        memoryList = memoryList.filter { it.id != memoryId }
        MemoryStore.saveEntries(context, memoryList)
        if (selectedFilterDate != null) {
            filteredMemoryList = memoryList.filter { it.date == selectedFilterDate }
        }
        Toast.makeText(context, "Memory deleted", Toast.LENGTH_SHORT).show()
    }

    fun clearFilter() {
        selectedFilterDate = null
        filteredMemoryList = emptyList()
    }

    LaunchedEffect(memoryList) {
        if (selectedFilterDate != null) {
            filteredMemoryList = memoryList.filter { it.date == selectedFilterDate }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "📅 Memory Capsule",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = currentColor.value
                ),
                actions = {
                    IconButton(onClick = { filterDatePickerDialog.show() }) {
                        Icon(Icons.Default.FilterAlt, contentDescription = "Filter", tint = Color.White)
                    }
                    if (selectedFilterDate != null) {
                        IconButton(onClick = { clearFilter() }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Filter", tint = Color.White)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        val savedUri = selectedImageUri?.let {
                            copyImageToInternalStorage(context, it)
                        }

                        val memory = MemoryEntry(
                            title = title,
                            description = description,
                            emotion = emotion,
                            date = formatter.format(selectedDate),
                            imageUri = savedUri?.toString()
                        )
                        memoryList = memoryList + memory
                        MemoryStore.saveEntries(context, memoryList)

                        title = ""
                        description = ""
                        emotion = ""
                        selectedImageUri = null
                        currentColor.value = colors.random()

                        Toast.makeText(context, "Memory saved!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                containerColor = currentColor.value
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // Filter indicator
            if (selectedFilterDate != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(currentColor.value.copy(alpha = 0.2f))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Showing memories from $selectedFilterDate",
                        color = currentColor.value,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    TextButton(onClick = { clearFilter() }) {
                        Text("Clear", color = currentColor.value)
                    }
                }
            }

            // Form section
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = currentColor.value,
                            focusedLabelColor = currentColor.value
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = currentColor.value,
                            focusedLabelColor = currentColor.value
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = emotion,
                        onValueChange = { emotion = it },
                        label = { Text("Emotion") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = currentColor.value,
                            focusedLabelColor = currentColor.value
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { datePickerDialog.show() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = currentColor.value
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Select Date")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors[(colors.indexOf(currentColor.value) + 1) % colors.size]
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Photo, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Photo")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Selected Date: ${formatter.format(selectedDate)}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )

                    selectedImageUri?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(it),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            IconButton(
                                onClick = { selectedImageUri = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(
                                        color = Color.Black.copy(alpha = 0.4f),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                if (selectedFilterDate != null) "Memories from $selectedFilterDate" else "All Memories",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(if (selectedFilterDate != null) filteredMemoryList.reversed() else memoryList.reversed()) { memory ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    memory.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = currentColor.value
                                )
                                IconButton(onClick = { deleteMemory(memory.id) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFD32F2F)
                                    )
                                }
                            }

                            Text(
                                memory.date,
                                color = Color.Gray,
                                fontSize = 12.sp
                            )

                            if (memory.emotion.isNotBlank()) {
                                FilterChip(
                                    selected = false,
                                    onClick = {},
                                    label = { Text(memory.emotion) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = currentColor.value.copy(alpha = 0.2f),
                                        labelColor = currentColor.value
                                    ),
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            memory.imageUri?.let {
                                Spacer(modifier = Modifier.height(12.dp))
                                Image(
                                    painter = rememberAsyncImagePainter(Uri.parse(it)),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                memory.description,
                                fontSize = 14.sp,
                                color = Color.Black.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper functions remain the same
fun copyImageToInternalStorage(context: Context, uri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = getFileName(context, uri) ?: "memory_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)

        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        file.toUri()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getFileName(context: Context, uri: Uri): String? {
    val returnCursor = context.contentResolver.query(uri, null, null, null, null)
    returnCursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0) return it.getString(nameIndex)
        }
    }
    return null
}