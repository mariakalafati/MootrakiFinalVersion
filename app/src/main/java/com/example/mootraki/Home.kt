package com.example.mootraki

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mootraki.ui.theme.MootrakiTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Home(
    navController: NavController,
    viewModel: SubmitEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val snackbarHostState = remember { SnackbarHostState() } // Snackbar state
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            val date = remember {
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateFormat.format(calendar.time)
            }

            val entryUiState = viewModel.entryUiState

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp
                )
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "How was your day?",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Emotions",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Today's note",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp
            )

            BasicTextField(
                value = entryUiState.entryDetails.note,
                onValueChange = {
                    viewModel.updateUiState(entryUiState.entryDetails.copy(note = it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small)
                    .padding(8.dp),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    coroutineScope.launch {
                        if (entryUiState.isEntryValid) {
                            viewModel.submitEntry()
                            navController.navigate("calendar") {
                                // Optionally clear the back stack to prevent users from going back to the current screen
                                popUpTo("home") { inclusive = true }
                            }
                        } else {
                            snackbarHostState.showSnackbar("Oops, seams like you've missed a reflection field.")
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
            fontSize = 24.sp,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EmotionOption(emotion: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() } // Trigger the click handler
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
                text = emotion.take(1), // First letter of the emotion
                fontSize = 18.sp,
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

