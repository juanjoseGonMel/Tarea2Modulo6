package com.modulo6.videogamesrf.ui.fragments

import android.annotation.SuppressLint
import android.graphics.text.LineBreaker
import android.media.MediaPlayer
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
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
    private lateinit var mediaPlayer: MediaPlayer

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

        binding.btnRetry.setOnClickListener {
            loadGameDetails()
        }

        loadGameDetails()

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.accumula_town)
        mediaPlayer.start()

        /*
        gameId?.let { id ->
            val call: Call<GameDetailDto> = repository.getGamesDetail(id)
            call.enqueue(object : Callback<GameDetailDto>{
                override fun onResponse(p0: Call<GameDetailDto>, response: Response<GameDetailDto>) {
                    binding.pbLoading.visibility = View.GONE
                    binding.tvTitle.text = getString(R.string.NombrePokemon) + ": " + response.body()?.nombre
                    binding.tvLongDesc.text = getString(R.string.DescripcionPokemon) + ": " + response.body()?.descripcion
                    binding.tvMov.text = getString(R.string.MovimientosPokemon) + ": " + response.body()?.movimientos
                    binding.tvType.text = getString(R.string.TipoPokemon) + ": " + response.body()?.tipo
                    binding.tvHuevo.text = getString(R.string.HuevoPokemon) + ": " + response.body()?.grupoHuevo
                    binding.tvHabilidad.text = getString(R.string.HabilidadPokemon) + ": " + response.body()?.habilidades
                    binding.tvGeneracion.text = getString(R.string.GeneracionPokemon) + ": " + response.body()?.generacion.toString()
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
        */

    }

    private fun loadGameDetails() {
        binding.pbLoading.visibility = View.VISIBLE
        binding.tvErrorMessage.visibility = View.GONE
        binding.btnRetry.visibility = View.GONE

        gameId?.let { id ->
            val call: Call<GameDetailDto> = repository.getGamesDetail(id)
            call.enqueue(object : Callback<GameDetailDto> {
                @SuppressLint("StringFormatInvalid")
                override fun onResponse(p0: Call<GameDetailDto>, response: Response<GameDetailDto>) {
                    binding.pbLoading.visibility = View.GONE

                    if (response.isSuccessful) {
                        response.body()?.let { gameDetail ->
                            binding.tvTitle.text = gameDetail.nombre
                            binding.tvLongDesc.text = gameDetail.descripcion
                            binding.tvMov.text = gameDetail.movimientos
                            binding.tvType.text = gameDetail.tipo
                            binding.tvHuevo.text = gameDetail.grupoHuevo
                            binding.tvHabilidad.text = gameDetail.habilidades
                            binding.tvGeneracion.text = gameDetail.generacion.toString()

                            binding.vvVideo.addYouTubePlayerListener(object : AbstractYouTubePlayerListener(){
                                override fun onReady(youTubePlayer: YouTubePlayer) {
                                    youTubePlayer.loadVideo(gameDetail.urlVideo, 0f)
                                }
                            })
                            lifecycle.addObserver(binding.vvVideo)

                            Glide.with(binding.root.context)
                                .load(gameDetail.urlImagen)
                                .into(binding.ivImage)

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                                binding.tvLongDesc.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                        } ?: showError(getString(R.string.error_no_data))
                    } else {
                        showError(getString(R.string.error_server))
                    }
                }

                override fun onFailure(p0: Call<GameDetailDto>, p1: Throwable) {
                    binding.pbLoading.visibility = View.GONE
                    showError(getString(R.string.error_network))
                }
            })
        }
    }


    private fun showError(message: String) {
        binding.tvErrorMessage.text = message
        binding.tvErrorMessage.visibility = View.VISIBLE
        binding.btnRetry.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        binding.vvVideo.release()
        _binding = null
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