package com.example.biomechanics

import androidx.compose.ui.geometry.Offset

data class PoseLandmark(
    val id: Int,
    val x: Float, // Normalized 0.0 .. 1.0
    val y: Float, // Normalized 0.0 .. 1.0
    val z: Float = 0f,
    val visibility: Float = 1.0f
)

enum class MuscleDivision(val displayName: String, val badgeColorHex: Long) {
    FULL_BODY("FULL BODY", 0xFFE2E8F0),
    CHEST("CHEST", 0xFFF59E0B),
    BACK("BACK", 0xFF38BDF8),
    SHOULDERS("SHOULDERS", 0xFFEC4899),
    BICEPS("BICEPS", 0xFFA855F7),
    TRICEPS("TRICEPS", 0xFF8B5CF6),
    LEGS("LEGS", 0xFF10B981),
    GLUTES("GLUTES", 0xFF059669),
    CALVES("CALVES", 0xFF34D399),
    CORE("CORE", 0xFF06B6D4)
}

enum class EquipmentType {
    BARBELL,
    DUMBBELL,
    MACHINE,
    CABLE,
    BODYWEIGHT,
    SMITH_MACHINE,
    EZ_BAR
}

enum class ExerciseType(
    val displayName: String,
    val division: MuscleDivision,
    val equipment: EquipmentType,
    val cueText: String,
    val primaryJointName: String,
    val targetInflectionAngle: Float,
    val defaultWeightKg: Float,
    val isHoldOrTimer: Boolean = false,
    val isBodyweight: Boolean = false
) {
    // CHEST
    BENCH_PRESS("Barbell Bench Press", MuscleDivision.CHEST, EquipmentType.BARBELL, "Elbows tucked 45°-70°, bar touches sternum", "ELBOW", 85f, 60f),
    INCLINE_PRESS("Incline Barbell Bench Press", MuscleDivision.CHEST, EquipmentType.BARBELL, "Clavicular drive, keep wrists stacked", "ELBOW", 85f, 50f),
    DECLINE_PRESS("Decline Bench Press", MuscleDivision.CHEST, EquipmentType.BARBELL, "Lower chest drive", "ELBOW", 85f, 60f),
    DUMBBELL_BENCH("Dumbbell Bench Press", MuscleDivision.CHEST, EquipmentType.DUMBBELL, "Deep stretch, control descent", "ELBOW", 85f, 20f),
    INCLINE_DUMBBELL_PRESS("Incline Dumbbell Press", MuscleDivision.CHEST, EquipmentType.DUMBBELL, "Upper chest focus", "ELBOW", 85f, 18f),
    CHEST_PRESS_MACHINE("Chest Press Machine", MuscleDivision.CHEST, EquipmentType.MACHINE, "Squeeze pecs at lockout", "ELBOW", 90f, 40f),
    INCLINE_CHEST_PRESS_MACHINE("Incline Chest Press Machine", MuscleDivision.CHEST, EquipmentType.MACHINE, "Drive up and together", "ELBOW", 90f, 35f),
    PEC_DECK("Pec Deck / Chest Fly Machine", MuscleDivision.CHEST, EquipmentType.MACHINE, "Hug the tree, peak squeeze", "ELBOW", 130f, 30f),
    CABLE_CHEST_FLY("Cable Chest Fly", MuscleDivision.CHEST, EquipmentType.CABLE, "Constant tension, slight elbow bend", "ELBOW", 130f, 15f),
    PUSH_UPS("Push-Ups", MuscleDivision.CHEST, EquipmentType.BODYWEIGHT, "Rigid body line, chest to floor", "ELBOW", 80f, 0f, isBodyweight = true),

    // BACK
    LAT_PULLDOWN("Lat Pulldown", MuscleDivision.BACK, EquipmentType.CABLE, "Pull to upper chest, retract scaps", "ELBOW", 70f, 45f),
    SEATED_CABLE_ROW("Seated Cable Row", MuscleDivision.BACK, EquipmentType.CABLE, "Neutral spine, drive elbows to hips", "ELBOW", 80f, 50f),
    BARBELL_ROW("Barbell Bent-Over Row", MuscleDivision.BACK, EquipmentType.BARBELL, "Torso 45°, pull to belly button", "ELBOW", 75f, 60f),
    DUMBBELL_ROW("One-Arm Dumbbell Row", MuscleDivision.BACK, EquipmentType.DUMBBELL, "Lawnmower pull, stretch at bottom", "ELBOW", 75f, 25f),
    MACHINE_ROW("Machine Row", MuscleDivision.BACK, EquipmentType.MACHINE, "Chest supported, squeeze lats", "ELBOW", 75f, 40f),
    ASSISTED_PULL_UP("Assisted Pull-Up", MuscleDivision.BACK, EquipmentType.MACHINE, "Control eccentric, full stretch", "ELBOW", 65f, -20f),
    PULL_UPS("Pull-Ups", MuscleDivision.BACK, EquipmentType.BODYWEIGHT, "Dead hang to chin over bar", "ELBOW", 65f, 0f, isBodyweight = true),
    CHIN_UPS("Chin-Ups", MuscleDivision.BACK, EquipmentType.BODYWEIGHT, "Underhand grip, bicep/lat focus", "ELBOW", 60f, 0f, isBodyweight = true),
    DEADLIFT("Deadlift", MuscleDivision.BACK, EquipmentType.BARBELL, "Drive floor away, lock hips", "HIP", 70f, 100f),
    STRAIGHT_ARM_PULLDOWN("Straight-Arm Pulldown", MuscleDivision.BACK, EquipmentType.CABLE, "Keep arms straight, isolate lats", "SHOULDER", 60f, 20f),

    // SHOULDERS
    OVERHEAD_PRESS("Barbell Overhead Press", MuscleDivision.SHOULDERS, EquipmentType.BARBELL, "Bar clears chin, vertical lockout", "SHOULDER", 170f, 40f),
    DUMBBELL_SHOULDER_PRESS("Dumbbell Shoulder Press", MuscleDivision.SHOULDERS, EquipmentType.DUMBBELL, "90° to lockout, control descent", "SHOULDER", 165f, 16f),
    SHOULDER_PRESS_MACHINE("Shoulder Press Machine", MuscleDivision.SHOULDERS, EquipmentType.MACHINE, "Constant tension", "SHOULDER", 165f, 30f),
    LATERAL_RAISE("Lateral Raise", MuscleDivision.SHOULDERS, EquipmentType.DUMBBELL, "Raise to parallel, lead with elbows", "SHOULDER", 90f, 8f),
    FRONT_RAISE("Dumbbell Front Raise", MuscleDivision.SHOULDERS, EquipmentType.DUMBBELL, "Raise to eye level", "SHOULDER", 90f, 8f),
    CABLE_LATERAL_RAISE("Cable Lateral Raise", MuscleDivision.SHOULDERS, EquipmentType.CABLE, "Constant tension on side delt", "SHOULDER", 90f, 5f),
    REVERSE_PEC_DECK("Reverse Pec Deck", MuscleDivision.SHOULDERS, EquipmentType.MACHINE, "Isolate rear delts", "SHOULDER", 130f, 20f),
    FACE_PULL("Face Pull", MuscleDivision.SHOULDERS, EquipmentType.CABLE, "Pull to forehead, externally rotate", "ELBOW", 70f, 15f),

    // BICEPS
    BARBELL_CURL("Barbell Curl", MuscleDivision.BICEPS, EquipmentType.BARBELL, "Pin elbows, full flexion", "ELBOW", 50f, 20f),
    EZ_BAR_CURL("EZ-Bar Curl", MuscleDivision.BICEPS, EquipmentType.EZ_BAR, "Comfortable grip, squeeze peak", "ELBOW", 50f, 20f),
    DUMBBELL_CURL("Dumbbell Curl", MuscleDivision.BICEPS, EquipmentType.DUMBBELL, "Supinate at top", "ELBOW", 50f, 12f),
    HAMMER_CURL("Hammer Curl", MuscleDivision.BICEPS, EquipmentType.DUMBBELL, "Neutral grip, brachialis focus", "ELBOW", 55f, 14f),
    PREACHER_CURL("Preacher Curl", MuscleDivision.BICEPS, EquipmentType.EZ_BAR, "Arm supported, full stretch", "ELBOW", 50f, 15f),
    PREACHER_CURL_MACHINE("Preacher Curl Machine", MuscleDivision.BICEPS, EquipmentType.MACHINE, "Constant tension peak", "ELBOW", 50f, 20f),
    CABLE_CURL("Cable Curl", MuscleDivision.BICEPS, EquipmentType.CABLE, "Smooth pull, squeeze", "ELBOW", 50f, 15f),
    INCLINE_DUMBBELL_CURL("Incline Dumbbell Curl", MuscleDivision.BICEPS, EquipmentType.DUMBBELL, "Long head stretch", "ELBOW", 50f, 10f),

    // TRICEPS
    CABLE_TRICEP_PUSHDOWN("Cable Tricep Pushdown", MuscleDivision.TRICEPS, EquipmentType.CABLE, "Stationary upper arms, lockout", "ELBOW", 170f, 20f),
    ROPE_PUSHDOWN("Rope Pushdown", MuscleDivision.TRICEPS, EquipmentType.CABLE, "Flare rope at bottom", "ELBOW", 170f, 15f),
    SKULL_CRUSHERS("Skull Crushers", MuscleDivision.TRICEPS, EquipmentType.EZ_BAR, "Lower to forehead, extend up", "ELBOW", 160f, 20f),
    CLOSE_GRIP_BENCH("Close-Grip Bench Press", MuscleDivision.TRICEPS, EquipmentType.BARBELL, "Shoulder-width grip, tuck elbows", "ELBOW", 80f, 50f),
    OVERHEAD_CABLE_EXTENSION("Overhead Cable Extension", MuscleDivision.TRICEPS, EquipmentType.CABLE, "Deep stretch, extend fully", "ELBOW", 160f, 15f),
    DUMBBELL_OVERHEAD_EXTENSION("Dumbbell Overhead Extension", MuscleDivision.TRICEPS, EquipmentType.DUMBBELL, "Both hands, control descent", "ELBOW", 160f, 16f),
    ASSISTED_DIP("Assisted Dip", MuscleDivision.TRICEPS, EquipmentType.MACHINE, "Upright torso for triceps", "ELBOW", 90f, -20f),
    BENCH_DIPS("Bench Dips", MuscleDivision.TRICEPS, EquipmentType.BODYWEIGHT, "Keep back close to bench", "ELBOW", 90f, 0f, isBodyweight = true),

    // LEGS
    SQUAT("Barbell Squat", MuscleDivision.LEGS, EquipmentType.BARBELL, "Break parallel (<90°), mid-foot balance", "KNEE", 90f, 60f),
    SMITH_MACHINE_SQUAT("Smith Machine Squat", MuscleDivision.LEGS, EquipmentType.SMITH_MACHINE, "Feet slightly forward", "KNEE", 90f, 40f),
    LEG_PRESS("Leg Press", MuscleDivision.LEGS, EquipmentType.MACHINE, "Deep bend, don't lock knees", "KNEE", 80f, 100f),
    HACK_SQUAT("Hack Squat", MuscleDivision.LEGS, EquipmentType.MACHINE, "Quad focus, deep stretch", "KNEE", 85f, 40f),
    LEG_EXTENSION("Leg Extension", MuscleDivision.LEGS, EquipmentType.MACHINE, "Full quad lockout, 2s squeeze", "KNEE", 170f, 40f),
    LEG_CURL("Leg Curl", MuscleDivision.LEGS, EquipmentType.MACHINE, "Hamstring isolation", "KNEE", 80f, 35f),
    ROMANIAN_DEADLIFT("Romanian Deadlift", MuscleDivision.LEGS, EquipmentType.BARBELL, "Hinge at hips, soft knees", "HIP", 75f, 60f),
    WALKING_LUNGES("Walking Lunges", MuscleDivision.LEGS, EquipmentType.BODYWEIGHT, "90° front knee bend", "KNEE", 90f, 0f, isBodyweight = true),
    BULGARIAN_SPLIT_SQUAT("Bulgarian Split Squat", MuscleDivision.LEGS, EquipmentType.DUMBBELL, "Rear foot elevated, deep stretch", "KNEE", 85f, 12f),
    GOBLET_SQUAT("Goblet Squat", MuscleDivision.LEGS, EquipmentType.DUMBBELL, "Hold weight at chest, upright torso", "KNEE", 90f, 20f),

    // GLUTES
    HIP_THRUST("Barbell Hip Thrust", MuscleDivision.GLUTES, EquipmentType.BARBELL, "Squeeze glutes at top, chin tucked", "HIP", 170f, 60f),
    GLUTE_BRIDGE("Glute Bridge", MuscleDivision.GLUTES, EquipmentType.BODYWEIGHT, "Drive through heels", "HIP", 170f, 0f, isBodyweight = true),
    CABLE_KICKBACK("Cable Kickback", MuscleDivision.GLUTES, EquipmentType.CABLE, "Squeeze at peak extension", "HIP", 160f, 10f),
    HIP_ABDUCTION("Hip Abduction Machine", MuscleDivision.GLUTES, EquipmentType.MACHINE, "Push knees out", "HIP", 130f, 30f),

    // CALVES
    STANDING_CALF_RAISE("Standing Calf Raise", MuscleDivision.CALVES, EquipmentType.MACHINE, "Full plantarflexion apex", "ANKLE", 130f, 40f),
    SEATED_CALF_RAISE("Seated Calf Raise", MuscleDivision.CALVES, EquipmentType.MACHINE, "Soleus focus", "ANKLE", 120f, 20f),
    LEG_PRESS_CALF_RAISE("Leg Press Calf Raise", MuscleDivision.CALVES, EquipmentType.MACHINE, "Toes on edge, deep stretch", "ANKLE", 130f, 60f),
    CALF_RAISE_MACHINE("Calf Raise Machine", MuscleDivision.CALVES, EquipmentType.MACHINE, "Squeeze at top", "ANKLE", 130f, 40f),

    // CORE
    PLANK("Plank", MuscleDivision.CORE, EquipmentType.BODYWEIGHT, "Neutral spine, core isometric", "TORSO", 175f, 0f, isHoldOrTimer = true, isBodyweight = true),
    CRUNCH("Crunch", MuscleDivision.CORE, EquipmentType.BODYWEIGHT, "Contract abs, lift shoulders", "TORSO", 140f, 0f, isBodyweight = true),
    CABLE_CRUNCH("Cable Crunch", MuscleDivision.CORE, EquipmentType.CABLE, "Round back, pull with abs", "TORSO", 90f, 25f),
    HANGING_LEG_RAISE("Hanging Leg Raise", MuscleDivision.CORE, EquipmentType.BODYWEIGHT, "Legs to 90°, minimal swing", "HIP", 90f, 0f, isBodyweight = true),
    KNEE_RAISE("Knee Raise", MuscleDivision.CORE, EquipmentType.BODYWEIGHT, "Drive knees to chest", "HIP", 80f, 0f, isBodyweight = true),
    AB_WHEEL("Ab Wheel Rollout", MuscleDivision.CORE, EquipmentType.BODYWEIGHT, "Anti-extension brace", "TORSO", 160f, 0f, isBodyweight = true),
    RUSSIAN_TWIST("Russian Twist", MuscleDivision.CORE, EquipmentType.BODYWEIGHT, "Controlled oblique rotations", "TORSO", 45f, 0f, isBodyweight = true),
    MOUNTAIN_CLIMBERS("Mountain Climbers", MuscleDivision.CORE, EquipmentType.BODYWEIGHT, "Pace and core stability", "HIP", 90f, 0f, isBodyweight = true)
}

