package com.example.cursosecom.ui.mycourses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cursosecom.CursoCard // Importe o CursoCard que já existe

@Composable
fun MeusCursosScreen(
    navController: NavController,
    userId: Int,
    viewModel: MeusCursosViewModel = viewModel()
) {
    LaunchedEffect(key1 = userId) {
        viewModel.fetchMeusCursos(userId)
    }

    val cursos = viewModel.cursos.value
    val isLoading = viewModel.isLoading.value
    val error = viewModel.error.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else if (error != null) {
            Text(
                text = error,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        } else if (cursos.isEmpty()) {
            Text(
                text = "Você ainda não possui nenhum curso.",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
                items(cursos) { curso ->
                    // Reutilizamos o mesmo CursoCard da HomeScreen!
                    CursoCard(curso = curso, onClick = {
                        // ALTERADO: Adicionamos o parâmetro ?possuiCurso=true na rota
                        navController.navigate("detalhes_curso/${curso.id}?possuiCurso=true")
                    })
                }
            }
        }
    }
}