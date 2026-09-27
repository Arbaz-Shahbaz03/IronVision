# Add project specific ProGuard rules here.
# Aggressive Obfuscation & IP Protection for IronVision Biomechanics Engine

-repackageclasses 'com.aistudio.ironvision.secure'
-allowaccessmodification

# Keep Biomechanics and Core Models from being completely stripped, but obfuscate names
-keep class com.example.biomechanics.** { *; }
-keep class com.example.data.model.** { *; }

# Obfuscate everything else aggressively
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

# Keep ML Kit and CameraX intact
-keep class com.google.mlkit.** { *; }
-keep class androidx.camera.** { *; }
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
