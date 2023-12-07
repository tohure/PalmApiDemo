package io.tohure.palmapp.view.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.generativelanguage.v1beta3.DiscussServiceClient
import com.google.ai.generativelanguage.v1beta3.DiscussServiceSettings
import com.google.ai.generativelanguage.v1beta3.Example
import com.google.ai.generativelanguage.v1beta3.GenerateMessageRequest
import com.google.ai.generativelanguage.v1beta3.Message
import com.google.ai.generativelanguage.v1beta3.MessagePrompt
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider
import com.google.api.gax.rpc.FixedHeaderProvider
import io.tohure.palmapp.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(value = listOf())
    val messages: StateFlow<List<Message>>
        get() = _messages

    private var client: DiscussServiceClient

    init {
        // Initialize the Discuss Service Client
        client = initializeDiscussServiceClient()

        // Create the message prompt
        val prompt = createPrompt("How many organizers have GDG Cloud Lima?")

        // Send the first request to kickstart the conversation
        val request = createMessageRequest(prompt)
        generateMessage(request)
    }

    private fun generateMessage(
        request: GenerateMessageRequest
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = client.generateMessage(request)

                val returnedMessage = response.candidatesList.last()

                // display the returned message in the UI
                _messages.update {
                    // Add the response to the list
                    it.toMutableList().apply {
                        add(returnedMessage)
                    }
                }
            } catch (e: Exception) {
                // There was an error, let's add a new message with the details
                _messages.update { messages ->
                    val mutableList = messages.toMutableList()
                    mutableList.apply {
                        add(
                            Message.newBuilder()
                                .setAuthor("API Error")
                                .setContent(e.message)
                                .build()
                        )
                    }
                }
            }
        }
    }

    fun sendMessage(userInput: String) {
        val prompt = createPrompt(userInput)

        val request = createMessageRequest(prompt)
        generateMessage(request)
    }

    // (This is a workaround because GAPIC java libraries don't yet support API key auth)
    private fun startTransportChannelProvider() =
        InstantiatingGrpcChannelProvider.newBuilder()
            .setHeaderProvider(FixedHeaderProvider.create(hashMapOf("x-goog-api-key" to BuildConfig.palmKey)))
            .build()

    private fun initializeDiscussServiceClient(): DiscussServiceClient {
        // Create DiscussServiceSettings
        val settings = DiscussServiceSettings.newBuilder()
            .setTransportChannelProvider(startTransportChannelProvider())
            .setCredentialsProvider(FixedCredentialsProvider.create(null))
            .build()

        // Initialize a DiscussServiceClient
        return DiscussServiceClient.create(settings)
    }

    private fun createPrompt(
        messageContent: String
    ): MessagePrompt {
        val palmMessage = Message.newBuilder()
            .setAuthor("Me")
            .setContent(messageContent)
            .build()

        // Updating main UI
        _messages.update {
            it.toMutableList().apply {
                add(palmMessage)
            }
        }

        return MessagePrompt.newBuilder()
            .addMessages(palmMessage) // required
            .setContext("Respond to all questions with a rhyming poem.") // optional
            .addExamples(createCaliforniaExample()) // use addAllExamples() to add a list of examples
            .build()
    }

    private fun createCaliforniaExample(): Example {
        val input = Message.newBuilder()
            .setContent("What is the capital of California?")
            .build()

        val response = Message.newBuilder()
            .setContent("If the capital of California is what you seek, Sacramento is where you ought to peek.")
            .build()

        return Example.newBuilder()
            .setInput(input)
            .setOutput(response)
            .build()
    }

    private fun createMessageRequest(prompt: MessagePrompt) =
        GenerateMessageRequest.newBuilder()
            .setModel("models/chat-bison-001") // Required, which model to use to generate the result
            .setPrompt(prompt) // Required
            .setTemperature(0.5f) // Optional, controls the randomness of the output
            .setCandidateCount(1) // Optional, the number of generated messages to return
            .build()

}