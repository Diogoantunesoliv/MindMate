package com.example.chatgptapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatgptapp.model.ChatRequest
import com.example.chatgptapp.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import com.example.chatgptapp.model.Message
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var exibirTelaChatGPT by remember { mutableStateOf(false) }
            var exibirTelaDiario by remember { mutableStateOf(false) }

            if (exibirTelaChatGPT) {
                TelaChatGPT {
                    exibirTelaChatGPT = false
                }
            } else if (exibirTelaDiario) {
                TelaDiario {
                    exibirTelaDiario = false
                }
            } else {
                TelaInicial(
                    onNavigateToChatGPT = {
                        exibirTelaChatGPT = true
                    },
                    onNavigateToDiario = {
                        exibirTelaDiario = true
                    }
                )
            }
        }
    }
}


@Composable
fun TelaInicial(onNavigateToChatGPT: () -> Unit, onNavigateToDiario: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "MindMate - Aplicativo de Suporte Mental",
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 150.dp)
        )
        Text(
            text = "Transformando Pensamentos em Positividade!",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(bottom = 100.dp)
        )

        Button(onClick = onNavigateToChatGPT) {
            Text("Conversar com o ChatGPT")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToDiario) {
            Text("O meu Diário")
        }
    }
}



@Composable
fun TelaChatGPT(onNavigateBack: () -> Unit) {
    var inputText by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(150.dp))

        Text(
            text = "MindMate - Aplicativo de Ajuda Mental",
            fontSize = 24.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF6200EE),
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Como te sentes hoje?") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp)),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                textColor = Color.Black
            )
        )

        Text(
            text = "Resposta: $responseText",
            fontSize = 18.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { onNavigateBack() }) {
                Text("Voltar")
            }

            Spacer(modifier = Modifier.width(2.dp))

            Button(onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        Log.d("ChatGPTApp", "Fazendo chamada à API")

                        // Criar a requisição com base no inputText
                        val chatRequest = ChatRequest(
                            model = "gpt-3.5-turbo",
                            messages = listOf(
                                Message(role = "user", content = inputText)
                            )
                        )

                        // Fazendo a chamada com a requisição
                        val response = RetrofitInstance.api.postMessage(chatRequest).execute()
                        if (response.isSuccessful) {
                            val chatResponse = response.body()
                            withContext(Dispatchers.Main) {
                                responseText = chatResponse?.response ?: "Resposta vazia"
                                Log.d("ChatGPTApp", "Resposta recebida: $responseText")
                            }
                        } else {
                            val errorBody = response.errorBody()?.string() ?: "Erro desconhecido"
                            Log.e("ChatGPTApp", "Erro na resposta da API: $errorBody")
                        }
                    } catch (e: Exception) {
                        Log.e("ChatGPTApp", "Falha na chamada da API", e)
                    }
                }
            }) {
                Text("Enviar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun TelaDiario(onNavigateBack: () -> Unit) {
    var entradaDiario by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Meu Diário",
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        TextField(
            value = entradaDiario,
            onValueChange = { entradaDiario = it },
            label = { Text("Expressa-te aqui!") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        Button(
            onClick = { onNavigateBack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Voltar")
        }
    }
}
