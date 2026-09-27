package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// IronVision Reference System Color Palette (v3.0 Hard-Locked Spec)
// Background: Near-black charcoal (NO navy or teal tint)
val IronDarkBackground = Color(0xFF17181C)

// Surface & Card: Dark grey
val IronSurfaceDark = Color(0xFF26272B)
val IronSurfaceElevated = Color(0xFF2D2E33)
val IronSurfaceSubtle = Color(0xFF1F2024)

// Subtle 1px card outline
val IronBorder = Color(0xFF3A3B40)

// Primary Accent: Neon lime-green (The single most important visual signal)
val NeonLime = Color(0xFFD6FF3F)
val NeonLimeGlow = Color(0x33D6FF3F)
val NeonLimeDark = Color(0xFF17181C) // Text on lime button

// Backward compatibility alias for any older references
val AmberElectric = NeonLime
val AmberGlow = NeonLime

// Text Colors
val IronTextPrimary = Color(0xFFFFFFFF)
val IronTextSecondary = Color(0xFF9B9CA3)
val IronTextMuted = Color(0xFF6E7079)

// Form Breakdown Highlight: Red-orange glow gradient for flagged joint/limb
val FormBreakdownRed = Color(0xFFFF2D2D)
val FormBreakdownOrange = Color(0xFFFF5A3C)
val FormBreakdownGlow = Color(0x66FF5A3C)

// Laser colors for skeletal overlay
val LaserEmerald = NeonLime
val LaserCrimson = FormBreakdownOrange
val LaserCrimsonBg = Color(0x33FF5A3C)
val CyanGlow = Color(0xFFE2E8F0)
val CyanNeon = NeonLime
