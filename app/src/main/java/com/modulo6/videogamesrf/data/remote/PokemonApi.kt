package com.modulo6.videogamesrf.data.remote

import com.modulo6.videogamesrf.data.remote.model.GameDetailDto
import com.modulo6.videogamesrf.data.remote.model.PokemonDetailDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonApi {

    //https://pokeapi.co/api/v2/pokemon/149/
    @GET("api/v2/pokemon/{id}")
    fun getPokeDete(
        @Path("id") id: String?
    ): Call<PokemonDetailDto>
}