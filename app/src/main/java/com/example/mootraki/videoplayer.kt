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

data class VideoCategory(val name: String, val videoResId: Int, val duration: Int)

val videoCategories = listOf(
    VideoCategory("Minimalist - 1 min", R.raw.video1, 60),
    VideoCategory("Sea - 2 min", R.raw.video2, 120),
    VideoCategory("Sunset - 3 min", R.raw.video3, 180),
    VideoCategory("Soft - 4 min", R.raw.video4, 240),
    VideoCategory("Sky - 5 min", R.raw.video5, 300)
)

@Composable
fun VideoScreen() {
    var selectedVideoCategory by remember { mutableStateOf(videoCategories[0]) }
    var isPlaying by remember { mutableStateOf(false) }
    var timeRemains by remember { mutableStateOf(selectedVideoCategory.duration) }
    var phaseIndex by remember { mutableStateOf(0) }


    LocalVideoPlayer(videoResId = selectedVideoCategory.videoResId, isPlaying = isPlaying)
    DropDownVideoCategories(selectedVideoCategory) { category ->
        selectedVideoCategory = category
        timeRemains = category.duration
        phaseIndex = 0
        isPlaying = false
    }
    CountdownTimer(
        duration = timeRemains,
        isPlaying = isPlaying,
        phaseIndex = phaseIndex,
        onPlayPause = { playing, newPhaseIndex ->
            isPlaying = playing
            phaseIndex = newPhaseIndex
        },
        onVideoPlay = { play ->
            isPlaying = play
        }
    )

}

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
                        onCategorySelected(category)
                    }
                )
            }
        }
    }
}

@Composable
fun LocalVideoPlayer(videoResId: Int, isPlaying: Boolean) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                VideoView(context).apply {
                    val uri: Uri = Uri.parse("android.resource://${context.packageName}/$videoResId")
                    setVideoURI(uri)

                    setOnPreparedListener { mediaPlayer: MediaPlayer ->
                        mediaPlayer.isLooping = true
                        if (isPlaying) {
                            mediaPlayer.start()
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
        if (!isPlaying) {
            Image(
                painter = painterResource(id = R.drawable.pause),
                contentDescription = "Video Placeholder",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun CountdownTimer(
    duration: Int,
    isPlaying: Boolean,
    phaseIndex: Int,
    onPlayPause: (Boolean,Int) -> Unit,
    onVideoPlay: (Boolean) -> Unit
) {
    var timeRemains by remember { mutableStateOf(duration) }
    var timerAction by remember { mutableStateOf(false) }
    var currentPhase by remember { mutableStateOf("Breathe in") }
    var currentPhaseIndex by remember { mutableStateOf(phaseIndex) }

    val phases = listOf(
        Pair("Breathe in", 8),
        Pair("Hold", 4),
        Pair("Breathe out", 8)
    )

    LaunchedEffect(duration) {
        timeRemains = duration
        currentPhaseIndex = 0
        currentPhase = phases[currentPhaseIndex].first
    }

    LaunchedEffect(timerAction) {
        if (timerAction) {
            onVideoPlay(true)
            while (timeRemains > 0) {
                val currentPhaseDuration = phases[currentPhaseIndex].second
                currentPhase = phases[currentPhaseIndex].first
                for (i in 0 until currentPhaseDuration) {
                    if (!timerAction || timeRemains <= 0) break
                    delay(1000)
                    timeRemains--
                }
                if (timeRemains <= 0) break
                currentPhaseIndex = (currentPhaseIndex + 1) % phases.size
            }
            timerAction = false
            onVideoPlay(false)
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
            val newPlayingState = !isPlaying

            if (timeRemains <= 0) {
                timeRemains = duration
                currentPhaseIndex = 0
                currentPhase = phases[currentPhaseIndex].first
            }
            timerAction = newPlayingState
            onPlayPause(newPlayingState, currentPhaseIndex)
        }) { Text(if (isPlaying) "Pause" else "Play") }
    }
}
