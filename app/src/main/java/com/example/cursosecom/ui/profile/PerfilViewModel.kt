package com.example.cursosecom.ui.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cursosecom.data.model.PerfilUsuario
import com.example.cursosecom.ui.home.RetrofitClient
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {
    private val _userProfile = mutableStateOf<PerfilUsuario?>(null)
    val userProfile: State<PerfilUsuario?> = _userProfile

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun fetchUserProfile(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _userProfile.value = RetrofitClient.instance.getPerfilUsuario(userId)
            } catch (e: Exception) {
                _error.value = "Falha ao carregar o perfil do usuário."
            } finally {
                _isLoading.value = false
            }
        }
    }
}