package com.modulo6.videogamesrf.data.remote.model

import com.google.gson.annotations.SerializedName

data class GameDetailDto(

    @SerializedName("title")
    var title : String? = null,

    @SerializedName("image")
    var image: String? = null,

    @SerializedName("long_desc")
    var longDesc : String? = null

)