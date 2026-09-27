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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billing.RevenueCatManager
import com.example.data.WorkoutRepository
import com.example.data.model.RepMetric
import com.example.data.model.WorkoutSession
import com.example.ui.theme.FormBreakdownOrange
import com.example.ui.theme.FormBreakdownRed
import com.example.ui.theme.IronBorder
import com.example.ui.theme.IronDarkBackground
import com.example.ui.theme.IronSurfaceDark
import com.example.ui.theme.IronSurfaceElevated
import com.example.ui.theme.IronTextMuted
import com.example.ui.theme.IronTextPrimary
import com.example.ui.theme.IronTextSecondary
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonLimeDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostSetReviewScreen(
    sessionId: Long,
    repository: WorkoutRepository,
    revenueCatManager: RevenueCatManager,
    onBack: () -> Unit,
    onOpenPaywall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isProUnlocked by revenueCatManager.isProAccessUnlocked.collectAsState()
    val sessionState by repository.getSession(sessionId).collectAsState(initial = null)
    val repsState by repository.getAllRepsForSession(sessionId).collectAsState(initial = emptyList())

    var selectedRepFilter by remember { mutableIntStateOf(0) } // 0 = All reps, 1..N = specific rep
    var showExportToast by remember { mutableStateOf(false) }

    val displaySession = sessionState ?: WorkoutSession(
        id = sessionId,
        exerciseType = "SQUAT",
        totalReps = 5,
        avgDepthAngleDeg = 84.6f,
        maxBarDeviationCm = 3.2f,
        durationSeconds = 46L,
        rpe = 8.5f
    )

    val displayReps = if (repsState.isNotEmpty()) repsState else listOf(
        RepMetric(1, 1, 1, 86f, 74f, 2.1f, 2.2f, 1.1f, 96, "CLEAN_REP", true),
        RepMetric(2, 1, 2, 84f, 72f, 2.4f, 2.0f, 1.2f, 94, "CLEAN_REP", true),
        RepMetric(3, 1, 3, 85f, 73f, 2.8f, 2.3f, 1.4f, 92, "CLEAN_REP", true),
        RepMetric(4, 1, 4, 83f, 70f, 3.2f, 2.4f, 1.8f, 88, "SLIGHT_DRIFT", true),
        RepMetric(5, 1, 5, 85f, 69f, 3.1f, 2.5f, 2.1f, 85, "FATIGUE_SPEED_DROP", true)
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(IronDarkBackground),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "SET BIOMECHANICAL AUDIT",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = IronTextPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${displaySession.exerciseType} • ${displayReps.size} REPS COMPLETED",
                            fontSize = 11.sp,
                            color = NeonLime,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                            .testTag("back_to_camera_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(50))
                            .background(NeonLime.copy(alpha = 0.15f))
                            .border(1.dp, NeonLime, RoundedCornerShape(50))
                            .clickable { onOpenPaywall() }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .testTag("pro_status_badge")
                    ) {
                        Text(
                            text = "PRO ACTIVE",
                            color = NeonLime,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // SUMMARY METRIC CARDS (3 Stat pills matching Image 2 design)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricSummaryCard(
                        title = "AVG DEPTH",
                        value = "${displaySession.avgDepthAngleDeg.toInt()}°",
                        subtitle = "PARALLEL BREACHED",
                        valueColor = NeonLime,
                        modifier = Modifier.weight(1f)
                    )
                    MetricSummaryCard(
                        title = "MAX DRIFT",
                        value = "${displaySession.maxBarDeviationCm}cm",
                        subtitle = "TOLERANCE <4.5cm",
                        valueColor = if (displaySession.maxBarDeviationCm > 4.5f) FormBreakdownOrange else NeonLime,
                        modifier = Modifier.weight(1f)
                    )
                    MetricSummaryCard(
                        title = "FORM SCORE",
                        value = "93/100",
                        subtitle = "COMPETITION PASS",
                        valueColor = NeonLime,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 2D BAR-PATH TRAJECTORY GRAPH
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bar_path_graph_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = IronSurfaceDark),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(IronBorder)
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "2D BAR-PATH TRAJECTORY",
                                    color = IronTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = "Barbell deviation relative to mid-foot gravity axis",
                                    color = IronTextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(NeonLime.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "GHOST ACTIVE",
                                    color = NeonLime,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Filter chips (Pill shaped)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("ALL REPS", "REP 1", "REP 2", "REP 3", "REP 4", "REP 5").forEachIndexed { idx, label ->
                                val isSelected = selectedRepFilter == idx
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(if (isSelected) NeonLime else Color(0xFF1F2024))
                                        .border(1.dp, if (isSelected) NeonLime else IronBorder, RoundedCornerShape(50))
                                        .clickable { selectedRepFilter = idx }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) NeonLimeDark else IronTextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Canvas Graph with Neon Lime Bar Path
                        BarPathGraphCanvas(
                            reps = displayReps,
                            selectedRep = selectedRepFilter,
                            showProGhost = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Legend
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LegendItem(color = NeonLime, label = "Current Set", isDashed = false)
                            LegendItem(color = Color.White.copy(alpha = 0.6f), label = "Historical PR", isDashed = true)
                            LegendItem(color = Color.White.copy(alpha = 0.25f), label = "Midfoot Axis", isDashed = true)
                        }
                    }
                }
            }

            // PRO FEATURES SECTION (Full access unlocked)
            item {
                ProGatedFeatureCard(
                    title = "HISTORICAL FORM COMPARISON & GHOST OVERLAY",
                    description = "Overlay your current barbell trajectory directly against your personal record 1RM baseline curve.",
                    isUnlocked = true,
                    onUnlockClicked = onOpenPaywall,
                    previewContent = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "BASELINE: 160 KG SQUAT (PR)",
                                    color = NeonLime,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Horizontal drift reduced by 1.1 cm compared to historical average.",
                                    color = IronTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(NeonLime.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+8.4% STABILITY",
                                    color = NeonLime,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                )
            }

            // PRO FEATURE 2: Velocity Based Training (VBT)
            item {
                ProGatedFeatureCard(
                    title = "BARBELL VELOCITY LOSS & FATIGUE (VBT)",
                    description = "Tracks concentric speed drop-off across reps to measure true neuromuscular fatigue before failure.",
                    isUnlocked = true,
                    onUnlockClicked = onOpenPaywall,
                    previewContent = {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "REP 1 SPEED: 0.72 m/s",
                                    color = NeonLime,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "REP 5 SPEED: 0.44 m/s",
                                    color = FormBreakdownOrange,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Velocity loss: 38.8% • RPE 8.5 estimated reserve: 1 rep in tank.",
                                color = IronTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                )
            }

            // PRO FEATURE 3: Video Export
            item {
                ProGatedFeatureCard(
                    title = "BAR-PATH VIDEO EXPORT (60 FPS TELEMETRY)",
                    description = "Exports MP4 video of your set with the skeleton overlay, barbell curve, and joint angles baked in.",
                    isUnlocked = true,
                    onUnlockClicked = onOpenPaywall,
                    previewContent = {
                        Button(
                            onClick = { showExportToast = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("export_video_button"),
                            shape = RoundedCornerShape(50), // Pill button
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonLime,
                                contentColor = NeonLimeDark
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "EXPORT 1080P TELEMETRY VIDEO",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun MetricSummaryCard(
    title: String,
    value: String,
    subtitle: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = IronSurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(IronBorder)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = IronTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = valueColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = IronTextMuted,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BarPathGraphCanvas(
    reps: List<RepMetric>,
    selectedRep: Int,
    showProGhost: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF141518))
            .border(1.dp, IronBorder, RoundedCornerShape(16.dp))
    ) {
        val w = size.width
        val h = size.height
        val midX = w * 0.5f

        // 1. Grid Lines
        val gridColor = Color(0xFF22242B)
        for (i in 1..4) {
            val y = h * (i / 5f)
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1f
            )
        }

        // 2. Midfoot Reference Line (White dashed)
        val dashed = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
        drawLine(
            color = Color.White.copy(alpha = 0.4f),
            start = Offset(midX, 10f),
            end = Offset(midX, h - 10f),
            strokeWidth = 2f,
            pathEffect = dashed
        )

        // Safe Tolerance Lines (±4.5 cm scaled)
        val tolPx = w * 0.18f
        drawLine(
            color = Color.White.copy(alpha = 0.12f),
            start = Offset(midX - tolPx, 10f),
            end = Offset(midX - tolPx, h - 10f),
            strokeWidth = 1f,
            pathEffect = dashed
        )
        drawLine(
            color = Color.White.copy(alpha = 0.12f),
            start = Offset(midX + tolPx, 10f),
            end = Offset(midX + tolPx, h - 10f),
            strokeWidth = 1f,
            pathEffect = dashed
        )

        // 3. Pro Ghost Baseline
        if (showProGhost) {
            val ghostPath = Path().apply {
                moveTo(midX, h * 0.15f)
                cubicTo(
                    midX + 8f, h * 0.45f,
                    midX - 6f, h * 0.70f,
                    midX + 4f, h * 0.85f
                )
            }
            drawPath(
                path = ghostPath,
                color = Color.White.copy(alpha = 0.45f),
                style = Stroke(width = 2.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)))
            )
        }

        // 4. Current Set Bar Path (Neon Lime)
        val currentPath = Path().apply {
            moveTo(midX, h * 0.18f)
            cubicTo(
                midX + 16f, h * 0.42f,
                midX - 10f, h * 0.68f,
                midX + 12f, h * 0.86f
            )
        }
        drawPath(
            path = currentPath,
            color = NeonLime.copy(alpha = 0.3f),
            style = Stroke(width = 8f, cap = StrokeCap.Round)
        )
        drawPath(
            path = currentPath,
            color = NeonLime,
            style = Stroke(width = 3.5f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun ProGatedFeatureCard(
    title: String,
    description: String,
    isUnlocked: Boolean,
    onUnlockClicked: () -> Unit,
    previewContent: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = IronSurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(IronBorder)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = IronTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(NeonLime.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "UNLOCKED",
                        color = NeonLime,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = IronTextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1F2024))
                    .padding(12.dp)
            ) {
                previewContent()
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String, isDashed: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(14.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = IronTextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
