package com.example.chatgptapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var exibirTelaChatGPT by remember { mutableStateOf(false) }

            if (exibirTelaChatGPT) {
                TelaChatGPT(onNavigateBack = { exibirTelaChatGPT = false })
            } else {
                TelaInicial(onNavigate = { exibirTelaChatGPT = true })
            }
        }
    }
}

@Composable
fun TelaInicial(onNavigate: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bem-vindo ao MindMate", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigate) {
            Text("Conversar com o ChatGPT")
        }
        Button(onClick = onNavigate) {
            Text("Diário de Sentimentos")
        }
    }
}

@Composable
fun TelaChatGPT(onNavigateBack: () -> Unit) {
    var inputText by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MindMate - Aplicativo de Suporte Mental",
            fontSize = 24.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF6200EE),
            modifier = Modifier.padding(16.dp)
        )
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Digite sua mensagem") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp)),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                textColor = Color.Black
            )
        )
        Button(
            onClick = {
                // Aqui você irá chamar a API do ChatGPT usando Retrofit
                // Exemplo: responseText = chatGptApi.getResponse(inputText)
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Enviar", color = Color.White)
        }
        Text(
            text = "Resposta: $responseText",
            fontSize = 18.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ... Componentes existentes ...

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateBack,
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Voltar", color = Color.White)
            }
        }
    }
}