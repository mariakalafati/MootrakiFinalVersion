package com.example.mootraki

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class AffirmationResponse(
    val affirmation: String
)

interface AffirmationApiService {
    @GET("/")
    suspend fun getAffirmation(): AffirmationResponse
}

object RetrofitInstance {
    private const val BASE_URL = "https://www.affirmations.dev"
    val api: AffirmationApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AffirmationApiService::class.java)
    }
}

@Composable
fun Affirmations() {
    var currentAffirmation by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(true) }
    var currentBackground by remember { mutableStateOf(R.drawable.background) }
    val backgrounds = listOf(
        R.drawable.background,
        R.drawable.background2,
        R.drawable.background3,
        R.drawable.background4,
        R.drawable.background5,
        R.drawable.background6,
        R.drawable.background7
    )

    suspend fun fetchNewAffirmation() {
        isLoading = true
        try {
            val response = RetrofitInstance.api.getAffirmation()
            currentAffirmation = response.affirmation
        } catch (e: Exception) {
            currentAffirmation = "Error fetching affirmation: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        fetchNewAffirmation()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                CoroutineScope(Dispatchers.Main).launch {
                    fetchNewAffirmation()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(currentBackground),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize())
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = currentAffirmation,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(20.dp))
        }

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = currentAffirmation,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(20.dp)
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(36.dp))
                    .clickable {
                        currentBackground = backgrounds.random()
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Click to change background",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    }
}
