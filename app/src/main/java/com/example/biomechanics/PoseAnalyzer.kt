package com.example.biomechanics

import android.util.Log
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions

/**
 * 100% Offline, On-Device Pose Mapper.
 */
class PoseAnalyzer {

    // Publicly accessible detector for MlKitAnalyzer
    val detector: PoseDetector by lazy {
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
            .build()
        PoseDetection.getClient(options)
    }

    /**
     * Transforms ML Kit Pose results into normalized IronVision landmarks.
     */
    fun transformPose(pose: Pose, viewWidth: Int, viewHeight: Int): List<PoseLandmark> {
        val allLandmarks = pose.allPoseLandmarks
        if (allLandmarks.isEmpty() || viewWidth <= 0 || viewHeight <= 0) {
            return emptyList()
        }

        val result = mutableListOf<PoseLandmark>()

        for (i in 0..32) {
            val mlLandmark = pose.getPoseLandmark(i)
            if (mlLandmark != null) {
                val point = mlLandmark.position
                
                result.add(
                    PoseLandmark(
                        id = i,
                        x = point.x / viewWidth,
                        y = point.y / viewHeight,
                        z = 0f,
                        visibility = mlLandmark.inFrameLikelihood
                    )
                )
            } else {
                result.add(PoseLandmark(id = i, x = 0.5f, y = 0.5f, visibility = 0.0f))
            }
        }

        return result
    }

    fun close() {
        try {
            detector.close()
        } catch (e: Exception) {
            Log.e("PoseAnalyzer", "Error closing detector", e)
        }
    }
}
