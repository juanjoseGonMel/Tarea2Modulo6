package com.modulo6.videogamesrf.data.remote.model

import com.google.gson.annotations.SerializedName

data class GameDto(

    @SerializedName("id")
    var id : String? = null,

    @SerializedName("nombre")
    var nombre : String? = null,

    @SerializedName("imagen_url")
    var imagenUrl : String? = null,

)
