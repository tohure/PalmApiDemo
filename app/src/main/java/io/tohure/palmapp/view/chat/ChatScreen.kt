package io.tohure.palmapp.view.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.ai.generativelanguage.v1beta3.Message
import io.tohure.palmapp.ui.theme.PalmAppTheme

@Composable
fun ChatScreen(
    mainViewModel: ChatViewModel = viewModel()
) {
    val (inputText, setInputText) = remember { mutableStateOf("") }
    val messages: List<Message> by mainViewModel.messages.collectAsState()
    Column(
        modifier = Modifier.padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = inputText,
            onValueChange = setInputText,
            label = { Text("Input:") }
        )
        Button(
            onClick = {
                mainViewModel.sendMessage(inputText)
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Send Message")
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            reverseLayout = true
        ) {
            items(messages) { message ->
                Card(modifier = Modifier.padding(vertical = 2.dp)) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = if (message.author == "1") "PaLM" else message.author,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = message.content,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatUiPreview() {
    PalmAppTheme {
        ChatScreen()
    }
}