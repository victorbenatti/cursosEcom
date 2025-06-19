package com.example.cursosecom.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cursosecom.R

// ASSINATURA DA FUNÇÃO ALTERADA
@Composable
fun PerfilScreen(
    navController: NavController,
    userId: Int,
    viewModel: PerfilViewModel = viewModel()
) {
    // NOVO: Efeito para buscar os dados do perfil quando a tela é carregada
    LaunchedEffect(key1 = userId) {
        if (userId != 0) { // Garante que não é um ID inválido
            viewModel.fetchUserProfile(userId)
        }
    }

    // NOVO: Observa os estados do ViewModel
    val userProfile = viewModel.userProfile.value
    val isLoading = viewModel.isLoading.value
    val error = viewModel.error.value

    // O Column principal agora envolve a lógica de estado
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Centraliza o indicador de progresso
    ) {
        // NOVO: Lógica para exibir o progresso, erro ou o conteúdo
        if (isLoading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text(text = error, color = Color.Red)
        } else if (userProfile != null) {
            // O conteúdo do perfil só é exibido se userProfile não for nulo
            // O Arrangement.Top é para alinhar o conteúdo no topo quando ele aparecer
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // --- Cabeçalho do Perfil (usando dados dinâmicos) ---
                Spacer(modifier = Modifier.height(32.dp))

                AsyncImage(
                    model = userProfile.urlFotoPerfil ?: R.drawable.ic_launcher_foreground, // ALTERADO
                    contentDescription = "Foto de Perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = userProfile.nomeCompleto, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) // ALTERADO
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = userProfile.email, style = MaterialTheme.typography.bodyLarge, color = Color.Gray) // ALTERADO

                Spacer(modifier = Modifier.height(32.dp))
                Divider()

                // --- Menu de Opções ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    ProfileMenuItem(
                        icon = Icons.Default.AccountCircle,
                        text = "Editar Perfil",
                        onClick = { /* Lógica futura */ }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.Lock,
                        text = "Alterar Senha",
                        onClick = { /* Lógica futura */ }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.Settings,
                        text = "Configurações",
                        onClick = { /* Lógica futura */ }
                    )
                }

                Spacer(modifier = Modifier.weight(1f)) // Empurra o botão de Sair para baixo

                // --- Botão de Logout ---
                Button(
                    onClick = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sair")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sair da Conta")
                    }
                }
            }
        }
    }
}

// O componente reutilizável continua o mesmo
@Composable
private fun ProfileMenuItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = text, modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
    Divider()
}