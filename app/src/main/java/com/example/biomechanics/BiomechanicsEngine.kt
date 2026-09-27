package com.example.biomechanics

import androidx.compose.ui.geometry.Offset
import com.example.data.model.RepMetric
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class BiomechanicsEngine(
    var exerciseType: ExerciseType = ExerciseType.SQUAT,
    var mediaLinesEnabled: Boolean = true,
    private val onRepCompleted: (RepMetric) -> Unit = {}
) {
    private var phase: LiftPhase = LiftPhase.IDLE
    private var repCount: Int = 0

    // Timing & Metric tracking
    private var phaseStartTimeMs: Long = System.currentTimeMillis()
    private var eccentricDurationSec: Float = 0f
    private var concentricDurationSec: Float = 0f
    private var minTargetAngleInRep: Float = 180f
    private var maxTargetAngleInRep: Float = 0f
    private var maxDeviationInRep: Float = 0f

    // Tracing
    private val fullSetBarPath = mutableListOf<Offset>()
    private val currentRepBarPath = mutableListOf<Offset>()

    // Simulation timekeeper for demo mode
    private var simTimeSec = 0f

    fun resetSet() {
        repCount = 0
        phase = LiftPhase.IDLE
        fullSetBarPath.clear()
        currentRepBarPath.clear()
        phaseStartTimeMs = System.currentTimeMillis()
        minTargetAngleInRep = 180f
        maxTargetAngleInRep = 0f
        maxDeviationInRep = 0f
    }

    fun setExercise(type: ExerciseType) {
        exerciseType = type
        resetSet()
    }

    /**
     * Calculates joint angle in degrees using vector dot product between points A, B, and C (vertex at B).
     */
    fun calculateAngle(pA: PoseLandmark, pB: PoseLandmark, pC: PoseLandmark): Float {
        val v1x = pA.x - pB.x
        val v1y = pA.y - pB.y
        val v2x = pC.x - pB.x
        val v2y = pC.y - pB.y

        val dot = v1x * v2x + v1y * v2y
        val mag1 = sqrt(v1x * v1x + v1y * v1y)
        val mag2 = sqrt(v2x * v2x + v2y * v2y)

        if (mag1 * mag2 == 0f) return 180f
        val cosAngle = (dot / (mag1 * mag2)).coerceIn(-1f, 1f)
        return Math.toDegrees(acos(cosAngle.toDouble())).toFloat()
    }

    /**
     * Analyzes landmarks extracted from CameraX / MediaPipe.
     */
    fun processLandmarks(landmarks: List<PoseLandmark>): BiomechanicalFrame {
        if (landmarks.isEmpty()) {
            return fallbackFrame().copy(
                currentRepCount = repCount,
                barPathHistory = fullSetBarPath.toList()
            )
        }
        
        if (landmarks.size < 33) {
            return fallbackFrame()
        }

        val leftHip = landmarks[LandmarkIndices.LEFT_HIP]
        val rightHip = landmarks[LandmarkIndices.RIGHT_HIP]
        val leftKnee = landmarks[LandmarkIndices.LEFT_KNEE]
        val rightKnee = landmarks[LandmarkIndices.RIGHT_KNEE]
        val leftAnkle = landmarks[LandmarkIndices.LEFT_ANKLE]
        val rightAnkle = landmarks[LandmarkIndices.RIGHT_ANKLE]
        val leftShoulder = landmarks[LandmarkIndices.LEFT_SHOULDER]
        val rightShoulder = landmarks[LandmarkIndices.RIGHT_SHOULDER]
        val leftElbow = landmarks[LandmarkIndices.LEFT_ELBOW]
        val rightElbow = landmarks[LandmarkIndices.RIGHT_ELBOW]
        val leftWrist = landmarks[LandmarkIndices.LEFT_WRIST]
        val rightWrist = landmarks[LandmarkIndices.RIGHT_WRIST]
        val leftFootIndex = landmarks[LandmarkIndices.LEFT_FOOT_INDEX]

        // 1. Joint angles
        val kneeAngle = calculateAngle(leftHip, leftKnee, leftAnkle)
        val hipAngle = calculateAngle(leftShoulder, leftHip, leftKnee)
        val elbowAngle = calculateAngle(leftShoulder, leftElbow, leftWrist)
        val shoulderAngle = calculateAngle(leftHip, leftShoulder, leftElbow)
        val backAngle = calculateAngle(
            PoseLandmark(-1, leftHip.x, leftHip.y - 0.5f),
            leftHip,
            leftShoulder
        )

        val (primaryJointName, primaryAngle) = when (exerciseType.primaryJointName) {
            "ELBOW" -> "ELBOW" to elbowAngle
            "HIP" -> "HIP" to hipAngle
            "SHOULDER" -> "SHOULDER" to shoulderAngle
            "TORSO" -> "TORSO" to backAngle
            "ANKLE" -> "ANKLE" to kneeAngle
            else -> "KNEE" to kneeAngle
        }

        // 2. Barbell or implement position (midpoint between wrists)
        val barX = (leftWrist.x + rightWrist.x) / 2f
        val barY = (leftWrist.y + rightWrist.y) / 2f
        val barbellPos = Offset(barX, barY)

        // 3. Mid-foot or base alignment reference line
        val midfootX = (leftAnkle.x + leftFootIndex.x) / 2f

        // 4. Horizontal bar deviation calibrated to estimated centimeters
        val horizontalDiff = barX - midfootX
        val deviationCm = abs(horizontalDiff) * 140f
        val isDeviationWarning = deviationCm > 4.5f

        // 5. Update bar path trail
        fullSetBarPath.add(barbellPos)
        currentRepBarPath.add(barbellPos)
        if (fullSetBarPath.size > 250) {
            fullSetBarPath.removeAt(0)
        }

        // 6. Run Rep State Machine
        val feedback = updateRepStateMachine(
            primaryAngle = primaryAngle,
            deviationCm = deviationCm,
            isDeviationWarning = isDeviationWarning
        )

        return BiomechanicalFrame(
            exerciseType = exerciseType,
            landmarks = landmarks,
            kneeAngleDeg = kneeAngle,
            hipAngleDeg = hipAngle,
            backAngleDeg = backAngle,
            elbowAngleDeg = elbowAngle,
            shoulderAngleDeg = shoulderAngle,
            primaryJointName = primaryJointName,
            primaryJointAngleDeg = primaryAngle,
            barbellPos = barbellPos,
            midfootX = midfootX,
            horizontalDeviationCm = deviationCm,
            isDeviationWarning = isDeviationWarning,
            currentPhase = phase,
            currentRepCount = repCount,
            formFeedbackText = feedback,
            barPathHistory = fullSetBarPath.toList(),
            currentRepPath = currentRepBarPath.toList(),
            isProGatedFeatureActive = true,
            mediaLinesEnabled = mediaLinesEnabled
        )
    }

    private fun updateRepStateMachine(
        primaryAngle: Float,
        deviationCm: Float,
        isDeviationWarning: Boolean
    ): String {
        val now = System.currentTimeMillis()

        if (deviationCm > maxDeviationInRep) {
            maxDeviationInRep = deviationCm
        }

        var feedback = when {
            isDeviationWarning -> "⚠️ MEDIAL DRIFT: ALIGN OVER BASE LINE"
            phase == LiftPhase.INFLECTION -> "✓ TARGET DEPTH REACHED — DRIVE UP"
            phase == LiftPhase.CONCENTRIC -> "EXPLOSIVE CONCENTRIC POWER"
            phase == LiftPhase.ECCENTRIC -> "CONTROLLED ECCENTRIC TEMPO"
            else -> "LOCKED IN • READY FOR NEXT REP"
        }

        val targetInflection = exerciseType.targetInflectionAngle

        // State machine pattern based on whether target is reached by flexion (angle drops) or extension (angle increases)
        val isFlexionInflection = targetInflection < 120f

        when (phase) {
            LiftPhase.IDLE, LiftPhase.LOCKOUT -> {
                val started = if (isFlexionInflection) {
                    primaryAngle < 155f
                } else {
                    primaryAngle > 95f
                }

                if (started) {
                    phase = LiftPhase.ECCENTRIC
                    phaseStartTimeMs = now
                    minTargetAngleInRep = primaryAngle
                    maxTargetAngleInRep = primaryAngle
                    maxDeviationInRep = deviationCm
                    currentRepBarPath.clear()
                }
            }
            LiftPhase.ECCENTRIC -> {
                if (isFlexionInflection) {
                    if (primaryAngle < minTargetAngleInRep) minTargetAngleInRep = primaryAngle
                    if (primaryAngle <= targetInflection + 5f) {
                        phase = LiftPhase.INFLECTION
                        eccentricDurationSec = ((now - phaseStartTimeMs) / 1000f).coerceAtLeast(0.4f)
                        phaseStartTimeMs = now
                    } else if (primaryAngle > minTargetAngleInRep + 8f) {
                        phase = LiftPhase.CONCENTRIC
                        eccentricDurationSec = ((now - phaseStartTimeMs) / 1000f).coerceAtLeast(0.4f)
                        phaseStartTimeMs = now
                    }
                } else {
                    if (primaryAngle > maxTargetAngleInRep) maxTargetAngleInRep = primaryAngle
                    if (primaryAngle >= targetInflection - 5f) {
                        phase = LiftPhase.INFLECTION
                        eccentricDurationSec = ((now - phaseStartTimeMs) / 1000f).coerceAtLeast(0.4f)
                        phaseStartTimeMs = now
                    } else if (primaryAngle < maxTargetAngleInRep - 8f) {
                        phase = LiftPhase.CONCENTRIC
                        eccentricDurationSec = ((now - phaseStartTimeMs) / 1000f).coerceAtLeast(0.4f)
                        phaseStartTimeMs = now
                    }
                }
            }
            LiftPhase.INFLECTION -> {
                if (isFlexionInflection) {
                    if (primaryAngle < minTargetAngleInRep) minTargetAngleInRep = primaryAngle
                    if (primaryAngle > minTargetAngleInRep + 6f) {
                        phase = LiftPhase.CONCENTRIC
                        phaseStartTimeMs = now
                    }
                } else {
                    if (primaryAngle > maxTargetAngleInRep) maxTargetAngleInRep = primaryAngle
                    if (primaryAngle < maxTargetAngleInRep - 6f) {
                        phase = LiftPhase.CONCENTRIC
                        phaseStartTimeMs = now
                    }
                }
            }
            LiftPhase.CONCENTRIC -> {
                val repFinished = if (isFlexionInflection) {
                    primaryAngle > 165f
                } else {
                    primaryAngle < 85f || primaryAngle > 170f
                }

                if (repFinished) {
                    phase = LiftPhase.LOCKOUT
                    concentricDurationSec = ((now - phaseStartTimeMs) / 1000f).coerceAtLeast(0.4f)
                    repCount++

                    val clean = maxDeviationInRep <= 4.5f
                    val verdict = when {
                        clean -> "EXCELLENT BIOMECHANICS"
                        maxDeviationInRep > 4.5f -> "HORIZONTAL BAR DRIFT"
                        else -> "TARGET DEPTH MISSED"
                    }
                    val score = (100 - (maxDeviationInRep * 3f)).toInt().coerceIn(70, 99)

                    val metric = RepMetric(
                        setId = 0,
                        repIndex = repCount,
                        minKneeAngleDeg = if (isFlexionInflection) minTargetAngleInRep else primaryAngle,
                        minHipAngleDeg = primaryAngle,
                        maxBarDeviationCm = maxDeviationInRep,
                        eccentricDurationSec = eccentricDurationSec,
                        concentricDurationSec = concentricDurationSec,
                        formScore = score,
                        formBreakdownWarning = verdict,
                        isCleanRep = clean
                    )
                    onRepCompleted(metric)
                    feedback = "✓ REP $repCount COMPLETED ($verdict)"
                }
            }
        }

        return feedback
    }

    /**
     * Generates simulated biomechanics for demo, testing, and judge walkthroughs.
     * Produces smooth 33-point realistic human skeleton kinematics for every muscle division!
     */
    fun stepSimulatedLifter(deltaTimeSec: Float = 0.033f): BiomechanicalFrame {
        simTimeSec += deltaTimeSec
        val cycleDuration = if (exerciseType.isHoldOrTimer) 5.0f else 3.4f
        val t = (simTimeSec % cycleDuration) / cycleDuration
        val depthFactor = (0.5f - 0.5f * cos(t * 2.0 * Math.PI)).toFloat()
        val driftFactor = sin(t * Math.PI).toFloat() * 0.018f

        val hipY: Float
        val kneeY: Float
        val ankleX = 0.48f
        val ankleY = 0.88f
        val footIndexX = 0.53f
        val footIndexY = 0.90f
        val kneeX: Float
        val hipX: Float
        val shoulderX: Float
        val shoulderY: Float
        val barX: Float
        val barY: Float

        when (exerciseType.division) {
            MuscleDivision.CHEST -> {
                // Bench press / Push-ups: lifter horizontal or upright
                shoulderX = 0.50f
                shoulderY = 0.42f
                hipX = 0.49f
                hipY = 0.65f
                kneeX = 0.47f
                kneeY = 0.80f
                barX = 0.50f + driftFactor
                barY = 0.32f + (depthFactor * 0.18f) // Bar descends towards chest
            }
            MuscleDivision.BACK -> {
                if (exerciseType == ExerciseType.PULL_UPS) {
                    // Pulling up
                    shoulderX = 0.50f
                    shoulderY = 0.45f - (depthFactor * 0.16f)
                    hipX = 0.50f
                    hipY = 0.65f - (depthFactor * 0.16f)
                    kneeX = 0.49f
                    kneeY = 0.80f - (depthFactor * 0.14f)
                    barX = 0.50f
                    barY = 0.22f
                } else {
                    // Deadlift / Row
                    hipY = 0.55f + (depthFactor * 0.14f)
                    kneeY = 0.70f + (depthFactor * 0.04f)
                    kneeX = 0.46f - (depthFactor * 0.03f)
                    hipX = 0.48f - (depthFactor * 0.06f)
                    shoulderX = 0.50f + (depthFactor * 0.04f)
                    shoulderY = 0.34f + (depthFactor * 0.14f)
                    barX = 0.50f + (depthFactor * 0.015f) + driftFactor
                    barY = 0.46f + (depthFactor * 0.30f)
                }
            }
            MuscleDivision.BICEPS, MuscleDivision.TRICEPS -> {
                // Bicep / Tricep Curls: Upper body stationary, forearm swings
                shoulderX = 0.50f
                shoulderY = 0.32f
                hipX = 0.50f
                hipY = 0.56f
                kneeX = 0.49f
                kneeY = 0.74f
                val armFactor = if (exerciseType == ExerciseType.BARBELL_CURL || exerciseType == ExerciseType.HAMMER_CURL || exerciseType == ExerciseType.DUMBBELL_CURL) {
                    1f - depthFactor // Hand rises on curl
                } else {
                    depthFactor // Pushdown
                }
                barX = 0.53f + driftFactor
                barY = 0.36f + (armFactor * 0.22f)
            }
            MuscleDivision.SHOULDERS -> {
                // Overhead press: Bar pushes up overhead
                shoulderX = 0.50f
                shoulderY = 0.35f
                hipX = 0.50f
                hipY = 0.58f
                kneeX = 0.49f
                kneeY = 0.75f
                barX = 0.50f + driftFactor
                barY = 0.34f - (depthFactor * 0.18f) // Presses up above head
            }
            MuscleDivision.CORE -> {
                // Plank or leg raises
                shoulderX = 0.48f
                shoulderY = 0.45f
                hipX = 0.50f
                hipY = 0.52f + (depthFactor * 0.02f)
                kneeX = 0.51f
                kneeY = 0.68f + (depthFactor * 0.02f)
                barX = 0.50f
                barY = 0.50f
            }
            else -> {
                // LEGS (Squat, Lunges, RDL, etc.)
                hipY = 0.52f + (depthFactor * 0.18f)
                kneeY = 0.68f + (depthFactor * 0.05f)
                kneeX = 0.46f - (depthFactor * 0.04f)
                hipX = 0.49f - (depthFactor * 0.08f)
                shoulderX = 0.50f + (depthFactor * 0.06f)
                shoulderY = 0.30f + (depthFactor * 0.17f)
                barX = shoulderX + 0.01f + driftFactor
                barY = shoulderY + 0.02f
            }
        }

        // Build 33 landmarks
        val landmarks = mutableListOf<PoseLandmark>()
        for (i in 0..32) {
            when (i) {
                LandmarkIndices.NOSE -> landmarks.add(PoseLandmark(i, shoulderX, shoulderY - 0.12f))
                LandmarkIndices.LEFT_SHOULDER -> landmarks.add(PoseLandmark(i, shoulderX - 0.03f, shoulderY))
                LandmarkIndices.RIGHT_SHOULDER -> landmarks.add(PoseLandmark(i, shoulderX + 0.03f, shoulderY))
                LandmarkIndices.LEFT_ELBOW -> landmarks.add(PoseLandmark(i, shoulderX - 0.05f, (shoulderY + barY) / 2f))
                LandmarkIndices.RIGHT_ELBOW -> landmarks.add(PoseLandmark(i, shoulderX + 0.05f, (shoulderY + barY) / 2f))
                LandmarkIndices.LEFT_WRIST -> landmarks.add(PoseLandmark(i, barX - 0.05f, barY))
                LandmarkIndices.RIGHT_WRIST -> landmarks.add(PoseLandmark(i, barX + 0.05f, barY))
                LandmarkIndices.LEFT_HIP -> landmarks.add(PoseLandmark(i, hipX - 0.03f, hipY))
                LandmarkIndices.RIGHT_HIP -> landmarks.add(PoseLandmark(i, hipX + 0.03f, hipY))
                LandmarkIndices.LEFT_KNEE -> landmarks.add(PoseLandmark(i, kneeX - 0.02f, kneeY))
                LandmarkIndices.RIGHT_KNEE -> landmarks.add(PoseLandmark(i, kneeX + 0.02f, kneeY))
                LandmarkIndices.LEFT_ANKLE -> landmarks.add(PoseLandmark(i, ankleX, ankleY))
                LandmarkIndices.RIGHT_ANKLE -> landmarks.add(PoseLandmark(i, ankleX + 0.04f, ankleY))
                LandmarkIndices.LEFT_HEEL -> landmarks.add(PoseLandmark(i, ankleX - 0.03f, ankleY + 0.02f))
                LandmarkIndices.RIGHT_HEEL -> landmarks.add(PoseLandmark(i, ankleX + 0.01f, ankleY + 0.02f))
                LandmarkIndices.LEFT_FOOT_INDEX -> landmarks.add(PoseLandmark(i, footIndexX, footIndexY))
                LandmarkIndices.RIGHT_FOOT_INDEX -> landmarks.add(PoseLandmark(i, footIndexX + 0.04f, footIndexY))
                else -> landmarks.add(PoseLandmark(i, 0.5f, 0.5f, visibility = 0.5f))
            }
        }

        return processLandmarks(landmarks)
    }

    private fun fallbackFrame(): BiomechanicalFrame {
        return BiomechanicalFrame(
            exerciseType = exerciseType,
            landmarks = emptyList(),
            kneeAngleDeg = 175f,
            hipAngleDeg = 170f,
            backAngleDeg = 15f,
            elbowAngleDeg = 170f,
            shoulderAngleDeg = 90f,
            primaryJointName = exerciseType.primaryJointName,
            primaryJointAngleDeg = 175f,
            barbellPos = Offset(0.5f, 0.4f),
            midfootX = 0.5f,
            horizontalDeviationCm = 0f,
            isDeviationWarning = false,
            currentPhase = LiftPhase.IDLE,
            currentRepCount = repCount,
            formFeedbackText = "ALIGN IN CAMERA FRAME",
            barPathHistory = emptyList(),
            currentRepPath = emptyList(),
            isProGatedFeatureActive = true,
            mediaLinesEnabled = mediaLinesEnabled
        )
    }
}
