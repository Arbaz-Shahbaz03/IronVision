package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billing.RevenueCatManager
import com.example.biomechanics.ExerciseType
import com.example.biomechanics.MuscleDivision
import com.example.data.WorkoutRepository
import com.example.data.model.UserProfile
import com.example.ui.theme.IronBorder
import com.example.ui.theme.IronDarkBackground
import com.example.ui.theme.IronSurfaceDark
import com.example.ui.theme.IronSurfaceElevated
import com.example.ui.theme.IronTextMuted
import com.example.ui.theme.IronTextPrimary
import com.example.ui.theme.IronTextSecondary
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonLimeDark

/**
 * Section 4.3 Dashboard / Session History Screen — Mapped strictly to Reference Image 2
 *
 * - Header: Avatar + "Welcome back, [Name]" greeting top-left, settings/notification icon top-right
 * - Stat row: 3 pill cards (icon + number + label): Reps this week, Flagged sets, Avg bar deviation
 * - Weekly chart card: Bar chart with one bar highlighted in neon lime-green (Image 2 style)
 * - "Personalized Plan" Pro card: Photo-backed card reading "Unlock Bar-Path Analytics" with circular play button
 * - Floating dark pill bottom navigation bar: 4 icons (Home, Tracking Camera, History, Settings)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    repository: WorkoutRepository,
    revenueCatManager: RevenueCatManager,
    onSelectExercise: (ExerciseType) -> Unit,
    onNavigateExercises: (MuscleDivision) -> Unit,
    onNavigateHistory: () -> Unit,
    onNavigatePaywall: () -> Unit,
    onNavigateRegister: () -> Unit,
    onNavigateWinterArc: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allSessions by repository.allSessions.collectAsState(initial = emptyList())
    val userProfile by repository.userProfile.collectAsState(initial = null)
    var activeNavIndex by remember { mutableStateOf(0) } // 0 = Home, 1 = Exercises, 2 = Tracking, 3 = History, 4 = Profile
    var showProfileModal by remember { mutableStateOf(false) }

    val athleteName = userProfile?.name?.takeIf { it.isNotBlank() } ?: "Athlete"
    val initials = remember(athleteName) {
        val parts = athleteName.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        if (parts.size >= 2) {
            "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
        } else if (parts.isNotEmpty()) {
            parts[0].take(2).uppercase()
        } else {
            "IV"
        }
    }

    // Calculate aggregated telemetry stats
    val totalReps = remember(allSessions) {
        if (allSessions.isEmpty()) 142 else allSessions.sumOf { it.totalReps }
    }
    val flaggedSets = remember(allSessions) {
        if (allSessions.isEmpty()) 2 else allSessions.count { it.maxBarDeviationCm > 4.5f }
    }
    val avgDeviation = remember(allSessions) {
        if (allSessions.isEmpty()) 1.8f else {
            val dev = allSessions.map { it.maxBarDeviationCm }.average().toFloat()
            if (dev.isNaN()) 1.8f else dev
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(IronDarkBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp) // Room for floating bottom nav
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. HEADER: Avatar + Welcome back + Dynamic Athlete Name + Settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { showProfileModal = true }
                        .padding(vertical = 4.dp)
                        .testTag("dashboard_profile_header")
                ) {
                    // Avatar Circle with dynamic initials
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E3036))
                            .border(1.dp, NeonLime.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = NeonLime,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Welcome back,",
                            color = IronTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = athleteName,
                            color = IronTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Settings / Notification Icon
                IconButton(
                    onClick = onNavigatePaywall,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(IronSurfaceDark)
                        .border(1.dp, IronBorder, CircleShape)
                        .testTag("dashboard_notifications_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. STAT ROW: Three pill cards (Image 2 reference: Steps / Calories / Heartbeat)
            // IronVision: Total reps this week, Flagged sets, Avg bar deviation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Pill 1: Reps
                DashboardStatPill(
                    icon = Icons.Default.FitnessCenter,
                    number = "$totalReps",
                    label = "REPS",
                    modifier = Modifier.weight(1f)
                )

                // Pill 2: Form Flags
                DashboardStatPill(
                    icon = Icons.Default.ReportProblem,
                    number = "$flaggedSets",
                    label = "FLAGGED",
                    modifier = Modifier.weight(1f)
                )

                // Pill 3: Avg Deviation
                DashboardStatPill(
                    icon = Icons.Default.ShowChart,
                    number = String.format("%.1fcm", avgDeviation),
                    label = "AVG DRIFT",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. WEEKLY CHART CARD (Image 2 reference: "Calories / Weekly Average" bar chart)
            // One bar highlighted in Neon Lime-Green (Tuesday/Current Day)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weekly_chart_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = IronSurfaceDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(IronBorder)
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Weekly Lifting Volume",
                                color = IronTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Daily completed reps & biomechanics",
                                color = IronTextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF1F2024))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "THIS WEEK",
                                color = NeonLime,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Minimalist vertical bar chart (matching Image 2)
                    val weeklyReps = remember(allSessions) {
                        val calendar = java.util.Calendar.getInstance()
                        val days = FloatArray(7) { 0f }
                        allSessions.forEach { session ->
                            calendar.timeInMillis = session.timestamp
                            val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
                            val index = if (dayOfWeek == java.util.Calendar.SUNDAY) 6 else dayOfWeek - 2
                            if (index in 0..6) {
                                days[index] += session.totalReps.toFloat()
                            }
                        }
                        val max = days.maxOrNull()?.coerceAtLeast(1f) ?: 1f
                        days.map { if (it == 0f) 0.05f else (it / max).coerceIn(0.1f, 1f) }
                    }

                    val calendar = java.util.Calendar.getInstance()
                    val currentDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
                    val currentDayIndex = if (currentDayOfWeek == java.util.Calendar.SUNDAY) 6 else currentDayOfWeek - 2

                    WeeklyVolumeBarChart(
                        heights = weeklyReps,
                        highlightedIndex = currentDayIndex,
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. EXERCISE DIVISIONS & MOVEMENT LIBRARY (Chest, Back, Legs, Core)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Exercise Divisions",
                    color = IronTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Browse All (24+) →",
                    color = NeonLime,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onNavigateExercises(MuscleDivision.FULL_BODY) }
                        .padding(4.dp)
                        .testTag("dashboard_view_all_exercises_link")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4 Category Cards: Chest, Back, Legs, Core
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DivisionCategoryCard(
                    title = "CHEST & PECS",
                    sub = "Bench, Incline, Dips",
                    colorHex = 0xFFF59E0B,
                    count = "5 Lifts",
                    onClick = { onNavigateExercises(MuscleDivision.CHEST) },
                    modifier = Modifier.weight(1f)
                )
                DivisionCategoryCard(
                    title = "BACK & LATS",
                    sub = "Deadlift, Rows, Pulls",
                    colorHex = 0xFF38BDF8,
                    count = "5 Lifts",
                    onClick = { onNavigateExercises(MuscleDivision.BACK) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DivisionCategoryCard(
                    title = "LEGS & GLUTES",
                    sub = "Squat, RDL, Lunges",
                    colorHex = 0xFF10B981,
                    count = "6 Lifts",
                    onClick = { onNavigateExercises(MuscleDivision.LEGS) },
                    modifier = Modifier.weight(1f)
                )
                DivisionCategoryCard(
                    title = "ABS & CORE",
                    sub = "Plank, Leg Raise, Roll",
                    colorHex = 0xFF06B6D4,
                    count = "5 Lifts",
                    onClick = { onNavigateExercises(MuscleDivision.CORE) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // WINTER ARC FEATURE CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateWinterArc() }
                    .testTag("winter_arc_dashboard_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141E30)), // Deep icy blue background
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF60A5FA).copy(alpha = 0.5f)))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AcUnit,
                                contentDescription = null,
                                tint = Color(0xFF93C5FD),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "WINTER ARC",
                                color = Color(0xFF93C5FD),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Build Strength & Discipline",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF2563EB))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "8 Week Training Program",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 6. QUICK WORKOUT LAUNCHER (Directly starts camera tracking)
            Text(
                text = "Featured Live Tracking",
                color = IronTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickExerciseCard(
                    title = "SQUAT",
                    sub = "Barbell Back Squat",
                    onClick = { onSelectExercise(ExerciseType.SQUAT) },
                    modifier = Modifier.weight(1f)
                )
                QuickExerciseCard(
                    title = "DEADLIFT",
                    sub = "Conventional Barbell",
                    onClick = { onSelectExercise(ExerciseType.DEADLIFT) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickExerciseCard(
                    title = "BENCH PRESS",
                    sub = "Pectoral Barbell",
                    onClick = { onSelectExercise(ExerciseType.BENCH_PRESS) },
                    modifier = Modifier.weight(1f)
                )
                QuickExerciseCard(
                    title = "BARBELL ROW",
                    sub = "Bent-Over Lat Hinge",
                    onClick = { onSelectExercise(ExerciseType.BARBELL_ROW) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 7. FLOATING BOTTOM NAVIGATION BAR (Image 2 reference: 5 icons)
        // Dark pill-shaped container floating slightly above screen edge
        // Icons: Home, Exercises Library, Camera (Live Tracking), Document (History), Athlete Profile
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 12.dp, start = 24.dp, end = 24.dp)
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(50))
                .background(IronSurfaceDark)
                .border(1.dp, IronBorder, RoundedCornerShape(50))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Home / Grid
                IconButton(onClick = { activeNavIndex = 0 }) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = "Home",
                        tint = if (activeNavIndex == 0) NeonLime else IronTextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // 2. Exercise Library (Chest, Back, Legs, Core)
                IconButton(onClick = {
                    activeNavIndex = 1
                    onNavigateExercises(MuscleDivision.FULL_BODY)
                }) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = "Exercises",
                        tint = if (activeNavIndex == 1) NeonLime else IronTextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // 3. Camera Tracking (Centerpiece)
                IconButton(onClick = {
                    activeNavIndex = 2
                    onSelectExercise(ExerciseType.SQUAT)
                }) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (activeNavIndex == 2) NeonLime else Color(0xFF1F2024)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Live Tracking",
                            tint = if (activeNavIndex == 2) NeonLimeDark else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // 4. Document / Session History
                IconButton(onClick = {
                    activeNavIndex = 3
                    onNavigateHistory()
                }) {
                    Icon(
                        imageVector = Icons.Default.HistoryEdu,
                        contentDescription = "History",
                        tint = if (activeNavIndex == 3) NeonLime else IronTextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // 5. Athlete Profile / Settings
                IconButton(onClick = {
                    activeNavIndex = 4
                    showProfileModal = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = if (activeNavIndex == 4) NeonLime else IronTextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // ATHLETE PROFILE BOTTOM SHEET MODAL
        if (showProfileModal) {
            ModalBottomSheet(
                onDismissRequest = { showProfileModal = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = IronSurfaceDark,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar Header
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(NeonLime.copy(alpha = 0.15f))
                            .border(2.dp, NeonLime, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = NeonLime,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = athleteName,
                        color = IronTextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = "Registered Athlete Profile",
                        color = IronTextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Athlete Metrics Grid
                    val currentAge = userProfile?.age ?: 24
                    val currentHeight = userProfile?.heightCm?.toInt() ?: 178
                    val currentWeight = userProfile?.weightKg?.toInt() ?: 78
                    val bmi = if (currentHeight > 0) {
                        val hM = currentHeight / 100f
                        currentWeight / (hM * hM)
                    } else 24.6f

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ProfileMetricBox(
                            label = "AGE",
                            value = "$currentAge yrs",
                            modifier = Modifier.weight(1f)
                        )
                        ProfileMetricBox(
                            label = "HEIGHT",
                            value = "$currentHeight cm",
                            modifier = Modifier.weight(1f)
                        )
                        ProfileMetricBox(
                            label = "WEIGHT",
                            value = "$currentWeight kg",
                            modifier = Modifier.weight(1f)
                        )
                        ProfileMetricBox(
                            label = "BMI",
                            value = String.format("%.1f", bmi),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Training Goals & Experience Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(IronDarkBackground)
                            .border(1.dp, IronBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Primary Goal:", color = IronTextSecondary, fontSize = 12.sp)
                                Text(
                                    text = userProfile?.fitnessGoal ?: "Strength & Form",
                                    color = NeonLime,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Experience:", color = IronTextSecondary, fontSize = 12.sp)
                                Text(
                                    text = userProfile?.experienceLevel ?: "Intermediate (1-3 yrs)",
                                    color = IronTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Focus Muscle:", color = IronTextSecondary, fontSize = 12.sp)
                                Text(
                                    text = userProfile?.targetMuscle ?: "All Muscle Groups",
                                    color = IronTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // EDIT PROFILE BUTTON
                    androidx.compose.material3.Button(
                        onClick = {
                            showProfileModal = false
                            onNavigateRegister()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("dashboard_edit_profile_button"),
                        shape = RoundedCornerShape(50),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = NeonLime,
                            contentColor = NeonLimeDark
                        )
                    ) {
                        Text(
                            text = "UPDATE PROFILE / SWITCH ATHLETE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Stat pill matching Image 2 reference:
 * Rounded rectangle, dark grey surface, icon top, bold number middle, small all-caps label bottom.
 */
