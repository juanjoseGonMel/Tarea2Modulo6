package com.modulo6.videogamesrf.ui.fragments

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.modulo6.videogamesrf.R
import com.modulo6.videogamesrf.application.VideoGamesRFApp
import com.modulo6.videogamesrf.data.GameRepository
import com.modulo6.videogamesrf.data.remote.model.GameDto
import com.modulo6.videogamesrf.databinding.FragmentGamesListBinding
import com.modulo6.videogamesrf.ui.adapters.GameAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GamesListFragment : Fragment() {

    private var _binding: FragmentGamesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: GameRepository

    //private var mediaPlayer: MediaPlayer = MediaPlayer.create(this, R.raw.accumula_town)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGamesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = (requireActivity().application as VideoGamesRFApp).repository

        //val call : Call<MutableList<GameDto>> = repository.getGames("cm/games/games_list.php")
        /*
        val call : Call<MutableList<GameDto>> = repository.getGames("Pokemons/PokeList")
        call.enqueue(object: Callback<MutableList<GameDto>>{
            override fun onResponse(
                p0: Call<MutableList<GameDto>>,
                response: Response<MutableList<GameDto>>
            ) {
                binding.pbLoading.visibility = View.GONE

                if (response.isSuccessful) {
                    response.body()?.let { games ->
                        if (games.isNotEmpty()) {
                            binding.rvGames.apply {
                                layoutManager = LinearLayoutManager(requireContext())
                                adapter = GameAdapter(games) { game ->
                                    game.id?.let { id ->
                                        requireActivity().supportFragmentManager
                                            .beginTransaction()
                                            .replace(R.id.fragment_container, GameDetailFragment.newInstance(id))
                                            .addToBackStack(null)
                                            .commit()
                                    }
                                }
                            }
                        }else {
                            binding.tvErrorMessage.text = getString(R.string.error_no_data)
                            binding.tvErrorMessage.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.tvErrorMessage.text = getString(R.string.error_server)
                    binding.tvErrorMessage.visibility = View.VISIBLE
                }



            }

            override fun onFailure(p0: Call<MutableList<GameDto>>, p1: Throwable) {
                binding.pbLoading.visibility = View.GONE
                binding.tvErrorMessage.text = getString(R.string.error_network)
                binding.tvErrorMessage.visibility = View.VISIBLE
            }

        })
        */

        binding.btnRetry.setOnClickListener {
            loadGames()
        }

        loadGames()


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun loadGames() {
        binding.pbLoading.visibility = View.VISIBLE
        binding.tvErrorMessage.visibility = View.GONE
        binding.btnRetry.visibility = View.GONE

        val call : Call<MutableList<GameDto>> = repository.getGames("Pokemons/PokeList")


        call.enqueue(object: Callback<MutableList<GameDto>> {
            override fun onResponse(
                p0: Call<MutableList<GameDto>>,
                response: Response<MutableList<GameDto>>
            ) {
                binding.pbLoading.visibility = View.GONE

                if (response.isSuccessful) {
                    response.body()?.let { games ->
                        if (games.isNotEmpty()) {
                            binding.rvGames.apply {
                                layoutManager = LinearLayoutManager(requireContext())
                                adapter = GameAdapter(games) { game ->
                                    game.id?.let { id ->
                                        requireActivity().supportFragmentManager
                                            .beginTransaction()
                                            .replace(R.id.fragment_container, GameDetailFragment.newInstance(id))
                                            .addToBackStack(null)
                                            .commit()
                                    }
                                }
                            }
                        } else {
                            showError(getString(R.string.error_no_data))
                        }
                    }
                } else {
                    showError(getString(R.string.error_server))
                }
            }

            override fun onFailure(p0: Call<MutableList<GameDto>>, p1: Throwable) {
                binding.pbLoading.visibility = View.GONE
                showError(getString(R.string.error_network))
            }
        })


    }

    private fun showError(message: String) {
        binding.tvErrorMessage.text = message
        binding.tvErrorMessage.visibility = View.VISIBLE
        binding.btnRetry.visibility = View.VISIBLE
    }

    /*private fun reloadFragment() {
        parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
    }*/

}