package com.example.itworkshopproject.screens.home

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.itworkshopproject.model.Mood
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

data class MoodLog(
    val mood: Mood,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun MoodAnalysisScreen(
    moodCounts: Map<Mood, Int>,
    onJourneyClick: () -> Unit
) {
    val moodLogs = remember {
        listOf(
            MoodLog(Mood.HAPPY),
            MoodLog(Mood.CALM),
            MoodLog(Mood.SAD),
            MoodLog(Mood.ANGRY),
            MoodLog(Mood.EXCITED),
            MoodLog(Mood.NEUTRAL)
        )
    }

    var showEmojiDialog by remember { mutableStateOf(false) }
    var showJourneyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFFEDE7F6), Color(0xFFF3E5F5)))
            )
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            "Mood Analysis 📊",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A148C),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        MoodLineChart(moodLogs)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { showEmojiDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
        ) {
            Text("Emoji Frequency", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showJourneyDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF512DA8))
        ) {
            Text("Your Emotional Journey", color = Color.White)
        }
    }

    if (showEmojiDialog) {
        EmojiFrequencyDialog(moodCounts) { showEmojiDialog = false }
    }

    if (showJourneyDialog) {
        EmotionalJourneyDialog(moodLogs) { showJourneyDialog = false }
    }
}

@Composable
fun MoodLineChart(moodLogs: List<MoodLog>) {
    // Mood emoji display on Y-axis
    val moodLabels = listOf("😢", "😐", "😊", "😡", "😴", "🤩")  // Adjust if needed
    // Simulate days for X-axis
    val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            LineChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description = Description().apply { text = "Mood Over the Week" }
                setTouchEnabled(true)
                isDragEnabled = true
                setPinchZoom(true)

                axisRight.isEnabled = false
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)
                xAxis.valueFormatter = IndexAxisValueFormatter(dayLabels)

                axisLeft.granularity = 1f
                axisLeft.axisMinimum = 0f
                axisLeft.axisMaximum = (moodLabels.size - 1).toFloat()
                axisLeft.setDrawGridLines(true)
                axisLeft.valueFormatter = IndexAxisValueFormatter(moodLabels)

                legend.isEnabled = true
            }
        },
        update = { chart ->
            val entries = moodLogs.mapIndexed { index, log ->
                Entry(index.toFloat(), log.mood.ordinal.toFloat())
            }

            val dataSet = LineDataSet(entries, "Your Mood").apply {
                color = android.graphics.Color.rgb(103, 58, 183)
                valueTextColor = android.graphics.Color.DKGRAY
                lineWidth = 2f
                setDrawValues(false)
                setCircleColor(android.graphics.Color.rgb(179, 136, 255))
                circleRadius = 5f
                setDrawCircles(true)
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }

            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
}

@Composable
fun EmojiFrequencyDialog(moodCounts: Map<Mood, Int>, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Emoji Frequency", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3700B3))

                Spacer(modifier = Modifier.height(16.dp))

                moodCounts.forEach { (mood, count) ->
                    MoodCard(mood, count)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3700B3)),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun EmotionalJourneyDialog(moodLogs: List<MoodLog>, onDismiss: () -> Unit) {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val groupedLogs = moodLogs.groupBy { formatter.format(Date(it.timestamp)) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Your Mood History", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF512DA8))

                groupedLogs.forEach { (date, logs) ->
                    Text(date, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6A1B9A))
                    logs.forEach {
                        Text("• ${it.mood.emoji} ${it.mood.name}", fontSize = 16.sp)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF512DA8)),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun MoodCard(mood: Mood, count: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD1C4E9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(mood.emoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(mood.name, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
            Surface(shape = CircleShape, color = Color(0xFF9575CD)) {
                Text(
                    "$count",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
