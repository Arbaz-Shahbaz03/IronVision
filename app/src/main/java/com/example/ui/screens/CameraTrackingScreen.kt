package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biomechanics.BiomechanicsEngine
import com.example.biomechanics.ExerciseType
import com.example.data.WorkoutRepository
import com.example.data.model.RepMetric
import com.example.ui.camera.CameraPreview
import com.example.ui.camera.PoseOverlayCanvas
import com.example.ui.theme.FormBreakdownOrange
import com.example.ui.theme.IronBorder
import com.example.ui.theme.IronDarkBackground
import com.example.ui.theme.IronTextSecondary
import com.example.ui.theme.NeonLime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraTrackingScreen(
    repository: WorkoutRepository,
    initialExercise: ExerciseType = ExerciseType.SQUAT,
    onFinishSet: (sessionId: Long) -> Unit,
    onNavigateHistory: () -> Unit,
    onNavigatePaywall: () -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    var isFrontCamera by remember { mutableStateOf(false) }
    var isSimulatedDemoMode by remember { mutableStateOf(false) }
    var currentExercise by remember { mutableStateOf(initialExercise) }
    var currentWeightKg by remember { mutableFloatStateOf(initialExercise.defaultWeightKg) }
    var isSetPaused by remember { mutableStateOf(false) }

    val activeSetReps = remember { mutableStateListOf<RepMetric>() }
    var setStartTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var elapsedSeconds by remember { mutableLongStateOf(0L) }

    val engine = remember {
        BiomechanicsEngine(
            exerciseType = currentExercise,
            mediaLinesEnabled = true,
            onRepCompleted = { repMetric ->
                activeSetReps.add(repMetric)
            }
        )
    }

    var currentFrame by remember {
        mutableStateOf(engine.stepSimulatedLifter(0f))
    }

    LaunchedEffect(currentExercise) {
        engine.setExercise(currentExercise)
        currentWeightKg = currentExercise.defaultWeightKg
        activeSetReps.clear()
        setStartTime = System.currentTimeMillis()
    }

    // 30 FPS Kinematics fallback loop for SIM mode
    LaunchedEffect(isSimulatedDemoMode, currentExercise, isSetPaused) {
        while (true) {
            delay(33)
            if (!isSetPaused) {
                elapsedSeconds = (System.currentTimeMillis() - setStartTime) / 1000L
                if (isSimulatedDemoMode) {
                    currentFrame = engine.stepSimulatedLifter(0.033f)
                }
            }
        }
    }

    fun finishAndAuditSet() {
        coroutineScope.launch {
            val sessionId = repository.saveCompletedWorkout(
                exerciseType = currentExercise.displayName,
                weightKg = currentWeightKg,
                reps = activeSetReps.toList(),
                durationSeconds = elapsedSeconds.coerceAtLeast(15L),
                rpe = 8.5f
            )
            onFinishSet(sessionId)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(IronDarkBackground)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -30f) {
                        finishAndAuditSet()
                    }
                }
            }
    ) {
        if (!isSimulatedDemoMode) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                isFrontCamera = isFrontCamera,
                onPoseLandmarks = { landmarks ->
                    if (!isSetPaused && !isSimulatedDemoMode) {
                        elapsedSeconds = (System.currentTimeMillis() - setStartTime) / 1000L
                        currentFrame = engine.processLandmarks(landmarks)
                    }
                }
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF141518)))
        }

        PoseOverlayCanvas(
            frame = currentFrame,
            modifier = Modifier.fillMaxSize()
        )

        // HUD OVERLAY
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 1. TOP STATS BAR (Matching Reference Image 2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Top-Left Info: Pull-Ups: 23, Last Rep: 1.4s, Time: 30.1s
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackToDashboard,
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0x66000000))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${currentExercise.displayName}: ${currentFrame.currentRepCount}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        text = "Last rep: ${String.format(Locale.US, "%.1fs", activeSetReps.lastOrNull()?.let { it.eccentricDurationSec + it.concentricDurationSec } ?: 0f)}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(start = 40.dp)
                    )
                    Text(
                        text = "Time: ${String.format(Locale.US, "%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60)}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(start = 40.dp)
                    )
                }

                // Top-Right: Camera Switch & SIM Toggle
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0x66000000))
                            .clickable { isSimulatedDemoMode = !isSimulatedDemoMode },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isSimulatedDemoMode) "SIM" else "CAM",
                            color = if (isSimulatedDemoMode) NeonLime else Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0x66000000))
                            .clickable { isFrontCamera = !isFrontCamera },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Cameraswitch, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 2. SEARCHING / STATUS OVERLAY
            val isSearching = !isSimulatedDemoMode && currentFrame.landmarks.isEmpty()
            if (isSearching) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SEARCHING FOR LIFTER...",
                        color = NeonLime,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }
            }

            // 3. BOTTOM CONTROL ROW
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ANGLE Pill
                Card(
                    modifier = Modifier.weight(1f).height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x9926272B)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(IronBorder))
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalArrangement = Arrangement.Center) {
                        Text("ANGLE", color = IronTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("${currentFrame.primaryJointAngleDeg.toInt()}°", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // PAUSE Button
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(NeonLime).clickable { isSetPaused = !isSetPaused },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = if (isSetPaused) Icons.Default.PlayArrow else Icons.Default.Pause, contentDescription = null, tint = Color.Black, modifier = Modifier.size(28.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                // BAR PATH Pill
                Card(
                    modifier = Modifier.weight(1f).height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x9926272B)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(if (currentFrame.isDeviationWarning) FormBreakdownOrange else IronBorder))
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalArrangement = Arrangement.Center) {
                        Text("BAR PATH", color = IronTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(if (currentFrame.isDeviationWarning) "DRIFTING" else "SAFE", color = if (currentFrame.isDeviationWarning) FormBreakdownOrange else NeonLime, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            // SWIPE HINT
            Text(
                text = "SWIPE UP FOR SET HISTORY",
                modifier = Modifier.fillMaxWidth().clickable { finishAndAuditSet() }.padding(bottom = 8.dp),
                color = IronTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp
            )
        }
    }
}
