package com.example.cursosecom.ui.detalhe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cursosecom.R
import com.example.cursosecom.data.model.Aula
import com.example.cursosecom.data.model.CursoDetalhado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CursoDetalheScreen(
    navController: NavController,
    cursoId: Int,
    possuiCurso: Boolean, // <-- PARÂMETRO NECESSÁRIO
    viewModel: CursoDetalheViewModel = viewModel()
) {
    LaunchedEffect(key1 = cursoId) {
        viewModel.fetchCursoDetalhes(cursoId)
    }

    val curso = viewModel.cursoState.value
    val isLoading = viewModel.isLoading.value
    val error = viewModel.error.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = curso?.titulo ?: "Detalhes do Curso", maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        // ALTERADO: A barra inferior agora é condicional
        bottomBar = {
            // O botão de comprar só aparece se o usuário NÃO possuir o curso
            if (!possuiCurso && curso != null) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.height(80.dp)
                ) {
                    Button(
                        onClick = { /* Lógica de compra futura */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.splash_yellow)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Comprar por R$ ${"%.2f".format(curso.preco)}", fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (error != null) {
                Text(text = error, color = Color.Red, modifier = Modifier.padding(16.dp))
            } else if (curso != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        AsyncImage(
                            model = curso.urlImagem ?: "https://i.imgur.com/l44jv9j.png",
                            contentDescription = "Capa do curso ${curso.titulo}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f),
                            contentScale = ContentScale.Crop
                        )
                    }

                    item {
                        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                            Text(curso.titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(curso.subtitulo ?: "", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            InfoPill(icon = Icons.Default.Person, text = curso.nomeInstrutor)
                            // ALTERADO: Usa o dado dinâmico do nível de dificuldade
                            InfoPill(icon = Icons.Default.SignalCellularAlt, text = curso.nivelDificuldade)
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Sobre o curso", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(curso.descricao ?: "Nenhuma descrição disponível.", style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)
                            }
                        }
                    }

                    item {
                        Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp)) {
                            Text("Aulas do Curso", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                            Divider(modifier = Modifier.padding(top = 8.dp))
                        }
                    }

                    items(curso.aulas) { aula ->
                        AulaItem(aula = aula)
                    }
                }
            }
        }
    }
}

// Componente InfoPill (sem alterações)
@Composable
fun InfoPill(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}


// Componente AulaItem (sem alterações)
@Composable
fun AulaItem(aula: Aula) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.PlayCircleOutline,
            contentDescription = "Ícone de aula",
            tint = colorResource(id = R.color.dark_green),
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "${aula.ordem}. ${aula.titulo}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
    Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
}