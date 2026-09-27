package com.example.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.TransformExperimental
import androidx.camera.view.transform.CoordinateTransform
import androidx.camera.view.transform.ImageProxyTransformFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.biomechanics.PoseAnalyzer
import com.example.biomechanics.PoseLandmark as IronPoseLandmark
import com.google.mlkit.vision.common.InputImage

@OptIn(TransformExperimental::class)
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    isFrontCamera: Boolean = false,
    onPoseLandmarks: (List<IronPoseLandmark>) -> Unit = {},
    onCameraAvailableChanged: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val hasPermission = remember(context) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
        }
    }

    val poseAnalyzer = remember { PoseAnalyzer() }

    LaunchedEffect(isFrontCamera) {
        cameraController.cameraSelector = if (isFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    LaunchedEffect(cameraController, previewViewRef) {
        val previewView = previewViewRef ?: return@LaunchedEffect
        
        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(context),
            object : ImageAnalysis.Analyzer {
                @OptIn(ExperimentalGetImage::class)
                override fun analyze(imageProxy: ImageProxy) {
                    val mediaImage = imageProxy.image ?: return
                    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    
                    poseAnalyzer.detector.process(inputImage)
                        .addOnSuccessListener { pose ->
                            if (pose.allPoseLandmarks.isEmpty()) {
                                onPoseLandmarks(emptyList())
                                return@addOnSuccessListener
                            }

                            // Create transformation from Image buffer to PreviewView coordinates
                            val transform = try {
                                val factory = ImageProxyTransformFactory()
                                factory.isUsingRotationDegrees = true
                                val source = factory.getOutputTransform(imageProxy)
                                val target = previewView.outputTransform
                                if (target != null) {
                                    CoordinateTransform(source, target)
                                } else null
                            } catch (e: Exception) {
                                null
                            }

                            val matrix = Matrix()
                            transform?.transform(matrix)

                            val landmarks = pose.allPoseLandmarks.map { mlLandmark ->
                                val point = floatArrayOf(mlLandmark.position.x, mlLandmark.position.y)
                                matrix.mapPoints(point)
                                
                                IronPoseLandmark(
                                    id = mlLandmark.landmarkType,
                                    x = point[0] / previewView.width,
                                    y = point[1] / previewView.height,
                                    z = 0f,
                                    visibility = mlLandmark.inFrameLikelihood
                                )
                            }
                            onPoseLandmarks(landmarks)
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                }
            }
        )
        
        cameraController.bindToLifecycle(lifecycleOwner)
        onCameraAvailableChanged(true)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1115))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    controller = cameraController
                    previewViewRef = this
                }
            }
        )

        if (!hasPermission) {
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { /* Handle result */ }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC0D0E12))
                    .clickable { launcher.launch(Manifest.permission.CAMERA) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CAMERA ACCESS REQUIRED\n(Tap to Grant Permission)",
                    color = Color(0xFFA0A5B5),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
