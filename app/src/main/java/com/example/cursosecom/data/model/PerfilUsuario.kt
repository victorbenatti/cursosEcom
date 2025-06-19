package com.example.cursosecom.data.model

import com.google.gson.annotations.SerializedName

data class PerfilUsuario(
    @SerializedName("id_usuario")
    val id: Int,
    @SerializedName("nome_completo")
    val nomeCompleto: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("url_foto_perfil")
    val urlFotoPerfil: String? // Pode ser nulo
)