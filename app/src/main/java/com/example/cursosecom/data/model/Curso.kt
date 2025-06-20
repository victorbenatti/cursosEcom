package com.example.cursosecom.data.model

import com.google.gson.annotations.SerializedName

data class Curso(
    @SerializedName("id_curso")
    val id: Int,

    @SerializedName("titulo_curso")
    val titulo: String,

    @SerializedName("subtitulo_curso")
    val subtitulo: String?,

    @SerializedName("preco_curso")
    val preco: Double,

    @SerializedName("nome_instrutor")
    val nomeInstrutor: String,

    @SerializedName("url_imagem_capa_curso")
    val urlImagem: String?,

    // NOVO: Campo opcional para o progresso
    @SerializedName("percentual_concluido")
    val percentualConcluido: Int? = null
)