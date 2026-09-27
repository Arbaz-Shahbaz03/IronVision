package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biomechanics.ExerciseType
import com.example.biomechanics.MuscleDivision
import com.example.ui.theme.IronBorder
import com.example.ui.theme.IronDarkBackground
import com.example.ui.theme.IronSurfaceDark
import com.example.ui.theme.IronTextMuted
import com.example.ui.theme.IronTextPrimary
import com.example.ui.theme.IronTextSecondary
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonLimeDark

@Composable
fun ExercisesScreen(
    initialDivision: MuscleDivision = MuscleDivision.FULL_BODY,
    onSelectExercise: (ExerciseType) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDivision by remember { mutableStateOf(initialDivision) }
    var searchQuery by remember { mutableStateOf("") }

    val allExercises = remember { ExerciseType.values().toList() }

    val filteredExercises = remember(selectedDivision, searchQuery) {
        allExercises.filter { ex ->
            val matchesDivision = when (selectedDivision) {
                MuscleDivision.FULL_BODY -> true
                else -> ex.division == selectedDivision
            }
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                ex.displayName.contains(searchQuery.trim(), ignoreCase = true) ||
                ex.cueText.contains(searchQuery.trim(), ignoreCase = true) ||
                ex.division.displayName.contains(searchQuery.trim(), ignoreCase = true)
            }
            matchesDivision && matchesSearch
        }
    }

    val divisionTabs = listOf(
        MuscleDivision.FULL_BODY,
        MuscleDivision.CHEST,
        MuscleDivision.BACK,
        MuscleDivision.LEGS,
        MuscleDivision.GLUTES,
        MuscleDivision.CORE,
        MuscleDivision.BICEPS,
        MuscleDivision.TRICEPS,
        MuscleDivision.SHOULDERS,
        MuscleDivision.CALVES
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(IronDarkBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // TOP HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(IronSurfaceDark)
                        .border(1.dp, IronBorder, CircleShape)
                        .testTag("exercises_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = IronTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "EXERCISE LIBRARY",
                        color = NeonLime,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Gym Movements & Divisions",
                        color = IronTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // SEARCH BAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search chest, core, back, legs...",
                            color = IronTextMuted,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = IronTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = IronTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("exercises_search_input"),
                    shape = RoundedCornerShape(14.dp),
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

            Spacer(modifier = Modifier.height(10.dp))

            // CATEGORY TABS (Chest, Back, Legs, Core, etc.)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(divisionTabs) { division ->
                    val isSelected = selectedDivision == division
                    val badgeColor = Color(division.badgeColorHex)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isSelected) NeonLime else IronSurfaceDark)
                            .border(
                                1.dp,
                                if (isSelected) NeonLime else IronBorder,
                                RoundedCornerShape(50)
                            )
                            .clickable { selectedDivision = division }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("division_tab_${division.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!isSelected && division != MuscleDivision.FULL_BODY) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(badgeColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = division.displayName,
                                color = if (isSelected) NeonLimeDark else IronTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // STATS BANNER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredExercises.size} MOVEMENTS AVAILABLE",
                    color = IronTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "REAL-TIME KINEMATICS ACTIVE",
                    color = NeonLime,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // EXERCISES LIST
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredExercises, key = { it.name }) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        onLaunch = { onSelectExercise(exercise) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseType,
    onLaunch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val divisionColor = Color(exercise.division.badgeColorHex)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("exercise_card_${exercise.name.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = IronSurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, IronBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Division & Joint row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Muscle Division Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(divisionColor.copy(alpha = 0.15f))
                        .border(1.dp, divisionColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = exercise.division.displayName,
                        color = divisionColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }

                // Target Joint & Angle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Straighten,
                        contentDescription = null,
                        tint = NeonLime,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${exercise.primaryJointName} ${exercise.targetInflectionAngle.toInt()}°",
                        color = IronTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Exercise Title
            Text(
                text = exercise.displayName,
                color = IronTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Biomechanical Cue
            Text(
                text = "Form cue: ${exercise.cueText}",
                color = IronTextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom action row: Baseline weight and Start button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (exercise.isHoldOrTimer) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = IronTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Isometric Hold",
                            color = IronTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = IronTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (exercise.defaultWeightKg > 0f) "Baseline: ${exercise.defaultWeightKg.toInt()} kg" else "Bodyweight",
                            color = IronTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Button(
                    onClick = onLaunch,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonLime,
                        contentColor = NeonLimeDark
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("launch_exercise_${exercise.name.lowercase()}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "START COACH",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}
