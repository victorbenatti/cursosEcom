package com.example.cursosecom.ui.cart

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cursosecom.data.model.Curso
import com.example.cursosecom.ui.home.RetrofitClient
import kotlinx.coroutines.launch

class CarrinhoViewModel : ViewModel() {
    private val _itens = mutableStateOf<List<Curso>>(emptyList())
    val itens: State<List<Curso>> = _itens

    val totalItens = derivedStateOf { _itens.value.size }
    val precoTotal = derivedStateOf { _itens.value.sumOf { it.preco } }

    fun fetchItensCarrinho(userId: Int) {
        viewModelScope.launch {
            try {
                _itens.value = RetrofitClient.instance.getItensCarrinho(userId)
            } catch (e: Exception) {
                // Em um app real, seria bom mostrar um erro na UI
                _itens.value = emptyList()
            }
        }
    }

    fun removerItem(userId: Int, cursoId: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.removerDoCarrinho(userId, cursoId)
                fetchItensCarrinho(userId)
            } catch (e: Exception) { /* Tratar erro */ }
        }
    }

    fun adicionarItem(userId: Int, cursoId: Int, onResult: (sucesso: Boolean, mensagem: String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.adicionarAoCarrinho(userId, cursoId)
                if (response.status == "ok") {
                    fetchItensCarrinho(userId)
                    onResult(true, response.mensagem)
                } else {
                    onResult(false, response.mensagem)
                }
            } catch (e: Exception) {
                onResult(false, "Erro de conexão ao tentar adicionar ao carrinho.")
            }
        }
    }

    // NOVA FUNÇÃO
    fun finalizarCompra(userId: Int, onResult: (sucesso: Boolean, mensagem: String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.finalizarCompra(userId)
                if (response.status == "ok") {
                    // Limpa o carrinho localmente para a UI atualizar
                    _itens.value = emptyList()
                    onResult(true, response.mensagem)
                } else {
                    onResult(false, response.mensagem)
                }
            } catch (e: Exception) {
                onResult(false, "Falha na conexão ao finalizar a compra.")
            }
        }
    }
}