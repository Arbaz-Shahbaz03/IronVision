package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun LegalPolicyScreen(
    onAccept: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Terms of Service & Privacy Policy",
                    color = IronTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Last updated: September 2026",
                    color = IronTextSecondary,
                    fontSize = 12.sp
                )
                
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(IronSurfaceDark, RoundedCornerShape(16.dp))
                        .border(1.dp, IronBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        PolicySection(
                            title = "1. Intellectual Property & Research Protection",
                            content = "The IronVision concept, source code, UI/UX design, biomechanical algorithms, and rep-counting engines are proprietary intellectual property. " +
                                    "Strictly prohibited: Unauthorized copying, replication, reverse engineering, or usage of this concept or code in " +
                                    "Final Year Projects (FYP), academic research papers, commercial products, or competing applications without explicit written permission and licensing."
                        )
                        PolicySection(
                            title = "2. On-Device Privacy",
                            content = "IronVision operates 100% on-device. Camera frames are processed in real-time for pose estimation and are never recorded, " +
                                    "stored in cloud servers, or transmitted to third parties. Your workout telemetry remains strictly private on your device."
                        )
                        PolicySection(
                            title = "3. Limitation of Liability",
                            content = "IronVision provides real-time form feedback for informational and coaching purposes only. It does not replace professional medical " +
                                    "or physical training advice. Use at your own risk; always maintain proper form and safety precautions during heavy lifts."
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAccept,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonLime,
                    contentColor = NeonLimeDark
                )
            ) {
                Text(
                    text = "ACCEPT & CONTINUE",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            color = NeonLime,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = content,
            color = IronTextSecondary,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}
