package com.modulo6.videogamesrf.application

import android.app.Application
import com.modulo6.videogamesrf.data.GameRepository
import com.modulo6.videogamesrf.data.remote.RetrofitHelper

class VideoGamesRFApp: Application() {

    private val retrofit by lazy {
        RetrofitHelper().getRetrofit()
    }

    val repository by lazy {
        GameRepository(retrofit)
    }

}