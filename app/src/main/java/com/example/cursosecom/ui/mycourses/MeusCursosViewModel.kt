package com.example.cursosecom.ui.mycourses

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cursosecom.data.model.Curso
import com.example.cursosecom.ui.home.RetrofitClient
import kotlinx.coroutines.launch

class MeusCursosViewModel : ViewModel() {
    private val _cursos = mutableStateOf<List<Curso>>(emptyList())
    val cursos: State<List<Curso>> = _cursos

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun fetchMeusCursos(userId: Int) {
        if (userId == 0) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _cursos.value = RetrofitClient.instance.getMeusCursos(userId)
            } catch (e: Exception) {
                _error.value = "Falha ao carregar seus cursos."
            } finally {
                _isLoading.value = false
            }
        }
    }
}