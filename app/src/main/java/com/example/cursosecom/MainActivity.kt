package com.example.cursosecom

// IMPORTS NECESSÁRIOS PARA TUDO FUNCIONAR
// -----------------------------------------------------------------
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import com.example.cursosecom.data.model.Curso
import com.example.cursosecom.ui.cart.CarrinhoScreen
import com.example.cursosecom.ui.cart.CarrinhoViewModel
import com.example.cursosecom.ui.detalhe.CursoDetalheScreen
import com.example.cursosecom.ui.home.HomeViewModel
import com.example.cursosecom.ui.main.MainScreen
import com.example.cursosecom.ui.theme.CursosEcomTheme
import com.example.cursosecom.ui.video.VideoPlayerScreen // NOVO IMPORT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder // NOVO IMPORT
import java.net.URLEncoder
import java.nio.charset.StandardCharsets // NOVO IMPORT


// -----------------------------------------------------------------
// FIM DOS IMPORTS


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CursosEcomTheme {
                val navController = rememberNavController()

                // NavHost principal da aplicação
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController) }
                    composable("cadastro") { RegisterScreen(navController) }

                    composable(
                        route = "main_screen/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        MainScreen(navControllerApp = navController, userId = userId)
                    }

                    composable(
                        "carrinho_screen/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        CarrinhoScreen(navController = navController, userId = userId)
                    }

                    composable(
                        route = "detalhes_curso/{cursoId}/{userId}?possuiCurso={possuiCurso}",
                        arguments = listOf(
                            navArgument("cursoId") { type = NavType.IntType },
                            navArgument("userId") { type = NavType.IntType },
                            navArgument("possuiCurso") {
                                type = NavType.BoolType
                                defaultValue = false
                            }
                        )
                    ) { backStackEntry ->
                        val cursoId = backStackEntry.arguments?.getInt("cursoId") ?: 0
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        val possuiCurso = backStackEntry.arguments?.getBoolean("possuiCurso") ?: false

                        if (cursoId != 0) {
                            CursoDetalheScreen(
                                navController = navController,
                                cursoId = cursoId,
                                userId = userId,
                                possuiCurso = possuiCurso
                            )
                        }
                    }

                    // NOVA ROTA PARA O PLAYER DE VÍDEO
                    composable(
                        route = "video_player/{videoUrl}",
                        arguments = listOf(navArgument("videoUrl") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val encodedUrl = backStackEntry.arguments?.getString("videoUrl") ?: ""
                        // Decodifica a URL para seu formato original
                        val decodedUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
                        if (decodedUrl.isNotEmpty()) {
                            VideoPlayerScreen(navController = navController, videoUrl = decodedUrl)
                        }
                    }
                }
            }
        }
    }
}

// O resto das funções (LoginScreen, RegisterScreen, etc.) continuam abaixo...
// Apenas a HomeScreen precisa de um pequeno ajuste na navegação.

@Composable
fun LoginScreen(navController: NavController) {
    // ... (o código da LoginScreen permanece o mesmo)
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

                        if (status == "ok") {
                            val usuarioJson = obj.getJSONObject("usuario")
                            val usuarioId = usuarioJson.getInt("id_usuario")

                            withContext(Dispatchers.Main) {
                                Toast.makeText(contexto, obj.getString("mensagem"), Toast.LENGTH_SHORT).show()
                                navController.navigate("main_screen/$usuarioId") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        } else {
                            val msg = obj.getString("mensagem")
                            mensagem.value = msg
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
    // ... (código sem alterações)
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
    // ... (código sem alterações)
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
    // ... (código sem alterações)
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
    userId: Int,
    homeViewModel: HomeViewModel = viewModel(),
    carrinhoViewModel: CarrinhoViewModel = viewModel()
) {
    val cursos = homeViewModel.cursosState.value
    val isLoading = homeViewModel.isLoading.value
    val error = homeViewModel.error.value
    val context = LocalContext.current

    // NOVO: A lógica do carrinho agora vive aqui
    LaunchedEffect(key1 = userId) {
        if (userId != 0) {
            carrinhoViewModel.fetchItensCarrinho(userId)
        }
    }
    val totalItensCarrinho = carrinhoViewModel.totalItens.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cursos Disponíveis") },
                // NOVO: Ações na barra superior
                actions = {
                    BadgedBox(
                        badge = {
                            if (totalItensCarrinho > 0) {
                                Badge { Text("$totalItensCarrinho") }
                            }
                        }
                    ) {
                        IconButton(onClick = { navController.navigate("carrinho_screen/$userId") }) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Carrinho de Compras"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        // O resto da tela (Column, LazyColumn, etc.) continua igual
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
                        CursoCard(
                            curso = curso,
                            onCardClick = {
                                navController.navigate("detalhes_curso/${curso.id}/$userId")
                            },
                            showAddToCartButton = true,
                            onAddToCartClick = {
                                if (userId != 0) {
                                    carrinhoViewModel.adicionarItem(userId, curso.id) { sucesso, mensagem ->
                                        Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Faça login para adicionar ao carrinho.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun CursoCard(
    curso: Curso,
    onCardClick: () -> Unit,
    showAddToCartButton: Boolean,
    onAddToCartClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.clickable(onClick = onCardClick)
        ) {
            AsyncImage(
                model = curso.urlImagem ?: "https://i.imgur.com/l44jv9j.png",
                contentDescription = "Capa do curso ${curso.titulo}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                Text(
                    text = curso.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = curso.nomeInstrutor,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                // Seção de progresso, só aparece se o percentual existir
                curso.percentualConcluido?.let { progresso ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Column {
                        LinearProgressIndicator(
                            progress = { progresso / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$progresso% concluído",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }

            // A lógica do botão de preço/adicionar ao carrinho só aparece se não for um curso com progresso
            if (curso.percentualConcluido == null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 8.dp, bottom = 8.dp, top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "R$ ${"%.2f".format(curso.preco)}",
                        fontWeight = FontWeight.ExtraBold,
                        color = colorResource(id = R.color.dark_green),
                        fontSize = 18.sp
                    )
                    if (showAddToCartButton) {
                        IconButton(onClick = onAddToCartClick) {
                            Icon(
                                imageVector = Icons.Default.AddShoppingCart,
                                contentDescription = "Adicionar ao Carrinho",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            } else {
                // Adiciona um espaçamento no final do card para cursos com progresso
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}