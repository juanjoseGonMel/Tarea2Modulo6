package com.modulo6.videogamesrf.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.modulo6.videogamesrf.R
import com.modulo6.videogamesrf.data.GameRepository
import com.modulo6.videogamesrf.data.remote.RetrofitHelper
import com.modulo6.videogamesrf.databinding.ActivityMainBinding
import com.modulo6.videogamesrf.ui.fragments.GamesListFragment
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /*private lateinit var repository: GameRepository
    private lateinit var retrofit: Retrofit*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, GamesListFragment())
                .commit()
        }

        /*retrofit = RetrofitHelper().getRetrofit()
        repository = GameRepository(retrofit)*/


    }
}