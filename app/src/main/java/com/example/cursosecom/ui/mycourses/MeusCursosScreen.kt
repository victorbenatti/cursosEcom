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
import com.example.cursosecom.CursoCard // Importa o CursoCard de MainActivity
import com.example.cursosecom.data.model.Curso // Importa a data class Curso

/**
 * Tela que exibe a lista de cursos adquiridos pelo usuário.
 *
 * @param navController O controlador de navegação principal para ir para outras telas (como detalhes).
 * @param userId O ID do usuário logado para buscar os cursos corretos.
 * @param viewModel O ViewModel que gerencia a busca e o estado dos dados desta tela.
 */
@Composable
fun MeusCursosScreen(
    navController: NavController,
    userId: Int,
    viewModel: MeusCursosViewModel = viewModel()
) {
    // Efeito que é executado uma vez quando a tela é carregada (ou quando o userId muda).
    // Ele dispara a busca dos cursos do usuário na API.
    LaunchedEffect(key1 = userId) {
        if (userId != 0) {
            viewModel.fetchMeusCursos(userId)
        }
    }

    // Observa os estados do ViewModel para re-renderizar a tela quando eles mudarem.
    val cursos = viewModel.cursos.value
    val isLoading = viewModel.isLoading.value
    val error = viewModel.error.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Lógica de UI para exibir um indicador de carregamento, uma mensagem de erro,
        // uma mensagem de carrinho vazio ou a lista de cursos.
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
            // Lista rolável que exibe os cursos adquiridos.
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
                items(cursos) { curso ->
                    // Reutiliza o componente `CursoCard` para exibir cada curso.
                    CursoCard(
                        curso = curso,
                        onCardClick = {
                            // Define a ação de clique para navegar para os detalhes do curso,
                            // passando os parâmetros necessários na rota.
                            navController.navigate("detalhes_curso/${curso.id}/$userId?possuiCurso=true")
                        },
                        showAddToCartButton = false, // Não mostra o botão de carrinho nesta tela.
                        onAddToCartClick = { /* Não faz nada aqui */ }
                    )
                }
            }
        }
    }
}
