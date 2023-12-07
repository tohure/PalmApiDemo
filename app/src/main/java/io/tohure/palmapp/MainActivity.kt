package io.tohure.palmapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import io.tohure.palmapp.ui.theme.PalmAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PalmAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SampleUi()
                }
            }
        }
    }
}

@Composable
fun SampleUi(
    viewModel: MainViewModel = viewModel()
) {
    val (inputText, setInputText) = remember { mutableStateOf("") }
    val textOutput: String by viewModel.output.collectAsState()
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
                viewModel.sendPrompt(inputText)
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Generate Text")
        }
        Card(
            modifier = Modifier
                .padding(vertical = 2.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = textOutput,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PalmAppTheme {
        SampleUi()
    }
}