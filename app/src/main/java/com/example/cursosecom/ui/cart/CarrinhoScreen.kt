package com.example.cursosecom.ui.cart

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cursosecom.data.model.Curso
import com.example.cursosecom.navigation.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrinhoScreen(
    navController: NavController,
    userId: Int,
    viewModel: CarrinhoViewModel = viewModel()
) {
    LaunchedEffect(key1 = userId) {
        if (userId != 0) {
            viewModel.fetchItensCarrinho(userId)
        }
    }

    val itens by viewModel.itens
    val precoTotal by viewModel.precoTotal
    val context = LocalContext.current
    var isCheckingOut by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Carrinho") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        bottomBar = {
            if (itens.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total:", style = MaterialTheme.typography.titleLarge)
                            Text(
                                "R$ ${"%.2f".format(precoTotal)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                isCheckingOut = true
                                viewModel.finalizarCompra(userId) { sucesso, mensagem ->
                                    Toast.makeText(context, mensagem, Toast.LENGTH_LONG).show()
                                    if (sucesso) {
                                        // Navega para a tela principal, e de lá para "Meus Cursos"
                                        navController.navigate("main_screen/$userId") {
                                            // Limpa toda a pilha de navegação até o login
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                    isCheckingOut = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isCheckingOut // Desabilita o botão durante o processo
                        ) {
                            if (isCheckingOut) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(Icons.Default.ShoppingCartCheckout, contentDescription = "Finalizar Compra")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Finalizar Compra", fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (itens.isEmpty() && !isCheckingOut) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Seu carrinho está vazio.",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(itens) { curso ->
                        CarrinhoItemCard(
                            curso = curso,
                            onRemoveClick = {
                                viewModel.removerItem(userId, curso.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CarrinhoItemCard(
    curso: Curso,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = curso.urlImagem ?: "https://i.imgur.com/l44jv9j.png",
                contentDescription = "Capa do curso ${curso.titulo}",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = curso.titulo,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "R$ ${"%.2f".format(curso.preco)}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            IconButton(onClick = onRemoveClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remover do Carrinho",
                    tint = Color.Gray
                )
            }
        }
    }
}