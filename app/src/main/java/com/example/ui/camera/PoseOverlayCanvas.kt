package com.example.ui.camera

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.biomechanics.BiomechanicalFrame
import com.example.biomechanics.LandmarkIndices
import com.example.ui.theme.FormBreakdownGlow
import com.example.ui.theme.FormBreakdownOrange
import com.example.ui.theme.FormBreakdownRed
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonLimeGlow

@Composable
fun PoseOverlayCanvas(
    frame: BiomechanicalFrame,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        if (canvasWidth == 0f || canvasHeight == 0f) return@Canvas

        val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)

        // 1. Draw Bounding Box (Matching Reference Image 2 style)
        if (frame.landmarks.isNotEmpty()) {
            var minX = 1f
            var minY = 1f
            var maxX = 0f
            var maxY = 0f
            var foundAny = false

            for (lm in frame.landmarks) {
                if (lm.visibility > 0.3f) {
                    if (lm.x < minX) minX = lm.x
                    if (lm.y < minY) minY = lm.y
                    if (lm.x > maxX) maxX = lm.x
                    if (lm.y > maxY) maxY = lm.y
                    foundAny = true
                }
            }

            if (foundAny) {
                // Add some padding to the box
                val padding = 0.05f
                val left = (minX - padding).coerceIn(0f, 1f) * canvasWidth
                val top = (minY - padding).coerceIn(0f, 1f) * canvasHeight
                val right = (maxX + padding).coerceIn(0f, 1f) * canvasWidth
                val bottom = (maxY + padding).coerceIn(0f, 1f) * canvasHeight

                drawRect(
                    color = Color(0xFF38BDF8).copy(alpha = 0.8f), // Blue-ish as in reference
                    topLeft = Offset(left, top),
                    size = androidx.compose.ui.geometry.Size(right - left, bottom - top),
                    style = Stroke(width = 2f)
                )
                
                // Label "person" at top left of box
                // (Note: Drawing text in Canvas requires native canvas or specialized libraries, 
                // skipping text for now to keep it standard Compose Canvas)
            }
        }

        // 2. Vertical Medial Plumb Line (Gravity Plumb Line)
        if (frame.mediaLinesEnabled) {
            val midfootPx = frame.midfootX * canvasWidth

            drawLine(
                color = Color.White.copy(alpha = 0.45f),
                start = Offset(midfootPx, 0f),
                end = Offset(midfootPx, canvasHeight),
                strokeWidth = 2f,
                pathEffect = dashedEffect
            )
        }

        // 3. Draw Skeleton Bones (Vibrant Green like Reference Image 2)
        if (frame.landmarks.size >= 33) {
            for ((startIdx, endIdx) in LandmarkIndices.SKELETON_PAIRS) {
                val p1 = frame.landmarks[startIdx]
                val p2 = frame.landmarks[endIdx]

                if (p1.visibility > 0.4f && p2.visibility > 0.4f) {
                    val p1Px = Offset(p1.x * canvasWidth, p1.y * canvasHeight)
                    val p2Px = Offset(p2.x * canvasWidth, p2.y * canvasHeight)

                    drawLine(
                        color = Color(0xFF4ADE80), // Vibrant Green
                        start = p1Px,
                        end = p2Px,
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Draw Key Joint Nodes
            for (lm in frame.landmarks) {
                if (lm.visibility > 0.4f) {
                    val centerPx = Offset(lm.x * canvasWidth, lm.y * canvasHeight)
                    drawCircle(
                        color = Color(0xFFFACC15), // Yellow/Orange nodes
                        radius = 4f,
                        center = centerPx
                    )
                }
            }
        }

        // 4. Barbell Reticle (Neon Lime or Red-Orange when drifting)
        val barPx = Offset(frame.barbellPos.x * canvasWidth, frame.barbellPos.y * canvasHeight)
        val reticleColor = if (frame.isDeviationWarning) FormBreakdownOrange else NeonLime

        // Crosshairs
        drawLine(
            color = reticleColor,
            start = Offset(barPx.x - 18f, barPx.y),
            end = Offset(barPx.x + 18f, barPx.y),
            strokeWidth = 2.5f
        )
        drawLine(
            color = reticleColor,
            start = Offset(barPx.x, barPx.y - 18f),
            end = Offset(barPx.x, barPx.y + 18f),
            strokeWidth = 2.5f
        )
        // Outer ring & center dot
        drawCircle(
            color = reticleColor,
            radius = 14f,
            center = barPx,
            style = Stroke(width = 2f)
        )
    }
}
