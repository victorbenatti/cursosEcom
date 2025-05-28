package com.example.cursosecom

import android.graphics.Paint.Align
import android.os.Bundle
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                if (email.value.isBlank() || senha.value.isBlank()) {
                    mensagem.value = "Preencha todos os campos."
                } else if (email.value == "admin@email.com" && senha.value == "123456") {
                    mensagem.value = "Login válido. Redirecionar para tela inicial."
                } else {
                    mensagem.value = "Credenciais inválidas."
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
            color = if (mensagem.value == "Login válido. Redirecionar para tela inicial.") Color(0xFF4CAF50) else Color.Red,
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

        Button(
            onClick = { /* login */ },
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

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    CursosEcomTheme {
        RegisterScreen(navController = rememberNavController())
    }
}