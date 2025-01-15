package com.example.mootraki

import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
//first go to C:\Users\DimitraP\AndroidStudioProjects\myapp\app\src\main\res\raw and drag and drop the videos there
//call all of this with MaterialTheme {
//                Surface {
//                    VideoScreen()
//                }
//            }


// Data class for video categories
data class VideoCategory(val name: String, val videoResId: Int, val duration: Int)

// List of video categories
val videoCategories = listOf(
    VideoCategory("Minimalist - 1 min", R.raw.video1, 60),
    VideoCategory("Sea - 2 min", R.raw.video2, 120),
    VideoCategory("Sunset - 3 min", R.raw.video3, 180),
    VideoCategory("Soft - 4 min", R.raw.video4, 240),
    VideoCategory("Sky - 5 min", R.raw.video5, 300)
)
/*enum class TimerCategory(val seconds: Int) {
    SHORT(10),   // 10 seconds
    MEDIUM(30),  // 30 seconds
    LONG(60),    // 60 seconds
    EXTRA_LONG(120), // 120 seconds
    EXTREME(300) // 300 seconds
}*/

// Composable for the video screen
@Composable
fun VideoScreen() {
    var selectedVideoCategory by remember { mutableStateOf(videoCategories[0]) } // Default to the first category
    var isPlaying by remember { mutableStateOf(false) }
    var timeRemains by remember { mutableStateOf(selectedVideoCategory.duration) } // Timer state
    var phaseIndex by remember { mutableStateOf(0) } // Track the current phase index


    // Video Player with Play/Pause Button
    LocalVideoPlayer(videoResId = selectedVideoCategory.videoResId, isPlaying = isPlaying)
    DropDownVideoCategories(selectedVideoCategory) { category ->
        selectedVideoCategory = category
        timeRemains = category.duration // Update timer duration when category changes
        phaseIndex = 0 // Reset phase index to start from "Breathe in"
        isPlaying = false // Stop the video when changing categories
    }
    CountdownTimer(
        duration = timeRemains,
        isPlaying = isPlaying,
        phaseIndex = phaseIndex,
        onPlayPause = { playing, newPhaseIndex ->
            isPlaying = playing // Update the play state based on timer
            phaseIndex = newPhaseIndex // Update the phase index
        },
        onVideoPlay = { play ->
            isPlaying = play // Control video playback based on timer state
        }
    )

}

// Composable for the dropdown menu
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownVideoCategories(selectedCategory: VideoCategory, onCategorySelected: (VideoCategory) -> Unit) {
    val dropDownOpen = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = selectedCategory.name,
            modifier = Modifier
                .clickable { dropDownOpen.value = true }
                .padding(16.dp)
        )
        DropdownMenu(
            expanded = dropDownOpen.value,
            onDismissRequest = { dropDownOpen.value = false }
        ) {
            videoCategories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(text = category.name) },
                    onClick = {
                        dropDownOpen.value = false
                        onCategorySelected(category) // Notify the selected category
                    }
                )
            }
        }
    }
}

// Composable for the local video player
@Composable
fun LocalVideoPlayer(videoResId: Int, isPlaying: Boolean) {
    //var plays by remember { mutableStateOf(false) } // State for play/pause
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                VideoView(context).apply {
                    val uri: Uri = Uri.parse("android.resource://${context.packageName}/$videoResId")
                    setVideoURI(uri)

                    setOnPreparedListener { mediaPlayer: MediaPlayer ->
                        mediaPlayer.isLooping = true // Set looping to true
                        if (isPlaying) {
                            //mediaPlayer.isLooping = true
                            mediaPlayer.start() // Start video if playing
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            update = { videoView ->
                val uri: Uri = Uri.parse("android.resource://${videoView.context.packageName}/$videoResId")
                videoView.setVideoURI(uri)
                if (isPlaying) {
                    videoView.start()
                } else {
                    videoView.pause()
                }
            }
        )
        // Placeholder Image
        if (!isPlaying) {
            Image(
                painter = painterResource(id = R.drawable.pause), // Replace with your placeholder image
                contentDescription = "Video Placeholder",
                modifier = Modifier.fillMaxSize()
            )
        }

        // Play/Pause Button
//        Button(
//            onClick = { plays = !plays },
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(16.dp)
//        ) {
//            Text(text = if (plays) "Pause" else "Play")
//        }
    }
}

@Composable
fun CountdownTimer(
    duration: Int,
    isPlaying: Boolean,
    phaseIndex: Int,
    onPlayPause: (Boolean,Int) -> Unit,
    onVideoPlay: (Boolean) -> Unit // Callback to control video playback
) {
    var timeRemains by remember { mutableStateOf(duration) } // Set the initial time in seconds
    var timerAction by remember { mutableStateOf(false) }
    var currentPhase by remember { mutableStateOf("Breathe in") } // Initial phase
    var currentPhaseIndex by remember { mutableStateOf(phaseIndex) } // Track the current phase index

    // Define the durations for each phase
    val phases = listOf(
        Pair("Breathe in", 8),
        Pair("Hold", 4),
        Pair("Breathe out", 8)
    )

    // Update the timer when the duration changes
    LaunchedEffect(duration) {
        timeRemains = duration // Update timeRemains when duration changes
        currentPhaseIndex = 0 // Reset phase index to start from "Breathe in"
        currentPhase = phases[currentPhaseIndex].first // Reset current phase
    }

    LaunchedEffect(timerAction) {
        if (timerAction) {
            onVideoPlay(true) // Start video playback when timer starts
            while (timeRemains > 0) {
                // Get the current phase duration
                val currentPhaseDuration = phases[currentPhaseIndex].second
                currentPhase = phases[currentPhaseIndex].first
                // Cycle through the phases
                //currentPhase = "Breathe in"
                for (i in 0 until currentPhaseDuration) {
                    if (!timerAction || timeRemains <= 0) break
                    delay(1000) // Wait for 1 second
                    timeRemains-- // Decrement the time left
                }

                if (timeRemains <= 0) break // Exit if time is up

                // Move to the next phase
                currentPhaseIndex = (currentPhaseIndex + 1) % phases.size // Loop back to the first phase
//                currentPhase = "Hold"
//                for (i in 0 until holdDuration) {
//                    if (!timerAction || timeRemains <= 0) break
//                    delay(1000) // Wait for 1 second
//                    timeRemains-- // Decrement the time left
//                }
//
//                if (timeRemains <= 0) break
//                currentPhase = "Breathe out"
//                for (i in 0 until breatheOutDuration) {
//                    if (!timerAction || timeRemains <= 0) break
//                    delay(1000) // Wait for 1 second
//                    timeRemains-- // Decrement the time left
//                }
            } //CAUTION
            timerAction = false // Stop the timer when it reaches 0
            onVideoPlay(false) // Stop video playback when timer ends
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = currentPhase, style = MaterialTheme.typography.headlineMedium)
        Text(text = "Time Left: $timeRemains seconds")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Toggle play/pause state
            val newPlayingState = !isPlaying

            // Reset timer if it has ended
            if (timeRemains <= 0) {
                timeRemains = duration // Reset to the original duration
                currentPhaseIndex = 0 // Reset phase index to start from the beginning
                currentPhase = phases[currentPhaseIndex].first
            }
            // Start or stop the timer based on the new state
            timerAction = newPlayingState
            // Update the play state
            onPlayPause(newPlayingState, currentPhaseIndex)
        }) {
            Text(if (isPlaying) "Pause" else "Play")
        }
    }
}