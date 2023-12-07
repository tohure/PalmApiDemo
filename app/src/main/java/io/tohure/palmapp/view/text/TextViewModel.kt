package io.tohure.palmapp.view.text

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.generativelanguage.v1beta3.GenerateTextRequest
import com.google.ai.generativelanguage.v1beta3.HarmCategory
import com.google.ai.generativelanguage.v1beta3.SafetySetting
import com.google.ai.generativelanguage.v1beta3.TextPrompt
import com.google.ai.generativelanguage.v1beta3.TextServiceClient
import com.google.ai.generativelanguage.v1beta3.TextServiceSettings
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider
import com.google.api.gax.rpc.FixedHeaderProvider
import io.tohure.palmapp.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TextViewModel : ViewModel() {

    private val _output = MutableStateFlow(value = "")
    val output: StateFlow<String>
        get() = _output

    private var client: TextServiceClient

    init {
        // Initialize the Text Service Client
        client = initializeTextServiceClient()

        sendPrompt("Do you know GDG Cloud Lima in Peru?")
    }

    fun sendPrompt(query: String) {
        // Create the text prompt
        val prompt = createPrompt(query)

        // Send the first request
        val request = createTextRequest(prompt)
        generateText(request)
    }

    private fun generateText(
        request: GenerateTextRequest
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = client.generateText(request)
                val returnedText = response.candidatesList.last()
                // display the returned text in the UI
                _output.update { returnedText.output }
            } catch (e: Exception) {
                // There was an error, let's add a new text with the details
                _output.update { "API Error: ${e.message}" }
            }
        }
    }

    // (This is a workaround because GAPIC java libraries don't yet support API key auth)
    private fun startTransportChannelProvider() =
        InstantiatingGrpcChannelProvider.newBuilder()
            .setHeaderProvider(FixedHeaderProvider.create(hashMapOf("x-goog-api-key" to BuildConfig.palmKey)))
            .build()

    private fun initializeTextServiceClient(): TextServiceClient {
        // Create TextServiceSettings
        val settings = TextServiceSettings.newBuilder()
            .setTransportChannelProvider(startTransportChannelProvider())
            .setCredentialsProvider(FixedCredentialsProvider.create(null))
            .build()

        // Initialize a TextServiceClient
        return TextServiceClient.create(settings)
    }

    private fun createPrompt(textContent: String) =
        TextPrompt.newBuilder()
            .setText(textContent)
            .build()

    private fun createTextRequest(prompt: TextPrompt) =
        GenerateTextRequest.newBuilder()
            .setModel("models/text-bison-001") // Required, which model to use to generate the result
            .setPrompt(prompt) // Required
            .setTemperature(0.5f) // Optional, controls the randomness of the output
            .setCandidateCount(1) // Optional, the number of generated texts to return
            .setTopK(43)
            .setTopP(1F)
            .addSafetySettings(
                SafetySetting.newBuilder()
                    .setCategory(HarmCategory.HARM_CATEGORY_DEROGATORY)
                    .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
            )
            .addSafetySettings(
                SafetySetting.newBuilder()
                    .setCategory(HarmCategory.HARM_CATEGORY_TOXICITY)
                    .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
            )
            .addSafetySettings(
                SafetySetting.newBuilder()
                    .setCategory(HarmCategory.HARM_CATEGORY_MEDICAL)
                    .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
            )
            .build()
}