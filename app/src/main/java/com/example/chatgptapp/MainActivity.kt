package com.example.chatgptapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.chatgptapp.network.RetrofitInstance
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.withContext
import com.example.chatgptapp.model.createChatRequest
import androidx.compose.runtime.Composable
import kotlinx.coroutines.Dispatchers
import com.google.firebase.perf.metrics.Trace
import com.google.firebase.perf.FirebasePerformance
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Snackbar
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var mostrarTelaInicio by remember { mutableStateOf(true) }
            var exibirTelaChatGPT by remember { mutableStateOf(false) }
            var exibirTelaDiario by remember { mutableStateOf(false) }
            val authState by authViewModel.authState.collectAsState()

            LaunchedEffect(authState) {
                mostrarTelaInicio = authState is AuthState.Login
            }

            when {
                mostrarTelaInicio -> {
                    TelaInicio {
                        mostrarTelaInicio = false

                        val analytics = FirebaseAnalytics.getInstance(this@MainActivity)
                        analytics.logEvent("log_botao_clicado", null)
                    }
                }
                exibirTelaChatGPT -> {
                    TelaChatGPT { exibirTelaChatGPT = false }
                }
                exibirTelaDiario -> {
                    TelaDiario { exibirTelaDiario = false }
                }
                authState is AuthState.LoggedIn -> {
                    TelaInicialPosLogin(
                        onNavigateToChatGPT = { exibirTelaChatGPT = true },
                        onNavigateToDiario = { exibirTelaDiario = true },
                        onLogout = { authViewModel.logout() }
                    )
                }
                authState is AuthState.Login -> {
                    TelaLogin(authViewModel)
                }
                authState is AuthState.Register -> {
                    TelaRegisto(authViewModel)
                }
            }
        }
    }
}

@Composable
fun TelaInicio(onIniciarClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bem-vindo ao MindMate",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 33.sp,
                fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 50.dp)
        )
        Button(
            onClick = onIniciarClicked,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00897B)),
            modifier = Modifier
                .height(50.dp)
                .padding(horizontal = 50.dp)
        ) {
            Text(
                "Iniciar",
                color = Color.White,
                textAlign = TextAlign.Center,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}
@Composable
fun TelaLogin(authViewModel: AuthViewModel) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var snackbarText by remember { mutableStateOf("") }
    var snackbarVisible by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.h4,
            color = Color(0xFF004D40),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)

        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisibility)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(image, "toggle password visibility")
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if(!email.contains("@")) {
                    snackbarText = "Por favor, preencha o mail corretamente!"
                    snackbarVisible = true
                } else if (email.isBlank() || password.isBlank()) {
                    snackbarText = "Por favor, preencha todos os campos!"
                    snackbarVisible = true
                }else {
                    authViewModel.login(email, password)
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00897B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login", color = Color.White)
        }
        Button(onClick = { authViewModel.navigateToRegister() },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00897B)),
            modifier = Modifier.fillMaxWidth()
        ){
            Text("Ir para Registo", color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = {

                //authViewModel.loginWithGoogle()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Filled.AccountCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Google")
            }
        }
        if (snackbarVisible) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    Button(
                        onClick = { snackbarVisible = false },
                    ) {
                        Text("Fechar")
                    }
                }
            ) {
                Text(snackbarText)
            }
        }
    }

}
@Composable
fun TelaRegisto(authViewModel: AuthViewModel) {
    var nome by remember { mutableStateOf("") }
    var apelido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var snackbarText by remember { mutableStateOf("") }
    var snackbarVisible by remember { mutableStateOf(false) }
    var passwordVisibility by remember { mutableStateOf(true) }
    var passwordStrength by remember { mutableStateOf(AuthViewModel.PasswordStrength.WEAK) }

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
            color = Color(0xFF004D40),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome Próprio") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = apelido,
            onValueChange = { apelido = it },
            label = { Text("Apelido") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = {
                password = it
                passwordStrength = authViewModel.checkPasswordStrength(it)
            },
            label = { Text("Password") },
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisibility)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(image, "toggle password visibility")
                }
            }
        )
        if (password.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                val barColor = when (passwordStrength) {
                    AuthViewModel.PasswordStrength.WEAK -> Color.Red
                    AuthViewModel.PasswordStrength.MEDIUM -> Color.Yellow
                    AuthViewModel.PasswordStrength.STRONG -> Color.Green
                }
                val barPercentage = when (passwordStrength) {
                    AuthViewModel.PasswordStrength.WEAK -> 0.33f
                    AuthViewModel.PasswordStrength.MEDIUM -> 0.66f
                    AuthViewModel.PasswordStrength.STRONG -> 1f
                }

                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    backgroundColor = Color.LightGray,
                    color = barColor,
                    progress = barPercentage
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if(!email.contains("@")) {
                    snackbarText = "Por favor, preencha o email corretamente!"
                    snackbarVisible = true
                } else if (nome.isBlank() || apelido.isBlank() || email.isBlank() || password.isBlank()) {
                    snackbarText = "Por favor, preencha todos os campos!"
                    snackbarVisible = true
                } else {
                    authViewModel.register(nome, apelido, email, password)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00897B))
        ) {
            Text("Registar", color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { authViewModel.navigateToLogin() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00897B))
        ) {
            Text("Voltar Inicio", color = Color.White)
        }

        if (snackbarVisible) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    Button(
                        onClick = { snackbarVisible = false },
                    ) {
                        Text("Fechar")
                    }
                }
            ) {
                Text(snackbarText)
            }
        }
    }
}

