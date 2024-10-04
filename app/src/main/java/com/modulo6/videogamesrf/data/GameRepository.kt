package com.modulo6.videogamesrf.data

import com.modulo6.videogamesrf.data.remote.GamesApi
import com.modulo6.videogamesrf.data.remote.model.GameDetailDto
import com.modulo6.videogamesrf.data.remote.model.GameDto
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.create

class GameRepository (
    private val retrofit: Retrofit
){

    private val gamesApi: GamesApi = retrofit.create(GamesApi::class.java)

    fun getGames(url: String): Call<MutableList<GameDto>> =
        gamesApi.getGames(url)

    fun getGamesDetail(id: String?): Call<GameDetailDto> =
        gamesApi.getGameDetail(id)
}