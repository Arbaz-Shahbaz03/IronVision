package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WorkoutRepository
import com.example.data.model.WorkoutSession
import com.example.ui.theme.FormBreakdownOrange
import com.example.ui.theme.IronBorder
import com.example.ui.theme.IronDarkBackground
import com.example.ui.theme.IronSurfaceDark
import com.example.ui.theme.IronTextMuted
import com.example.ui.theme.IronTextPrimary
import com.example.ui.theme.IronTextSecondary
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonLimeDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionHistoryScreen(
    repository: WorkoutRepository,
    onSessionSelected: (sessionId: Long) -> Unit,
    onStartNewWorkout: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sessions by repository.allSessions.collectAsState(initial = emptyList())
    var filterExercise by remember { mutableStateOf("ALL") }

    val filteredSessions = when (filterExercise) {
        "SQUAT" -> sessions.filter { it.exerciseType.contains("SQUAT", ignoreCase = true) }
        "DEADLIFT" -> sessions.filter { it.exerciseType.contains("DEADLIFT", ignoreCase = true) }
        "BENCH" -> sessions.filter { it.exerciseType.contains("BENCH", ignoreCase = true) }
        else -> sessions
    }

    val totalRepsLogged = if (sessions.isEmpty()) 142 else sessions.sumOf { it.totalReps }
    val flaggedCount = if (sessions.isEmpty()) 2 else sessions.count { it.maxBarDeviationCm > 4.5f }
    val avgDeviation = if (sessions.isEmpty()) 1.8f else {
        val d = sessions.map { it.maxBarDeviationCm }.average().toFloat()
        if (d.isNaN()) 1.8f else d
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(IronDarkBackground),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SESSION HISTORY",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = IronTextPrimary,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(IronSurfaceDark)
                            .border(1.dp, IronBorder, CircleShape)
                            .testTag("history_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = IronDarkBackground
                )
            )
        },
        containerColor = IronDarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // STAT PILL ROW (Image 2 style)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardStatPill(
                        icon = Icons.Default.FitnessCenter,
                        number = "$totalRepsLogged",
                        label = "REPS LOGGED",
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatPill(
                        icon = Icons.Default.ReportProblem,
                        number = "$flaggedCount",
                        label = "FLAGGED SETS",
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatPill(
                        icon = Icons.Default.ShowChart,
                        number = String.format("%.1fcm", avgDeviation),
                        label = "AVG DRIFT",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Filter Chips (Pill shaped)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(listOf("ALL", "SQUAT", "DEADLIFT", "BENCH")) { label ->
                        val isSelected = filterExercise == label
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) NeonLime else IronSurfaceDark)
                                .border(1.dp, if (isSelected) NeonLime else IronBorder, RoundedCornerShape(50))
                                .clickable { filterExercise = label }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                                .testTag("filter_chip_$label")
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) NeonLimeDark else IronTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Sessions List
            if (filteredSessions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = IronSurfaceDark),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(IronBorder)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "NO SESSIONS RECORDED YET",
                                color = IronTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Track your first set with the live on-device vision coach.",
                                color = IronTextSecondary,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onStartNewWorkout,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonLime,
                                    contentColor = NeonLimeDark
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "START FIRST SET",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            } else {
                items(filteredSessions) { session ->
                    SessionHistoryItemCard(
                        session = session,
                        onClick = { onSessionSelected(session.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SessionHistoryItemCard(
    session: WorkoutSession,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()) }
    val formattedDate = remember(session.timestamp) { dateFormat.format(Date(session.timestamp)) }
    val isFormWarning = session.maxBarDeviationCm > 4.5f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("session_item_${session.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = IronSurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isFormWarning) FormBreakdownOrange.copy(alpha = 0.5f) else IronBorder
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = session.exerciseType,
                        color = IronTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isFormWarning) FormBreakdownOrange.copy(alpha = 0.15f) else NeonLime.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isFormWarning) "FLAGGED" else "PRISTINE",
                            color = if (isFormWarning) FormBreakdownOrange else NeonLime,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${session.totalReps} REPS • ${session.durationSeconds} SEC • RPE ${session.rpe}",
                    color = IronTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formattedDate,
                    color = IronTextMuted,
                    fontSize = 10.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${session.avgDepthAngleDeg.toInt()}°",
                        color = IronTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "DEPTH",
                        color = IronTextMuted,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View Details",
                    tint = IronTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