@Composable
fun TelaInicialPosLogin(onNavigateToChatGPT: () -> Unit, onNavigateToDiario: () -> Unit, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(top = 52.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "MindMate - Aplicativo de Suporte Mental",
            style = MaterialTheme.typography.h4,
            color = Color(0xFF004D40),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = onNavigateToChatGPT,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00897B))
        ) {
            Text("Conversar com o ChatGPT", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNavigateToDiario,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00897B))
        ) {
            Text("O meu Diário", color = Color.White)
        }
        Button(
            onClick = onLogout, // Utiliza o método de logout passado como parâmetro
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00897B))
        ) {
            Text("Logout", color = Color.White)
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
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(150.dp))

        Text(
            text = "MindMate - Aplicativo de Ajuda Mental",
            fontSize = 24.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF6200EE),
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp)) //

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
                        val trace: Trace = FirebasePerformance.getInstance().newTrace("Chatgpt_Api_Request")
                        trace.start()
                        // Usar a função createChatRequest para criar a requisição com base no inputText
                        val chatRequest = createChatRequest(inputText)

                        // Fazer chamada com a requisição
                        val response = RetrofitInstance.api.postMessage(chatRequest).execute()
                        if (response.isSuccessful) {
                            val chatResponse = response.body()


                            val firstResponseContent = chatResponse?.choices?.firstOrNull()?.message?.content ?: "Resposta vazia"

                            withContext(Dispatchers.Main) {
                                responseText = firstResponseContent
                                inputText = ""
                                Log.d("ChatGPTApp", "Resposta recebida: $responseText")
                            }
                        } else {
                            val errorBody = response.errorBody()?.string() ?: "Erro desconhecido"
                            Log.e("ChatGPTApp", "Erro na resposta da API: $errorBody")
                        }
                        trace.stop()
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
    data class Nota(val titulo: String, val mensagem: String)
    var titulo by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf(listOf<Nota>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Elementos fixos
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.weight(1f) // Peso para manter no topo
        ) {
            Text(
                text = "Meu Diário",
                style = MaterialTheme.typography.h4,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 32.dp, bottom = 16.dp)
            )

            TextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = mensagem,
                onValueChange = { mensagem = it },
                label = { Text("Expressa-te aqui!") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.Black)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (titulo.isNotEmpty() && mensagem.isNotEmpty() && notas.size < 3) {
                        notas = notas + Nota(titulo, mensagem)
                        titulo = ""
                        mensagem = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Adicionar Nota")
            }
        }

        // Notas
        LazyColumn {
            items(notas) { nota ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(nota.titulo, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(nota.mensagem)
                    }
                }
            }
        }

        // Botão Voltar
        Button(
            onClick = { onNavigateBack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Voltar")
        }
    }
}
