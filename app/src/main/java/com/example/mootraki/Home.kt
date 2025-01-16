package com.example.mootraki

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Composable function for the Home screen.
 * This screen allows users to reflect on their day by selecting a mood, emotions, and adding notes.
 */
@Composable
fun Home(
    navController: NavController,
    viewModel: SubmitEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val snackbarHostState = remember { SnackbarHostState() } // Snackbar state for showing messages
    val coroutineScope = rememberCoroutineScope() // Coroutine scope for managing async operations

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Display current date
            val date = remember {
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.getDefault())
                dateFormat.format(calendar.time)
            }

            val entryUiState = viewModel.entryUiState

            // Date Display Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mood Selection Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "How was your day?",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Mood options rendered as emojis
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("😀", "😊", "😐", "☹️", "😢").forEachIndexed { index, emoji ->
                            MoodOption(
                                emoji = emoji,
                                isSelected = entryUiState.entryDetails.mood == index,
                                onClick = {
                                    viewModel.updateUiState(entryUiState.entryDetails.copy(mood = index))
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Emotion Selection Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Emotions",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Emotion options rendered as a grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(
                            listOf("Excited", "Relaxed", "Happy", "Tired", "Annoyed", "Stressed", "Bored", "Hopeful")
                        ) { emotion ->
                            EmotionOption(
                                emotion = emotion,
                                isSelected = entryUiState.entryDetails.emotions.contains(emotion),
                                onClick = {
                                    val updatedEmotions = if (entryUiState.entryDetails.emotions.contains(emotion)) {
                                        entryUiState.entryDetails.emotions - emotion
                                    } else {
                                        entryUiState.entryDetails.emotions + emotion
                                    }
                                    viewModel.updateUiState(entryUiState.entryDetails.copy(emotions = updatedEmotions))
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Note Input Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Today's note",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    BasicTextField(
                        value = entryUiState.entryDetails.note,
                        onValueChange = {
                            viewModel.updateUiState(entryUiState.entryDetails.copy(note = it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(8.dp),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(8.dp),
                                contentAlignment = Alignment.TopStart
                            ) {
                                if (entryUiState.entryDetails.note.isEmpty()) {
                                    Text(
                                        text = "Write here...",
                                        style = TextStyle(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 16.sp
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Submit Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (entryUiState.isEntryValid) {
                            viewModel.submitEntry()
                            navController.navigate("calendar") {
                                popUpTo("home") { inclusive = true }
                            }
                        } else {
                            snackbarHostState.showSnackbar("Oops, seems like you've missed a reflection field.")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Done",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * Composable function to display a mood option as an emoji.
 * @param emoji The emoji representing the mood.
 * @param isSelected Whether this mood is currently selected.
 * @param onClick Lambda to handle click events.
 */
@Composable
fun MoodOption(emoji: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 16.sp,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Composable function to display an emotion option.
 * @param emotion The name of the emotion.
 * @param isSelected Whether this emotion is currently selected.
 * @param onClick Lambda to handle click events.
 */
@Composable
fun EmotionOption(emotion: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emotion.take(1), // Display the first letter of the emotion
                fontSize = 16.sp,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = emotion,
            fontSize = 14.sp,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

