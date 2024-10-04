package com.modulo6.videogamesrf.data.remote.model

import com.google.gson.annotations.SerializedName

data class PokemonDetailDto(

    @SerializedName("name")
    var name : String?,

    @SerializedName("Sprites")
    var sprites: String?


)
  /*  @SerializedName("name")
    var name : String?,

    @SerializedName("Sprites")
    var sprites: Sprites?,
)

data class Sprites(
    @SerializedName("other")
    var other : Other?
)

data class Other(
    @SerializedName("official-artwork")
    var officialArtwork: officialArtwork
)

data class officialArtwork(
    @SerializedName("front_default")
    var frontDefault : String?,
    @SerializedName("front_shiny")
    var frontShiny : String?
)*/