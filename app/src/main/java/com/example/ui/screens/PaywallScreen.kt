package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.billing.RevenueCatManager
import com.example.billing.SubscriptionTier
import com.example.ui.theme.IronBorder
import com.example.ui.theme.IronDarkBackground
import com.example.ui.theme.IronSurfaceDark
import com.example.ui.theme.IronTextMuted
import com.example.ui.theme.IronTextPrimary
import com.example.ui.theme.IronTextSecondary
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonLimeDark
import kotlinx.coroutines.launch

@Composable
fun PaywallScreen(
    revenueCatManager: RevenueCatManager,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val isProUnlocked by revenueCatManager.isProAccessUnlocked.collectAsState()
    val statusMessage by revenueCatManager.statusMessage.collectAsState()

    var selectedTier by remember { mutableStateOf(SubscriptionTier.ANNUAL) }
    var isPurchasing by remember { mutableStateOf(false) }

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
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dismiss button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(IronSurfaceDark)
                        .border(1.dp, IronBorder, CircleShape)
                        .testTag("close_paywall_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Brand Header with Glowing Emblem
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(NeonLime.copy(alpha = 0.15f))
                    .border(2.dp, NeonLime, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = NeonLime,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "IRONVISION PRO",
                color = IronTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "ELITE BIOMECHANICAL SUITE",
                color = NeonLime,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Pro Features List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(IronSurfaceDark)
                    .border(1.dp, IronBorder, RoundedCornerShape(24.dp))
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PaywallFeatureRow(
                    icon = Icons.Default.MotionPhotosOn,
                    title = "Ghost PR Bar-Path Overlay",
                    subtitle = "Compare real-time trajectory against personal record baseline"
                )
                PaywallFeatureRow(
                    icon = Icons.Default.Speed,
                    title = "Velocity-Based Fatigue (VBT)",
                    subtitle = "Sub-cm speed drop-off & real-time RPE threshold analysis"
                )
                PaywallFeatureRow(
                    icon = Icons.Default.Videocam,
                    title = "60 FPS Telemetry Video Export",
                    subtitle = "Bake joint angles and bar-path vector graphics into 1080p MP4"
                )
                PaywallFeatureRow(
                    icon = Icons.Default.History,
                    title = "Unlimited Kinematic History",
                    subtitle = "Longitudinal joint tracking & movement symmetry audits"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pricing Tiers (Side-by-side pill cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TierSelectionCard(
                    tier = SubscriptionTier.ANNUAL,
                    isSelected = selectedTier == SubscriptionTier.ANNUAL,
                    onSelect = { selectedTier = SubscriptionTier.ANNUAL },
                    modifier = Modifier.weight(1f)
                )

                TierSelectionCard(
                    tier = SubscriptionTier.MONTHLY,
                    isSelected = selectedTier == SubscriptionTier.MONTHLY,
                    onSelect = { selectedTier = SubscriptionTier.MONTHLY },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Purchase Button (Pill shape)
            Button(
                onClick = {
                    isPurchasing = true
                    coroutineScope.launch {
                        revenueCatManager.purchasePackage(selectedTier)
                        isPurchasing = false
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("subscribe_pro_button"),
                shape = RoundedCornerShape(50), // Full pill
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonLime,
                    contentColor = NeonLimeDark
                ),
                enabled = !isPurchasing
            ) {
                Text(
                    text = if (isProUnlocked) "PRO ACTIVE • CONTINUE" else "START 7-DAY FREE TRIAL",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Restore Purchases
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Restore Purchases",
                    color = IronTextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clickable {
                            coroutineScope.launch {
                                revenueCatManager.restorePurchases()
                            }
                        }
                        .padding(6.dp)
                        .testTag("restore_purchases_button")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "RevenueCat Entitlement: 'pro_access' • Local on-device inference • Cancel anytime",
                color = IronTextMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun PaywallFeatureRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF1F2024))
                .border(1.dp, IronBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeonLime,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                color = IronTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = IronTextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun TierSelectionCard(
    tier: SubscriptionTier,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onSelect() }
            .testTag("tier_card_${tier.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF282B33) else IronSurfaceDark
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isSelected) NeonLime else IronBorder
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (tier == SubscriptionTier.ANNUAL) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(NeonLime.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "SAVE 45%",
                        color = NeonLime,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            } else {
                Spacer(modifier = Modifier.height(18.dp))
            }

            Text(
                text = tier.title,
                color = IronTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = tier.priceFormatted,
                color = if (isSelected) NeonLime else IronTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text = tier.period,
                color = IronTextSecondary,
                fontSize = 9.sp
            )
        }
    }
}
