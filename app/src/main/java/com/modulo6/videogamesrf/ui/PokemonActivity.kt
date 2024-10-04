package com.modulo6.videogamesrf.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.modulo6.videogamesrf.R
import com.modulo6.videogamesrf.data.remote.PokemonApi
import com.modulo6.videogamesrf.data.remote.RetrofitHelper
import com.modulo6.videogamesrf.data.remote.model.PokemonDetailDto
import com.modulo6.videogamesrf.databinding.ActivityPokemonBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PokemonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPokemonBinding

    companion object{
        const val BASEURLASD = "https://pokeapi.co/"
        const val LOGTAG2 = "LOGSPOKEMON"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokemonBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(BASEURLASD)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val PokemonApi = retrofit.create(PokemonApi::class.java)

        val call: Call<PokemonDetailDto> = PokemonApi.getPokeDete("149")

        call.enqueue(object : Callback<PokemonDetailDto>{
            override fun onResponse(p0: Call<PokemonDetailDto>, response: Response<PokemonDetailDto>) {
                //val url = response.body()?.sprites?.other?.officialArtwork?.frontDefault
                //Log.d(LOGTAG2,"url del pokemon ${url}")
            }

            override fun onFailure(p0: Call<PokemonDetailDto>, p1: Throwable) {

            }

        })

    }


}