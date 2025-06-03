package com.example.cursosecom

// Seus imports existentes ...
import android.graphics.Paint.Align
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold // Importante para a HomeScreen
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar // Exemplo, caso queira usar um TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api // Para TopAppBar, se usar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cursosecom.ui.theme.CursosEcomTheme
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
// Removi R daqui, pois ele é referenciado como com.example.cursosecom.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CursosEcomTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController) }
                    composable("cadastro") { RegisterScreen(navController) }
                    composable("home") { HomeScreen(navController) } // Nova rota para a tela de início
                }
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
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
            color = colorResource(id = com.example.cursosecom.R.color.splash_yellow),
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
            // Adicione o import para KeyboardType e VisualTransformation se quiser ocultar a senha
            // keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            // visualTransformation = PasswordVisualTransformation(),
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
                                // Navega para a tela home e limpa a pilha de volta até 'login'
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
                containerColor = colorResource(id = com.example.cursosecom.R.color.splash_yellow)
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
                    color = colorResource(id = com.example.cursosecom.R.color.splash_yellow),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

suspend fun enviarLogin(
    email: String,
    senha: String
): String = withContext(Dispatchers.IO) {
    var conn: HttpURLConnection? = null
    try {
        val url = URL("http://10.0.2.2:80/api-cursos/login.php") // Use 10.0.2.2 para emulador Android acessar localhost da máquina host
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
        // Retornar um JSON de erro mais consistente
        return@withContext "{\"status\":\"erro\",\"mensagem\":\"Falha de rede ou erro interno: ${e.message}\"}"
    } finally {
        conn?.disconnect()
    }
}

// Sua RegisterScreen e enviarCadastro permanecem iguais
@Composable
fun RegisterScreen(navController: NavController) {
    val nome = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") } // Corrigido para mutableStateOf
    val senha = remember { mutableStateOf("") }
    val confirmacaoSenha = remember { mutableStateOf("") }
    val mensagemErro = remember { mutableStateOf("") } // Para feedback de validação/erro


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Cadastro",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = com.example.cursosecom.R.color.splash_yellow),
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
            // keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            // visualTransformation = PasswordVisualTransformation(),
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
            // keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            // visualTransformation = PasswordVisualTransformation(),
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
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
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
        // val mensagem = remember { mutableStateOf("") } // Removido pois mensagemErro é usado

        Button(
            onClick = {
                // Validações básicas no cliente
                if (nome.value.isBlank() || email.value.isBlank() || senha.value.isBlank() || confirmacaoSenha.value.isBlank()) {
                    mensagemErro.value = "Todos os campos são obrigatórios."
                    return@Button
                }
                if (senha.value != confirmacaoSenha.value) {
                    mensagemErro.value = "As senhas não coincidem."
                    return@Button
                }
                // Adicione mais validações se necessário (ex: formato do email, força da senha)
                mensagemErro.value = "" // Limpa mensagens de erro anteriores

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
                                    popUpTo("cadastro") { inclusive = true } // Limpa cadastro da pilha
                                }
                            } else {
                                mensagemErro.value = mensagemServidor // Mostra erro do servidor
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
                containerColor = colorResource(id = com.example.cursosecom.R.color.splash_yellow)
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
                    color = colorResource(id = com.example.cursosecom.R.color.splash_yellow),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}


suspend fun enviarCadastro(
    nome: String,
    email: String,
    senha: String
): String = withContext(Dispatchers.IO) {
    var conn: HttpURLConnection? = null
    try {
        val url = URL("http://10.0.2.2:80/api-cursos/cadastro.php") // Use 10.0.2.2 para emulador Android
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

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    CursosEcomTheme {
        RegisterScreen(navController = rememberNavController())
    }
}

// ---------- NOVA TELA DE INÍCIO (HOME SCREEN) ----------
@OptIn(ExperimentalMaterial3Api::class) // Necessário se for usar TopAppBar do Material 3
@Composable
fun HomeScreen(navController: NavController) {
    val contexto = LocalContext.current

    // O Scaffold fornece uma estrutura básica de layout Material Design.
    // Inclui suporte para TopAppBar, BottomAppBar, FloatingActionButton, Drawer, etc.
    Scaffold(
        topBar = {
            // Você pode adicionar uma barra superior se desejar. Exemplo:
            // TopAppBar(title = { Text("Cursos ECOM") })
        }
    ) { paddingValues -> // paddingValues contém o padding necessário para o conteúdo não sobrepor as barras
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplica o padding do Scaffold
                .padding(16.dp), // Adiciona um padding interno para o conteúdo
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bem-vindo(a)!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = com.example.cursosecom.R.color.splash_yellow)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Aqui você encontrará os melhores cursos digitais.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Exemplo: Botão para listar cursos (funcionalidade futura)
            Button(
                onClick = {
                    Toast.makeText(contexto, "Funcionalidade de listar cursos em breve!", Toast.LENGTH_SHORT).show()
                    // Futuramente, navegaria para uma tela de listagem de cursos ou carregaria dados aqui
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Ocupa 80% da largura
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = com.example.cursosecom.R.color.splash_yellow)
                )
            ) {
                Text("Ver Cursos", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão de Logout
            Button(
                onClick = {
                    // Lógica de logout:
                    // 1. Limpar quaisquer dados de sessão/tokens (se aplicável no futuro)
                    // 2. Navegar de volta para a tela de login
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true } // Remove a HomeScreen da pilha
                        launchSingleTop = true // Evita múltiplas instâncias da tela de login se já estiver no topo
                    }
                    Toast.makeText(contexto, "Logout realizado.", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray // Uma cor diferente para o logout
                )
            ) {
                Text("Logout", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CursosEcomTheme {
        // Para o preview funcionar corretamente, precisamos de um NavController.
        // Podemos usar um NavController "dummy" aqui.
        val navController = rememberNavController()
        HomeScreen(navController = navController)
    }
}