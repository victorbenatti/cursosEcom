package com.example.cursosecom.data.model

import com.google.gson.annotations.SerializedName

data class Aula(
    @SerializedName("id_aula")
    val id: Int,
    @SerializedName("titulo_aula")
    val titulo: String,
    @SerializedName("ordem_aula")
    val ordem: Int
)