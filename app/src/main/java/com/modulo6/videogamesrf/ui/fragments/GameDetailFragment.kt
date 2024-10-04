package com.modulo6.videogamesrf.ui.fragments

import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.modulo6.videogamesrf.R
import com.modulo6.videogamesrf.application.VideoGamesRFApp
import com.modulo6.videogamesrf.data.GameRepository
import com.modulo6.videogamesrf.data.remote.model.GameDetailDto
import com.modulo6.videogamesrf.databinding.FragmentGameDetailBinding
import com.modulo6.videogamesrf.util.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log


private const val GAME_ID = "game_id"

class GameDetailFragment : Fragment() {

    private var gameId: String? = null

    private var _binding: FragmentGameDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: GameRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gameId = it.getString(GAME_ID)
            Log.d(Constants.LOGTAG,"id recibido $gameId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = (requireActivity().application as VideoGamesRFApp).repository
        gameId?.let { id ->
            val call: Call<GameDetailDto> = repository.getGamesDetail(id)
            call.enqueue(object : Callback<GameDetailDto>{
                override fun onResponse(p0: Call<GameDetailDto>, response: Response<GameDetailDto>) {
                    binding.pbLoading.visibility = View.GONE
                    binding.tvTitle.text = response.body()?.nombre
                    binding.tvLongDesc.text = response.body()?.descripcion
                    Glide.with(binding.root.context)
                        .load(response.body()?.urlImagen)
                        .into(binding.ivImage)

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        binding.tvLongDesc.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD

                }

                override fun onFailure(p0: Call<GameDetailDto>, p1: Throwable) {

                }

            })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(gameId: String) =
            GameDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(GAME_ID, gameId)
                }
            }
    }
}