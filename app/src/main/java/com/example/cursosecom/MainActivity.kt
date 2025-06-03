package com.example.cursosecom

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.cursosecom.R
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

        // Validação Login (Local)
        Button(
            onClick = {
                escopo.launch {
                    if (email.value.isBlank() || senha.value.isBlank()) {
                        mensagem.value = "Preencha todos os campos."
                        return@launch
                    }
                    val resposta = enviarLogin(email.value, senha.value)
                    // parse do JSON
                    try {
                        val obj = JSONObject(resposta)
                        val status = obj.getString("status")
                        val msg    = obj.getString("mensagem")
                        mensagem.value = msg
                        if (status == "ok") {
                            // sucesso: exibe Toast e navega
                            withContext(Dispatchers.Main) {
                                Toast.makeText(contexto, msg, Toast.LENGTH_SHORT).show()
                                // navController.navigate("telaPrincipal")
                            }
                        }
                    } catch (e: Exception) {
                        mensagem.value = "Resposta inválida do servidor."
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
            color = if (mensagem.value == "Login realizado com sucesso") Color(0xFF4CAF50) else Color.Red,
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

suspend fun enviarLogin(
    email: String,
    senha: String
): String = withContext(Dispatchers.IO) {
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
        Log.e("Login", "Erro ao enviar login", e)
        return@withContext "{\"status\":\"erro\",\"mensagem\":\"Falha de rede\"}"
    } finally {
        conn?.disconnect()
    }
}


@Composable
fun RegisterScreen(navController: NavController) {
    var nome = remember { mutableStateOf("") }
    var email = remember { mutableStateOf("") }
    var senha = remember { mutableStateOf("") }
    var confirmacaoSenha = remember { mutableStateOf("") }


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
        val mensagem = remember { mutableStateOf("") }

        Button(
            onClick = {
                escopo.launch {
                    val resposta = enviarCadastro(nome.value, email.value, senha.value)
                    // Toast.makeText(contexto, "Resposta crua: $resposta", Toast.LENGTH_LONG).show()

                    try {
                        val json = JSONObject(resposta)
                        val status = json.getString("status")
                        val mensagemServidor = json.getString("mensagem")

                        Toast.makeText(contexto, mensagemServidor, Toast.LENGTH_LONG).show()

                        if (status == "ok") {
                            navController.navigate("login")
                        }
                    } catch (e: Exception) {
                        Toast.makeText(contexto, "Erro ao cadastrar. Tente novamente.", Toast.LENGTH_LONG).show()
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

suspend fun enviarCadastro(
    nome: String,
    email: String,
    senha: String
): String = withContext(Dispatchers.IO) {
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

        // monta e envia o body
        val dados = "nome=${URLEncoder.encode(nome, "UTF-8")}" +
                "&email=${URLEncoder.encode(email, "UTF-8")}" +
                "&senha=${URLEncoder.encode(senha, "UTF-8")}"

        conn.outputStream.bufferedWriter().use { writer ->
            writer.write(dados)
            writer.flush()
        }

        // lê o código HTTP
        val code = conn.responseCode
        Log.d("Cadastro", "HTTP response code: $code")

        // decide de onde ler o stream
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        return@withContext stream.bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        Log.e("Cadastro", "Falha ao enviar cadastro", e)
        return@withContext "erro"
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