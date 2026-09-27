package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IronBorder
import com.example.ui.theme.IronDarkBackground
import com.example.ui.theme.IronSurfaceDark
import com.example.ui.theme.IronTextPrimary
import com.example.ui.theme.IronTextSecondary
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonLimeDark

/**
 * Section 4.1 Onboarding Screen — Mapped strictly to Reference Image 3
 *
 * - Eyebrow label at top: "IRONVISION — FORM COACH"
 * - Large bold white headline, two lines: "Track every rep. Perfect every lift."
 * - Two side-by-side rounded photo tiles beneath headline showing lifting action cards
 * - Dot page indicator below the photos (single dot shown in neon-lime)
 * - Full-width pill-shaped button in neon-lime accent color: "ENABLE CAMERA & START"
 * - Small secondary text link below button: "Runs 100% on your device — no account needed."
 */
@Composable
fun OnboardingPermissionScreen(
    onPermissionGranted: () -> Unit,
    onEnterDemoMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            onEnterDemoMode()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(IronDarkBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP SECTION: Eyebrow + Two-Line Headline (Image 3 reference)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Small all-caps eyebrow label
                Text(
                    text = "IRONVISION — FORM COACH",
                    color = NeonLime,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Default,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Large bold white headline, two lines
                Text(
                    text = "Track every rep.\nPerfect every lift.",
                    color = IronTextPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 40.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // MIDDLE SECTION: Two side-by-side rounded photo tiles (Image 3 reference)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Left Photo Tile: Squat Biomechanics
                LiftingActionTile(
                    title = "BARBELL SQUAT",
                    subtitle = "Real-time depth & knee tracking",
                    badge = "30 FPS",
                    gradient = listOf(Color(0xFF1F232B), Color(0xFF14171E)),
                    modifier = Modifier.weight(1f)
                )

                // Right Photo Tile: Deadlift Bar Path
                LiftingActionTile(
                    title = "DEADLIFT",
                    subtitle = "Sub-cm bar drift & medial lines",
                    badge = "ON-DEVICE",
                    gradient = listOf(Color(0xFF262933), Color(0xFF181B22)),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // DOT PAGE INDICATOR (Image 3 reference)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Active Dot in Neon-Lime
                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(NeonLime)
                )
                // Inactive dots
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3A3B40))
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3A3B40))
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // BOTTOM ACTION SECTION (Image 3 reference)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Full-width pill-shaped button in Neon-Lime with bold black text
                Button(
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag("enable_camera_button"),
                    shape = RoundedCornerShape(50), // Full pill shape
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonLime,
                        contentColor = NeonLimeDark
                    )
                ) {
                    Text(
                        text = "ENABLE CAMERA & START",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Small secondary text link below button, centered, muted grey
                Text(
                    text = "Runs 100% on your device — no account needed.",
                    color = IronTextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable { onEnterDemoMode() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("skip_to_demo_text")
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

/**
 * Side-by-side action tile with rich lifting graphics and dark styling (Image 3 reference)
 */
@Composable
fun LiftingActionTile(
    title: String,
    subtitle: String,
    badge: String,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(gradient))
            .border(1.dp, IronBorder, RoundedCornerShape(24.dp))
            .padding(14.dp)
    ) {
        // Decorative geometric lines simulating visual tracking
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = NeonLime.copy(alpha = 0.15f),
                start = Offset(0f, size.height * 0.7f),
                end = Offset(size.width, size.height * 0.3f),
                strokeWidth = 2f
            )
            drawCircle(
                color = NeonLime.copy(alpha = 0.25f),
                radius = 16f,
                center = Offset(size.width * 0.6f, size.height * 0.45f)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF17181C).copy(alpha = 0.8f))
                    .border(1.dp, IronBorder, RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badge,
                    color = NeonLime,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                )
            }

            // Bottom Title & Subtitle
            Column {
                Text(
                    text = title,
                    color = IronTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = IronTextSecondary,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
