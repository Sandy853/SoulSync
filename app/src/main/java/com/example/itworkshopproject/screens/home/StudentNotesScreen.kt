package com.example.itworkshopproject.screens.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.itworkshopproject.screens.home.NoteDataStore
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val subject: String,
    val title: String,
    val content: String,
    val images: List<String> = emptyList(),
    val questions: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentNotesScreen(navController: NavController) {
    val gradientBrush = Brush.verticalGradient(
        listOf(Color(0xFFFF9A8B), Color(0xFFFAD0C4), Color(0xFFA1C4FD), Color(0xFFC2E9FB))
    )

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var subjectName by remember { mutableStateOf("") }
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var showQuestionBox by remember { mutableStateOf(false) }
    var questionText by remember { mutableStateOf("") }
    var imageUris by remember { mutableStateOf<List<String>>(emptyList()) }
    var notesList by remember { mutableStateOf<List<Note>>(emptyList()) }

    // Load saved notes on first launch
    LaunchedEffect(Unit) {
        notesList = NoteDataStore.loadNotes(context)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUris = imageUris + it.toString() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF6A11CB), Color(0xFF2575FC))),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            Text("🎓 My Study Notes", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = subjectName,
            onValueChange = { subjectName = it },
            label = { Text("Enter Subject/Course", color = Color(0xFF6A11CB)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF6A11CB),
                unfocusedBorderColor = Color(0xFF2575FC),
                cursorColor = Color(0xFF6A11CB)
            ),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                Icon(Icons.Default.School, contentDescription = "Subject", tint = Color(0xFF6A11CB))
            },
            textStyle = TextStyle(color = Color(0xFF333333))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Note title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE9F7FE), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF2575FC), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            BasicTextField(
                value = noteTitle,
                onValueChange = { noteTitle = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 18.sp, color = Color(0xFF333333), fontWeight = FontWeight.Bold),
                decorationBox = { innerTextField ->
                    if (noteTitle.isEmpty()) {
                        Text("Note Title", color = Color(0xFF6A11CB).copy(alpha = 0.6f))
                    }
                    innerTextField()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notes
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFF6A11CB).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                BasicTextField(
                    value = noteContent,
                    onValueChange = { noteContent = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF333333))
                )

                LazyColumn {
                    items(imageUris) { uri ->
                        Image(
                            painter = rememberAsyncImagePainter(ImageRequest.Builder(context).data(uri).build()),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(vertical = 8.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FloatingActionButton(
                        onClick = { showQuestionBox = true },
                        modifier = Modifier.size(40.dp),
                        containerColor = Color(0xFFFF9A8B)
                    ) { Icon(Icons.Default.Quiz, contentDescription = null, tint = Color.White) }

                    Spacer(modifier = Modifier.width(8.dp))

                    FloatingActionButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.size(40.dp),
                        containerColor = Color(0xFFA1C4FD)
                    ) { Icon(Icons.Default.Image, contentDescription = null, tint = Color.White) }

                    Spacer(modifier = Modifier.width(8.dp))

                    FloatingActionButton(
                        onClick = {
                            if (subjectName.isNotBlank() && noteTitle.isNotBlank() && noteContent.isNotBlank()) {
                                val newNote = Note(subjectName, noteTitle, noteContent, imageUris)
                                notesList = notesList + newNote
                                coroutineScope.launch { NoteDataStore.saveNotes(context, notesList) }

                                subjectName = ""
                                noteTitle = ""
                                noteContent = ""
                                imageUris = emptyList()
                            }
                        },
                        modifier = Modifier.size(40.dp),
                        containerColor = Color(0xFF6A11CB)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Index
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF6A11CB).copy(0.2f), Color(0xFF2575FC).copy(0.1f))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(1.dp, Color.White.copy(0.3f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📚 Index", color = Color(0xFF6A11CB), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(notesList.indices.toList()) { index ->
                        val note = notesList[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${note.subject} - ${note.title}",
                                    modifier = Modifier.clickable {
                                        subjectName = note.subject
                                        noteTitle = note.title
                                        noteContent = note.content
                                        imageUris = note.images
                                    },
                                    color = Color(0xFF333333)
                                )
                            }
                            IconButton(onClick = {
                                notesList = notesList.filterIndexed { i, _ -> i != index }
                                coroutineScope.launch { NoteDataStore.saveNotes(context, notesList) }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        }
                        Divider(color = Color(0xFF6A11CB).copy(alpha = 0.2f))
                    }
                }
            }
        }
    }

    if (showQuestionBox) {
        Dialog(onDismissRequest = { showQuestionBox = false }) {
            Surface(shape = RoundedCornerShape(16.dp), color = Color.White, tonalElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("❓ Add Question", color = Color(0xFF6A11CB), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = questionText,
                        onValueChange = { questionText = it },
                        placeholder = { Text("Enter your question") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showQuestionBox = false }) {
                            Text("Cancel", color = Color(0xFF6A11CB))
                        }
                        Button(
                            onClick = {
                                noteContent += "\n\n[Q]: $questionText\n"
                                questionText = ""
                                showQuestionBox = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A11CB))
                        ) {
                            Text("Insert", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
