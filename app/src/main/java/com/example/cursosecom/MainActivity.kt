package com.example.cursosecom

// IMPORTS NECESSÁRIOS PARA TUDO FUNCIONAR
// -----------------------------------------------------------------
// Imports básicos do Android e de ferramentas
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

// Imports do Jetpack Compose para Layout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

// Imports do Jetpack Compose para Componentes (Material 3)
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar

// Imports do Jetpack Compose para o Runtime (estados, etc.)
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

// Imports para Modifiers e UI
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Imports de Arquitetura (ViewModel)
import androidx.lifecycle.viewmodel.compose.viewModel

// Imports de Navegação
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Biblioteca de Imagem (Coil)
import coil.compose.AsyncImage

// Suas classes de Dados e ViewModel
import com.example.cursosecom.data.model.Curso
import com.example.cursosecom.ui.home.HomeViewModel
import com.example.cursosecom.ui.theme.CursosEcomTheme

// Imports do Kotlin e Java
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

// -----------------------------------------------------------------
// FIM DOS IMPORTS


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CursosEcomTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable("login") { LoginScreen(navController) }
                    composable("cadastro") { RegisterScreen(navController) }
                    composable("home") { HomeScreen(navController) }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    // Código da LoginScreen (sem alterações)
    val email = remember { mutableStateOf("") }
    val senha = remember { mutableStateOf("") }
    val mensagem = remember { mutableStateOf("") }
    val contexto = LocalContext.current
    val escopo    = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Login",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.splash_yellow),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 150.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("E-mail") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color(0xFFBDBDBD),
                unfocusedContainerColor = Color(0xFFF5F5F5),
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = senha.value,
            onValueChange = { senha.value = it },
            label = { Text("Senha") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color(0xFFBDBDBD),
                unfocusedContainerColor = Color(0xFFF5F5F5),
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                escopo.launch {
                    if (email.value.isBlank() || senha.value.isBlank()) {
                        mensagem.value = "Preencha todos os campos."
                        return@launch
                    }
                    val resposta = enviarLogin(email.value, senha.value)
                    try {
                        val obj = JSONObject(resposta)
                        val status = obj.getString("status")
                        val msg    = obj.getString("mensagem")
                        mensagem.value = msg
                        if (status == "ok") {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(contexto, msg, Toast.LENGTH_SHORT).show()
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        mensagem.value = "Resposta inválida do servidor."
                        Log.e("LoginScreen", "Erro ao parsear JSON: ${e.localizedMessage}")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.splash_yellow)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Logar", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = mensagem.value,
            color = if (mensagem.value.contains("sucesso", ignoreCase = true) && !mensagem.value.contains("inválida", ignoreCase = true) ) Color(0xFF4CAF50) else Color.Red,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Não possui uma conta?", fontSize = 14.sp)

            TextButton(onClick = {
                navController.navigate("cadastro")
            }) {
                Text(
                    text = "Cadastre-se",
                    color = colorResource(id = R.color.splash_yellow),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

suspend fun enviarLogin(email: String, senha: String): String = withContext(Dispatchers.IO) {
    // Código da função enviarLogin (sem alterações)
    var conn: HttpURLConnection? = null
    try {
        val url = URL("http://10.0.2.2:80/api-cursos/login.php")
        conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 15_000
            readTimeout    = 15_000
            setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        }

        val dados = "email=${URLEncoder.encode(email, "UTF-8")}" +
                "&senha=${URLEncoder.encode(senha, "UTF-8")}"

        conn.outputStream.bufferedWriter().use { writer ->
            writer.write(dados)
            writer.flush()
        }

        val code = conn.responseCode
        Log.d("Login", "HTTP response code: $code")

        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        return@withContext stream.bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        Log.e("Login", "Erro ao enviar login: ${e.message}", e)
        return@withContext "{\"status\":\"erro\",\"mensagem\":\"Falha de rede ou erro interno: ${e.message}\"}"
    } finally {
        conn?.disconnect()
    }
}

@Composable
fun RegisterScreen(navController: NavController) {
    // Código da RegisterScreen (sem alterações)
    val nome = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val senha = remember { mutableStateOf("") }
    val confirmacaoSenha = remember { mutableStateOf("") }
    val mensagemErro = remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Cadastro",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.splash_yellow),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        )
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = nome.value,
            onValueChange = { nome.value = it },
            label = { Text("Nome completo") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color(0xFFBDBDBD),
                unfocusedContainerColor = Color(0xFFF5F5F5),
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Insira seu e-mail") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color(0xFFBDBDBD),
                unfocusedContainerColor = Color(0xFFF5F5F5),
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = senha.value,
            onValueChange = { senha.value = it },
            label = { Text("Crie uma senha") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color(0xFFBDBDBD),
                unfocusedContainerColor = Color(0xFFF5F5F5),
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = confirmacaoSenha.value,
            onValueChange = { confirmacaoSenha.value = it },
            label = { Text("Confirme sua senha") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color(0xFFBDBDBD),
                unfocusedContainerColor = Color(0xFFF5F5F5),
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (mensagemErro.value.isNotEmpty()) {
            Text(
                text = mensagemErro.value,
                color = Color.Red,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Ao clicar em Criar Conta, voce concorda com todos " +
                        "os nossos termos da política de privacidade",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val contexto = LocalContext.current
        val escopo = rememberCoroutineScope()

        Button(
            onClick = {
                if (nome.value.isBlank() || email.value.isBlank() || senha.value.isBlank() || confirmacaoSenha.value.isBlank()) {
                    mensagemErro.value = "Todos os campos são obrigatórios."
                    return@Button
                }
                if (senha.value != confirmacaoSenha.value) {
                    mensagemErro.value = "As senhas não coincidem."
                    return@Button
                }
                mensagemErro.value = ""

                escopo.launch {
                    val resposta = enviarCadastro(nome.value, email.value, senha.value)

                    try {
                        val json = JSONObject(resposta)
                        val status = json.getString("status")
                        val mensagemServidor = json.getString("mensagem")

                        withContext(Dispatchers.Main) {
                            Toast.makeText(contexto, mensagemServidor, Toast.LENGTH_LONG).show()
                            if (status == "ok") {
                                navController.navigate("login") {
                                    popUpTo("cadastro") { inclusive = true }
                                }
                            } else {
                                mensagemErro.value = mensagemServidor
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(contexto, "Erro ao processar cadastro. Tente novamente.", Toast.LENGTH_LONG).show()
                            mensagemErro.value = "Erro de comunicação com o servidor."
                            Log.e("RegisterScreen", "Erro ao parsear JSON ou cadastrar: ${e.localizedMessage}")
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.splash_yellow)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Cadastrar", color = Color.White)
        }

        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Já possui uma conta?", fontSize = 14.sp)

            TextButton(onClick = {
                navController.navigate("login")
            }) {
                Text(
                    text = "Faça o login",
                    color = colorResource(id = R.color.splash_yellow),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

suspend fun enviarCadastro(nome: String, email: String, senha: String): String = withContext(Dispatchers.IO) {
    // Código da função enviarCadastro (sem alterações)
    var conn: HttpURLConnection? = null
    try {
        val url = URL("http://10.0.2.2:80/api-cursos/cadastro.php")
        conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 15_000
            readTimeout = 15_000
            setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        }

        val dados = "nome=${URLEncoder.encode(nome, "UTF-8")}" +
                "&email=${URLEncoder.encode(email, "UTF-8")}" +
                "&senha=${URLEncoder.encode(senha, "UTF-8")}"

        conn.outputStream.bufferedWriter().use { writer ->
            writer.write(dados)
            writer.flush()
        }

        val code = conn.responseCode
        Log.d("Cadastro", "HTTP response code: $code")

        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        return@withContext stream.bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        Log.e("Cadastro", "Falha ao enviar cadastro: ${e.message}", e)
        return@withContext "{\"status\":\"erro\",\"mensagem\":\"Falha de rede ou erro interno no cadastro: ${e.message}\"}"
    } finally {
        conn?.disconnect()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel()
) {
    val cursos = homeViewModel.cursosState.value
    val isLoading = homeViewModel.isLoading.value
    val error = homeViewModel.error.value

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cursos Disponíveis") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (error != null) {
                Text(
                    text = error,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else if (cursos.isEmpty()) {
                Text(
                    text = "Nenhum curso encontrado.",
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(cursos) { curso ->
                        CursoCard(curso = curso, onClick = {
                            // Futuramente, navegar para a tela de detalhes do curso
                            // navController.navigate("detalhes_curso/${curso.id}")
                        })
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CursoCard(curso: Curso, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp) // Cantos um pouco mais arredondados para estilo
    ) {
        // 1. O layout principal agora é uma Coluna para ter a imagem no topo
        Column {
            // 2. A imagem agora ocupa toda a largura do card, atuando como uma thumbnail
            AsyncImage(
                model = curso.urlImagem ?: "https://i.imgur.com/l44jv9j.png",
                contentDescription = "Capa do curso ${curso.titulo}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f), // Proporção de vídeo/thumbnail (16:9)
                contentScale = ContentScale.Crop // Garante que a imagem preencha o espaço sem distorcer
            )

            // 3. Uma outra Coluna para agrupar as informações de texto com um padding geral
            Column(modifier = Modifier.padding(16.dp)) {
                // Título do curso com mais destaque
                Text(
                    text = curso.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 2, // No máximo 2 linhas para o título
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis // Adiciona "..." se o título for muito grande
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Nome do instrutor, mais sutil
                Text(
                    text = curso.nomeInstrutor,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Preço com bastante destaque
                Text(
                    text = "R$ ${"%.2f".format(curso.preco)}",
                    fontWeight = FontWeight.ExtraBold, // Fonte ainda mais forte para o preço
                    color = colorResource(id = R.color.dark_green),
                    fontSize = 18.sp
                )
            }
        }
    }
}