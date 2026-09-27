package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WorkoutRepository
import com.example.data.model.UserProfile
import com.example.ui.theme.FormBreakdownOrange
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
fun RegistrationScreen(
    repository: WorkoutRepository,
    onRegistrationCompleted: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("24") }
    var heightInput by remember { mutableStateOf("178") }
    var isHeightCm by remember { mutableStateOf(true) }
    var weightInput by remember { mutableStateOf("78") }
    var isWeightKg by remember { mutableStateOf(true) }

    var selectedGoal by remember { mutableStateOf("Strength & Form") }
    var selectedExperience by remember { mutableStateOf("Intermediate") }
    var selectedDivisionFocus by remember { mutableStateOf("Full Body") }

    var nameError by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val goals = listOf("Strength & Form", "Hypertrophy", "Powerlifting", "Functional Fitness")
    val experiences = listOf("Beginner (<1 yr)", "Intermediate (1-3 yrs)", "Advanced (3+ yrs)")
    val primaryMuscles = listOf("Full Body", "Chest & Arms", "Back & Lats", "Legs & Core")

    fun saveAndFinish() {
        if (name.trim().isEmpty()) {
            nameError = true
            Toast.makeText(context, "Please enter your name or tap 'Skip for now'", Toast.LENGTH_SHORT).show()
            return
        }

        val ageNum = age.trim().toIntOrNull() ?: 24
        val rawHeight = heightInput.trim().toFloatOrNull() ?: 178f
        val heightCm = if (isHeightCm) rawHeight else rawHeight * 2.54f // convert inches to cm if ft/in

        val rawWeight = weightInput.trim().toFloatOrNull() ?: 78f
        val weightKg = if (isWeightKg) rawWeight else rawWeight * 0.453592f // convert lbs to kg

        isSaving = true
        coroutineScope.launch {
            repository.saveUserProfile(
                UserProfile(
                    id = 1L,
                    name = name.trim(),
                    age = ageNum,
                    heightCm = heightCm,
                    weightKg = weightKg,
                    fitnessGoal = selectedGoal,
                    experienceLevel = selectedExperience,
                    targetMuscle = selectedDivisionFocus
                )
            )
            Toast.makeText(context, "Welcome, ${name.trim()}! Profile synced successfully.", Toast.LENGTH_SHORT).show()
            onRegistrationCompleted()
        }
    }

    fun skipRegistration() {
        coroutineScope.launch {
            // Save a guest profile so the app knows registration step was touched
            repository.saveUserProfile(
                UserProfile(
                    id = 1L,
                    name = "Athlete",
                    age = 24,
                    heightCm = 178f,
                    weightKg = 78f,
                    fitnessGoal = "Strength & Form",
                    experienceLevel = "Intermediate",
                    targetMuscle = "Full Body"
                )
            )
            onSkip()
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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // TOP BAR: Skip Button Top-Right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(NeonLime.copy(alpha = 0.15f))
                        .border(1.dp, NeonLime, RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "IRONVISION ONBOARDING",
                        color = NeonLime,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                // Skip Option Button
                Text(
                    text = "SKIP FOR NOW",
                    color = IronTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier
                        .clickable { skipRegistration() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("registration_skip_button")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // HEADER: Title & Subtitle
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Athlete Registration",
                    color = IronTextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 34.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Calibrate your personal biomechanics profile, body dimensions & lifting targets.",
                    color = IronTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))


            // 1. ATHLETE NAME INPUT
            RegistrationInputField(
                label = "FULL NAME",
                value = name,
                onValueChange = {
                    name = it
                    if (it.isNotEmpty()) nameError = false
                },
                placeholder = "e.g. Arbaz Gujjar / Alex Mercer",
                icon = Icons.Default.Person,
                isError = nameError,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
                testTag = "registration_name_input"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. AGE & BODY COMPOSITION ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Age Field
                Box(modifier = Modifier.weight(1f)) {
                    RegistrationInputField(
                        label = "AGE (YEARS)",
                        value = age,
                        onValueChange = { age = it.filter { ch -> ch.isDigit() } },
                        placeholder = "24",
                        icon = Icons.Default.Schedule,
                        keyboardType = KeyboardType.Number,
                        testTag = "registration_age_input"
                    )
                }

                // Weight Field with kg/lbs toggle
                Box(modifier = Modifier.weight(1.3f)) {
                    RegistrationUnitInputField(
                        label = "WEIGHT",
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        unit = if (isWeightKg) "KG" else "LBS",
                        onUnitToggle = { isWeightKg = !isWeightKg },
                        icon = Icons.Default.MonitorWeight,
                        testTag = "registration_weight_input"
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. HEIGHT INPUT with cm/ft toggle
            RegistrationUnitInputField(
                label = "BODY HEIGHT",
                value = heightInput,
                onValueChange = { heightInput = it },
                unit = if (isHeightCm) "CM" else "IN",
                onUnitToggle = { isHeightCm = !isHeightCm },
                icon = Icons.Default.Height,
                testTag = "registration_height_input"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 4. PRIMARY FITNESS GOAL SELECTOR
            SectionLabel(title = "PRIMARY TRAINING GOAL")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                goals.take(2).forEach { goal ->
                    GoalChip(
                        text = goal,
                        isSelected = selectedGoal == goal,
                        onClick = { selectedGoal = goal },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                goals.drop(2).forEach { goal ->
                    GoalChip(
                        text = goal,
                        isSelected = selectedGoal == goal,
                        onClick = { selectedGoal = goal },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. EXPERIENCE LEVEL
            SectionLabel(title = "LIFTING EXPERIENCE")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                experiences.forEach { exp ->
                    GoalChip(
                        text = exp,
                        isSelected = selectedExperience == exp,
                        onClick = { selectedExperience = exp },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6. TARGET MUSCLE GROUP FOCUS
            SectionLabel(title = "TODAY'S WORKOUT FOCUS")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                primaryMuscles.take(2).forEach { muscle ->
                    GoalChip(
                        text = muscle,
                        isSelected = selectedDivisionFocus == muscle,
                        onClick = { selectedDivisionFocus = muscle },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                primaryMuscles.drop(2).forEach { muscle ->
                    GoalChip(
                        text = muscle,
                        isSelected = selectedDivisionFocus == muscle,
                        onClick = { selectedDivisionFocus = muscle },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PRIMARY SUBMIT BUTTON
            Button(
                onClick = { saveAndFinish() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("registration_save_button"),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonLime,
                    contentColor = NeonLimeDark
                ),
                enabled = !isSaving
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isSaving) "SAVING PROFILE..." else "COMPLETE REGISTRATION & START",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary Skip Link
            Text(
                text = "Skip for now — continue as Guest Athlete",
                color = IronTextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable { skipRegistration() }
                    .padding(8.dp)
                    .testTag("registration_skip_bottom_text")
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = title,
            color = IronTextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun RegistrationInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    isError: Boolean = false,
    testTag: String = ""
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = if (isError) FormBreakdownOrange else IronTextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = IronTextMuted,
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isError) FormBreakdownOrange else NeonLime,
                    modifier = Modifier.size(20.dp)
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                capitalization = capitalization,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = IronSurfaceDark,
                unfocusedContainerColor = IronSurfaceDark,
                focusedBorderColor = if (isError) FormBreakdownOrange else NeonLime,
                unfocusedBorderColor = if (isError) FormBreakdownOrange else IronBorder,
                focusedTextColor = IronTextPrimary,
                unfocusedTextColor = IronTextPrimary
            )
        )
    }
}

@Composable
private fun RegistrationUnitInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    onUnitToggle: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String = ""
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = IronTextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NeonLime,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF1F2024))
                        .border(1.dp, IronBorder, RoundedCornerShape(50))
                        .clickable { onUnitToggle() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = unit,
                        color = NeonLime,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = IronSurfaceDark,
                unfocusedContainerColor = IronSurfaceDark,
                focusedBorderColor = NeonLime,
                unfocusedBorderColor = IronBorder,
                focusedTextColor = IronTextPrimary,
                unfocusedTextColor = IronTextPrimary
            )
        )
    }
}

@Composable
private fun GoalChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) NeonLime else IronSurfaceDark)
            .border(1.dp, if (isSelected) NeonLime else IronBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) NeonLimeDark else IronTextPrimary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}
