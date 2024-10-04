package com.modulo6.videogamesrf.data.remote

import com.modulo6.videogamesrf.data.remote.model.GameDetailDto
import com.modulo6.videogamesrf.data.remote.model.GameDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface GamesApi {


    //games-list

    /*@GET
    fun getGames(
        @Url url: String?
    ): Call<MutableList<GameDto>>

    @GET("cm/games/games_list.php")
    fun getGames2(): Call<MutableList<GameDto>>

    //game_detail

    @GET("cm/games/game_detail.php?")
    fun getGameDetail(
        @Query("id") id: String?
    ): Call<GameDetailDto>*/

    @GET
    fun getGames(
        @Url url: String?
    ): Call<MutableList<GameDto>>

    //game_detail

    /*@GET("game_detail/")
    fun getGameDetail(
        @Query("id") id: String?
    ): Call<GameDetailDto>*/

    @GET("game_detail/{id}")
    fun getGameDetail(
        @Path("id") id: String?
    ): Call<GameDetailDto>


}