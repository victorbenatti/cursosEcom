package com.example.cursosecom.ui.cart

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cursosecom.data.model.Curso
import com.example.cursosecom.ui.home.ResponseStatus
import com.example.cursosecom.ui.home.RetrofitClient
import kotlinx.coroutines.launch

class CarrinhoViewModel : ViewModel() {
    private val _itens = mutableStateOf<List<Curso>>(emptyList())
    val itens: State<List<Curso>> = _itens

    // Estado derivado para calcular o total de itens automaticamente
    val totalItens = derivedStateOf { _itens.value.size }
    // Estado derivado para calcular o preço total automaticamente
    val precoTotal = derivedStateOf { _itens.value.sumOf { it.preco } }

    fun fetchItensCarrinho(userId: Int) {
        viewModelScope.launch {
            try {
                _itens.value = RetrofitClient.instance.getItensCarrinho(userId)
            } catch (e: Exception) { /* Tratar erro de forma apropriada */ }
        }
    }

    fun removerItem(userId: Int, cursoId: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.removerDoCarrinho(userId, cursoId)
                fetchItensCarrinho(userId) // Atualiza a lista após remover
            } catch (e: Exception) { /* Tratar erro */ }
        }
    }

    // Função para ser chamada de outras telas para adicionar um item
    fun adicionarItem(userId: Int, cursoId: Int, onResult: (sucesso: Boolean, mensagem: String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.adicionarAoCarrinho(userId, cursoId)
                if (response.status == "ok") {
                    // Atualiza a lista do carrinho em background para o contador ser atualizado
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
}
