package com.example.cursosecom.data.model

import com.google.gson.annotations.SerializedName

data class CursoDetalhado(
    @SerializedName("id_curso")
    val id: Int,
    @SerializedName("titulo_curso")
    val titulo: String,
    @SerializedName("subtitulo_curso")
    val subtitulo: String?,
    @SerializedName("descricao_detalhada_curso") // Adicionamos a descrição
    val descricao: String?,
    @SerializedName("preco_curso")
    val preco: Double,
    @SerializedName("nome_instrutor")
    val nomeInstrutor: String,
    @SerializedName("url_imagem_capa_curso")
    val urlImagem: String?,
    @SerializedName("aulas") // A lista de aulas que vem da API
    val aulas: List<Aula>
)