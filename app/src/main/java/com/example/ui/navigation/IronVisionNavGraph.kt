package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.billing.RevenueCatManager
import com.example.biomechanics.ExerciseType
import com.example.biomechanics.MuscleDivision
import com.example.data.WorkoutRepository
import com.example.ui.screens.CameraTrackingScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExercisesScreen
import com.example.ui.screens.LegalPolicyScreen
import com.example.ui.screens.OnboardingPermissionScreen
import com.example.ui.screens.PaywallScreen
import com.example.ui.screens.PostSetReviewScreen
import com.example.ui.screens.RegistrationScreen
import com.example.ui.screens.SessionHistoryScreen
import com.example.ui.screens.WinterArcDashboard
import com.example.ui.theme.IronDarkBackground

object Destinations {
    const val DASHBOARD = "dashboard"
    const val REGISTRATION = "registration"
    const val EXERCISES = "exercises?division={division}"
    const val ONBOARDING = "onboarding"
    const val TRACKING = "tracking?exercise={exercise}"
    const val POST_SET_REVIEW = "post_set_review/{sessionId}"
    const val HISTORY = "history"
    const val PAYWALL = "paywall"
    const val LEGAL = "legal"
    const val WINTER_ARC = "winter_arc"

    fun exercisesRoute(division: MuscleDivision = MuscleDivision.FULL_BODY) = "exercises?division=${division.name}"
    fun trackingRoute(exercise: ExerciseType = ExerciseType.SQUAT) = "tracking?exercise=${exercise.name}"
    fun postSetReviewRoute(sessionId: Long) = "post_set_review/$sessionId"
}

@Composable
fun IronVisionNavGraph(
    navController: NavHostController,
    repository: WorkoutRepository,
    revenueCatManager: RevenueCatManager,
    hasCameraPermission: Boolean,
    modifier: Modifier = Modifier
) {
    var isCheckingRegistration by remember { mutableStateOf(true) }
    var userIsRegistered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userIsRegistered = repository.isUserRegistered()
        isCheckingRegistration = false
    }

    if (isCheckingRegistration) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(IronDarkBackground)
        )
        return
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("ironvision_prefs", android.content.Context.MODE_PRIVATE) }
    val policyAccepted = remember { prefs.getBoolean("policy_accepted", false) }

    val startDest = if (!policyAccepted) Destinations.LEGAL else if (userIsRegistered) Destinations.DASHBOARD else Destinations.REGISTRATION

    NavHost(
        navController = navController,
        startDestination = startDest,
        modifier = modifier
    ) {
        composable(Destinations.LEGAL) {
            LegalPolicyScreen(
                onAccept = {
                    prefs.edit().putBoolean("policy_accepted", true).apply()
                    navController.navigate(Destinations.REGISTRATION) {
                        popUpTo(Destinations.LEGAL) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinations.REGISTRATION) {
            RegistrationScreen(
                repository = repository,
                onRegistrationCompleted = {
                    navController.navigate(Destinations.DASHBOARD) {
                        popUpTo(Destinations.REGISTRATION) { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate(Destinations.DASHBOARD) {
                        popUpTo(Destinations.REGISTRATION) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinations.DASHBOARD) {
            DashboardScreen(
                repository = repository,
                revenueCatManager = revenueCatManager,
                onSelectExercise = { exercise ->
                    navController.navigate(Destinations.trackingRoute(exercise))
                },
                onNavigateExercises = { division ->
                    navController.navigate(Destinations.exercisesRoute(division))
                },
                onNavigateHistory = {
                    navController.navigate(Destinations.HISTORY)
                },
                onNavigatePaywall = {
                    navController.navigate(Destinations.PAYWALL)
                },
                onNavigateRegister = {
                    navController.navigate(Destinations.REGISTRATION)
                },
                onNavigateWinterArc = {
                    navController.navigate(Destinations.WINTER_ARC)
                }
            )
        }

        composable(Destinations.WINTER_ARC) {
            WinterArcDashboard(
                onNavigateExercise = { exercise ->
                    navController.navigate(Destinations.trackingRoute(exercise))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Destinations.EXERCISES,
            arguments = listOf(
                navArgument("division") {
                    type = NavType.StringType
                    defaultValue = MuscleDivision.FULL_BODY.name
                }
            )
        ) { backStackEntry ->
            val divName = backStackEntry.arguments?.getString("division") ?: MuscleDivision.FULL_BODY.name
            val initialDivision = try {
                MuscleDivision.valueOf(divName)
            } catch (e: Exception) {
                MuscleDivision.FULL_BODY
            }

            ExercisesScreen(
                initialDivision = initialDivision,
                onSelectExercise = { exercise ->
                    navController.navigate(Destinations.trackingRoute(exercise))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destinations.ONBOARDING) {
            OnboardingPermissionScreen(
                onPermissionGranted = {
                    navController.navigate(Destinations.DASHBOARD) {
                        popUpTo(Destinations.ONBOARDING) { inclusive = true }
                    }
                },
                onEnterDemoMode = {
                    navController.navigate(Destinations.DASHBOARD) {
                        popUpTo(Destinations.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Destinations.TRACKING,
            arguments = listOf(
                navArgument("exercise") {
                    type = NavType.StringType
                    defaultValue = ExerciseType.SQUAT.name
                }
            )
        ) { backStackEntry ->
            val exerciseName = backStackEntry.arguments?.getString("exercise") ?: ExerciseType.SQUAT.name
            val exerciseType = try {
                ExerciseType.valueOf(exerciseName)
            } catch (e: Exception) {
                ExerciseType.SQUAT
            }

            CameraTrackingScreen(
                repository = repository,
                initialExercise = exerciseType,
                onFinishSet = { sessionId ->
                    navController.navigate(Destinations.postSetReviewRoute(sessionId))
                },
                onNavigateHistory = {
                    navController.navigate(Destinations.HISTORY)
                },
                onNavigatePaywall = {
                    navController.navigate(Destinations.PAYWALL)
                },
                onBackToDashboard = {
                    navController.navigate(Destinations.DASHBOARD) {
                        popUpTo(Destinations.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Destinations.POST_SET_REVIEW,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 1L
            PostSetReviewScreen(
                sessionId = sessionId,
                repository = repository,
                revenueCatManager = revenueCatManager,
                onBack = {
                    navController.navigate(Destinations.DASHBOARD) {
                        popUpTo(Destinations.DASHBOARD) { inclusive = true }
                    }
                },
                onOpenPaywall = {
                    navController.navigate(Destinations.PAYWALL)
                }
            )
        }

        composable(Destinations.HISTORY) {
            SessionHistoryScreen(
                repository = repository,
                onSessionSelected = { sessionId ->
                    navController.navigate(Destinations.postSetReviewRoute(sessionId))
                },
                onStartNewWorkout = {
                    navController.navigate(Destinations.DASHBOARD)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destinations.PAYWALL) {
            PaywallScreen(
                revenueCatManager = revenueCatManager,
                onDismiss = {
                    navController.popBackStack()
                }
            )
        }
    }
}

