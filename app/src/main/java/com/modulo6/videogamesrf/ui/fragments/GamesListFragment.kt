package com.modulo6.videogamesrf.ui.fragments

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

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
        val call : Call<MutableList<GameDto>> = repository.getGames("games_list")


        call.enqueue(object: Callback<MutableList<GameDto>>{
            override fun onResponse(
                p0: Call<MutableList<GameDto>>,
                response: Response<MutableList<GameDto>>
            ) {
                binding.pbLoading.visibility = View.GONE

                response.body()?.let { games ->
                    binding.rvGames.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = GameAdapter(games){game ->
                            game.id?.let { id ->
                                requireActivity().supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, GameDetailFragment.newInstance(id))
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }
                    }
                }
            }

            override fun onFailure(p0: Call<MutableList<GameDto>>, p1: Throwable) {

            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}