@Composable
fun DashboardStatPill(
    icon: ImageVector,
    number: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(115.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(IronSurfaceDark)
            .border(1.dp, IronBorder, RoundedCornerShape(20.dp))
            .padding(14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeonLime,
                modifier = Modifier.size(18.dp)
            )

            Column {
                Text(
                    text = number,
                    color = IronTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = label,
                    color = IronTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

/**
 * Weekly volume bar chart matching Image 2:
 * Vertical bars with unselected bars in dark grey (#3A3B40), and Tuesday/highlighted bar in solid Neon-Lime.
 */
@Composable
fun WeeklyVolumeBarChart(
    heights: List<Float> = listOf(0.05f, 0.90f, 0.05f, 0.05f, 0.05f, 0.05f, 0.05f),
    highlightedIndex: Int = 1,
    modifier: Modifier = Modifier
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        days.forEachIndexed { idx, day ->
            val isHighlighted = idx == highlightedIndex
            val barRatio = heights.getOrElse(idx) { 0.05f }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                // Bar container to prevent stacking
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height((100 * barRatio).dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (isHighlighted) NeonLime else Color(0xFF32343A))
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Day Label
                Text(
                    text = day,
                    color = if (isHighlighted) NeonLime else IronTextMuted,
                    fontSize = 10.sp,
                    fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun QuickExerciseCard(
    title: String,
    sub: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("quick_exercise_${title.lowercase()}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = IronSurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(IronBorder)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    color = IronTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = sub,
                    color = IronTextSecondary,
                    fontSize = 10.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1F2024)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowOutward,
                    contentDescription = null,
                    tint = NeonLime,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun DivisionCategoryCard(
    title: String,
    sub: String,
    colorHex: Long,
    count: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = Color(colorHex)

    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("division_card_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = IronSurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(IronBorder)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(categoryColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = count,
                        color = categoryColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1F2024)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowOutward,
                        contentDescription = null,
                        tint = categoryColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                color = IronTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )

            Text(
                text = sub,
                color = IronTextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun ProfileMetricBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(IronDarkBackground)
            .border(1.dp, IronBorder, RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = IronTextSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = NeonLime,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

