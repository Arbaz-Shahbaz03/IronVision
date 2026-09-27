package com.example.winterarc

import com.example.biomechanics.ExerciseType

data class WinterArcWorkout(
    val dayName: String,
    val splitName: String, // e.g., "Push", "Pull"
    val exercises: List<WinterArcExercise>
)

data class WinterArcExercise(
    val exerciseType: ExerciseType,
    val targetSets: Int,
    val targetReps: String // e.g. "8-10"
)

object WinterArcProgram {
    val durationWeeks = 8

    fun getWorkoutForDay(dayIndex: Int): WinterArcWorkout {
        return when (dayIndex) {
            0 -> WinterArcWorkout(
                "MONDAY", "Push",
                listOf(
                    WinterArcExercise(ExerciseType.BENCH_PRESS, 3, "8-10"),
                    WinterArcExercise(ExerciseType.INCLINE_DUMBBELL_PRESS, 3, "10"),
                    WinterArcExercise(ExerciseType.SHOULDER_PRESS_MACHINE, 3, "10"),
                    WinterArcExercise(ExerciseType.LATERAL_RAISE, 3, "12-15"),
                    WinterArcExercise(ExerciseType.CABLE_TRICEP_PUSHDOWN, 3, "12")
                )
            )
            1 -> WinterArcWorkout(
                "TUESDAY", "Pull",
                listOf(
                    WinterArcExercise(ExerciseType.LAT_PULLDOWN, 3, "8-10"),
                    WinterArcExercise(ExerciseType.SEATED_CABLE_ROW, 3, "10"),
                    WinterArcExercise(ExerciseType.BARBELL_ROW, 3, "8-10"),
                    WinterArcExercise(ExerciseType.FACE_PULL, 3, "12-15"),
                    WinterArcExercise(ExerciseType.BARBELL_CURL, 3, "10")
                )
            )
            2 -> WinterArcWorkout(
                "WEDNESDAY", "Legs",
                listOf(
                    WinterArcExercise(ExerciseType.SQUAT, 4, "5-8"),
                    WinterArcExercise(ExerciseType.LEG_PRESS, 3, "10-12"),
                    WinterArcExercise(ExerciseType.ROMANIAN_DEADLIFT, 3, "8-10"),
                    WinterArcExercise(ExerciseType.LEG_EXTENSION, 3, "12-15"),
                    WinterArcExercise(ExerciseType.STANDING_CALF_RAISE, 4, "15")
                )
            )
            3 -> WinterArcWorkout(
                "THURSDAY", "Recovery / Rest",
                emptyList() // Active recovery
            )
            4 -> WinterArcWorkout(
                "FRIDAY", "Push",
                listOf(
                    WinterArcExercise(ExerciseType.INCLINE_PRESS, 3, "8-10"),
                    WinterArcExercise(ExerciseType.CHEST_PRESS_MACHINE, 3, "10"),
                    WinterArcExercise(ExerciseType.DUMBBELL_SHOULDER_PRESS, 3, "10"),
                    WinterArcExercise(ExerciseType.CABLE_LATERAL_RAISE, 3, "12-15"),
                    WinterArcExercise(ExerciseType.ROPE_PUSHDOWN, 3, "12")
                )
            )
            5 -> WinterArcWorkout(
                "SATURDAY", "Pull + Core",
                listOf(
                    WinterArcExercise(ExerciseType.PULL_UPS, 3, "Max"),
                    WinterArcExercise(ExerciseType.SEATED_CABLE_ROW, 3, "10"),
                    WinterArcExercise(ExerciseType.REVERSE_PEC_DECK, 3, "12-15"),
                    WinterArcExercise(ExerciseType.HAMMER_CURL, 3, "10-12"),
                    WinterArcExercise(ExerciseType.PLANK, 3, "60s")
                )
            )
            else -> WinterArcWorkout(
                "SUNDAY", "Rest / Recovery",
                emptyList()
            )
        }
    }
}
