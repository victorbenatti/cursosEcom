package com.example.cursosecom.ui.detalhe

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cursosecom.data.model.CursoDetalhado
import com.example.cursosecom.ui.home.RetrofitClient
import kotlinx.coroutines.launch

class CursoDetalheViewModel : ViewModel() {
    private val _cursoState = mutableStateOf<CursoDetalhado?>(null)
    val cursoState: State<CursoDetalhado?> = _cursoState

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun fetchCursoDetalhes(cursoId: Int, userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // ALTERADO: Passamos o userId para a chamada da API
                _cursoState.value = RetrofitClient.instance.getCursoDetalhes(cursoId, userId)
            } catch (e: Exception) {
                _error.value = "Falha ao carregar detalhes do curso."
            } finally {
                _isLoading.value = false
            }
        }
    }

    // NOVA FUNÇÃO
    fun marcarAulaComoConcluida(
        userId: Int,
        aulaId: Int,
        concluida: Boolean,
        onResult: (sucesso: Boolean, mensagem: String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val status = if (concluida) 1 else 0
                val response = RetrofitClient.instance.marcarAulaComoConcluida(userId, aulaId, status)
                if (response.status == "ok") {
                    // Atualiza o estado local para a UI responder imediatamente
                    val cursoAtual = _cursoState.value
                    cursoAtual?.let {
                        val aulasAtualizadas = it.aulas.map { aula ->
                            if (aula.id == aulaId) aula.copy(concluida = concluida) else aula
                        }
                        _cursoState.value = it.copy(aulas = aulasAtualizadas)
                    }
                    onResult(true, response.mensagem)
                } else {
                    onResult(false, response.mensagem)
                }
            } catch (e: Exception) {
                onResult(false, "Erro de conexão ao marcar aula.")
            }
        }
    }
}