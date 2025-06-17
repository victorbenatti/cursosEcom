package com.example.cursosecom.ui.detalhe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Verifique se este import existe
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cursosecom.data.model.Aula

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CursoDetalheScreen(
    navController: NavController,
    cursoId: Int,
    viewModel: CursoDetalheViewModel = viewModel()
) {
    // ... (o início da função continua igual)
    LaunchedEffect(key1 = cursoId) {
        viewModel.fetchCursoDetalhes(cursoId)
    }

    val curso = viewModel.cursoState.value
    val isLoading = viewModel.isLoading.value
    val error = viewModel.error.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = curso?.titulo ?: "Detalhes do Curso") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
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
                Text(text = error, color = Color.Red)
            } else if (curso != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // ... (item da imagem e dos detalhes continuam iguais)
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
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(curso.titulo, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(curso.subtitulo ?: "", fontSize = 16.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Por ${curso.nomeInstrutor}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(curso.descricao ?: "Nenhuma descrição disponível.", fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { /* Lógica de compra futura */ }, modifier = Modifier.fillMaxWidth()) {
                                Text("Comprar por R$ ${"%.2f".format(curso.preco)}", fontSize = 18.sp)
                            }
                        }
                    }

                    item {
                        Column(Modifier.padding(horizontal = 16.dp)) {
                            Divider(modifier = Modifier.padding(vertical = 16.dp))
                            Text("Aulas do Curso", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    items(curso.aulas) { aula ->
                        AulaItem(aula = aula)
                    }
                }
            }
        }
    }
} // <<-- FIM DA FUNÇÃO CursoDetalheScreen. A CHAVE ESTAVA DEPOIS DE AulaItem.

// A FUNÇÃO AulaItem AGORA ESTÁ FORA, NO NÍVEL SUPERIOR DO ARQUIVO.
@Composable
fun AulaItem(aula: Aula) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("${aula.ordem}. ${aula.titulo}", fontSize = 16.sp)
        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}