enum class LiftPhase(val label: String) {
    IDLE("READY"),
    ECCENTRIC("DESCENT / STRETCH ▼"),
    INFLECTION("TARGET DEPTH ⚓"),
    CONCENTRIC("POWER DRIVE ▲"),
    LOCKOUT("REP RECORDED ✓")
}

data class BiomechanicalFrame(
    val exerciseType: ExerciseType,
    val landmarks: List<PoseLandmark>,
    val kneeAngleDeg: Float,
    val hipAngleDeg: Float,
    val backAngleDeg: Float,
    val elbowAngleDeg: Float,
    val shoulderAngleDeg: Float,
    val primaryJointName: String,
    val primaryJointAngleDeg: Float,
    val barbellPos: Offset,
    val midfootX: Float,
    val horizontalDeviationCm: Float,
    val isDeviationWarning: Boolean,
    val currentPhase: LiftPhase,
    val currentRepCount: Int,
    val formFeedbackText: String,
    val barPathHistory: List<Offset>, // Complete path for current set
    val currentRepPath: List<Offset>,   // Current rep trace
    val isProGatedFeatureActive: Boolean = true, // Unlocked for all!
    val mediaLinesEnabled: Boolean = true // Visual alignment medialines
)

object LandmarkIndices {
    const val NOSE = 0
    const val LEFT_SHOULDER = 11
    const val RIGHT_SHOULDER = 12
    const val LEFT_ELBOW = 13
    const val RIGHT_ELBOW = 14
    const val LEFT_WRIST = 15
    const val RIGHT_WRIST = 16
    const val LEFT_HIP = 23
    const val RIGHT_HIP = 24
    const val LEFT_KNEE = 25
    const val RIGHT_KNEE = 26
    const val LEFT_ANKLE = 27
    const val RIGHT_ANKLE = 28
    const val LEFT_HEEL = 29
    const val RIGHT_HEEL = 30
    const val LEFT_FOOT_INDEX = 31
    const val RIGHT_FOOT_INDEX = 32

    val SKELETON_PAIRS = listOf(
        // Torso & Hips
        11 to 12,
        11 to 23,
        12 to 24,
        23 to 24,
        // Left Arm
        11 to 13,
        13 to 15,
        // Right Arm
        12 to 14,
        14 to 16,
        // Left Leg
        23 to 25,
        25 to 27,
        27 to 29,
        27 to 31,
        // Right Leg
        24 to 26,
        26 to 28,
        28 to 30,
        28 to 32,
        // Head anchor
        0 to 11,
        0 to 12
    )
}

