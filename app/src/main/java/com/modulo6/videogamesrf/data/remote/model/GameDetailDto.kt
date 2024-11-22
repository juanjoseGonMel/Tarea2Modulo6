package com.modulo6.videogamesrf.data.remote.model

import com.google.gson.annotations.SerializedName

data class GameDetailDto(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("tipo")
    val tipo: String,

    @SerializedName("habilidades")
    val habilidades: String,

    @SerializedName("movimientos")
    val movimientos: String,

    @SerializedName("url_imagen")
    val urlImagen: String,

    @SerializedName("url_video")
    val urlVideo: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("grupo_huevo")
    val grupoHuevo: String,

    @SerializedName("generacion")
    val generacion: Int
)
