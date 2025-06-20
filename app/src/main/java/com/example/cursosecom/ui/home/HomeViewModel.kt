package com.example.cursosecom.ui.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cursosecom.data.model.Curso
import com.example.cursosecom.data.model.CursoDetalhado
import com.example.cursosecom.data.model.PerfilUsuario
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// --- Interface para definir os endpoints da API com Retrofit ---
interface ApiService {
    @GET("listar_cursos.php")
    suspend fun getCursos(): List<Curso>

    @GET("detalhes_curso.php")
    suspend fun getCursoDetalhes(@Query("id") cursoId: Int): CursoDetalhado

    @GET("perfil.php")
    suspend fun getPerfilUsuario(@Query("usuario_id") usuarioId: Int): PerfilUsuario

    @GET("meus_cursos.php")
    suspend fun getMeusCursos(@Query("usuario_id") usuarioId: Int): List<Curso>

    // --- NOVOS ENDPOINTS PARA O CARRINHO ---

    @GET("listar_carrinho.php")
    suspend fun getItensCarrinho(@Query("usuario_id") usuarioId: Int): List<Curso>

    @POST("adicionar_carrinho.php")
    @FormUrlEncoded
    suspend fun adicionarAoCarrinho(
        @Field("usuario_id") usuarioId: Int,
        @Field("curso_id") cursoId: Int
    ): ResponseStatus

    @POST("remover_carrinho.php")
    @FormUrlEncoded
    suspend fun removerDoCarrinho(
        @Field("usuario_id") usuarioId: Int,
        @Field("curso_id") cursoId: Int
    ): ResponseStatus
}

// NOVO: Data class para representar respostas de status simples (ex: ok/erro)
data class ResponseStatus(val status: String, val mensagem: String)


// --- Objeto para criar uma instância única do Retrofit ---
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2/api-cursos/" // Seu IP e pasta da API

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
        retrofit.create(ApiService::class.java)
    }
}


// --- ViewModel para a HomeScreen ---
class HomeViewModel : ViewModel() {

    // Estado para a UI: pode ser Carregando, Sucesso com dados, ou Erro
    private val _cursosState = mutableStateOf<List<Curso>>(emptyList())
    val cursosState: State<List<Curso>> = _cursosState

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error


    init {
        // Busca os cursos assim que o ViewModel é criado
        fetchCursos()
    }

    fun fetchCursos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Chama a API de forma segura
                val cursos = RetrofitClient.instance.getCursos()
                _cursosState.value = cursos
                Log.d("HomeViewModel", "Cursos carregados: ${cursos.size}")
            } catch (e: Exception) {
                // Em caso de erro de rede ou parse
                _error.value = "Falha ao carregar os cursos: ${e.message}"
                Log.e("HomeViewModel", "Erro ao buscar cursos", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
