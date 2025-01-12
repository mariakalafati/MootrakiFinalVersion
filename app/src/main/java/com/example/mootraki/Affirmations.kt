package com.example.mootraki

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

/*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.tooling.preview.Preview
import com.example.mootraki.data.Datasource
import com.example.mootraki.data.Affirmation
import androidx.compose.material3.Card
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext


@Composable
fun Affirmations() {
    // Display a list of affirmations
    AffirmationList(
        affirmationList = Datasource().loadAffirmations(),
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun AffirmationList(affirmationList: List<Affirmation>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(affirmationList) { affirmation ->
            AffirmationCard(
                affirmation = affirmation,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun AffirmationCard(affirmation: Affirmation, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column {
            Image(
                painter = painterResource(affirmation.imageResourceId),
                contentDescription = stringResource(affirmation.stringResourceId),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = LocalContext.current.getString(affirmation.stringResourceId),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Preview
@Composable
private fun AffirmationCardPreview() {
    AffirmationCard(Affirmation(R.string.affirmation1, R.drawable.image1))
}
*/
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


// Data class to represent an affirmation
//data class Affirmation(val affirmation: String)
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

    suspend fun fetchNewAffirmation() {
        isLoading = true
        try {
            val response = RetrofitInstance.api.getAffirmation() // Example API call
            currentAffirmation = response.affirmation
        } catch (e: Exception) {
            currentAffirmation = "Error fetching affirmation: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }
    // Fetch affirmation when the screen is first displayed
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
        Image(painter = painterResource(R.drawable.background),
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
    }
}
