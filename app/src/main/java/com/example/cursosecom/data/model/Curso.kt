package com.example.cursosecom.data.model

import com.google.gson.annotations.SerializedName

// Esta data class representa um curso, exatamente como ele vem da API (vw_cursosdisponiveis)
data class Curso(
    @SerializedName("id_curso")
    val id: Int,

    @SerializedName("titulo_curso")
    val titulo: String,

    @SerializedName("subtitulo_curso")
    val subtitulo: String?, // Pode ser nulo

    @SerializedName("preco_curso")
    val preco: Double,

    @SerializedName("nome_instrutor")
    val nomeInstrutor: String,

    @SerializedName("url_imagem_capa_curso")
    val urlImagem: String? // Pode ser nulo
)