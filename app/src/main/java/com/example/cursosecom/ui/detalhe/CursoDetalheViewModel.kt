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

    fun fetchCursoDetalhes(cursoId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _cursoState.value = RetrofitClient.instance.getCursoDetalhes(cursoId)
            } catch (e: Exception) {
                _error.value = "Falha ao carregar detalhes do curso."
            } finally {
                _isLoading.value = false
            }
        }
    }
}