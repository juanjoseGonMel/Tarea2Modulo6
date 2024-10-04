package com.modulo6.videogamesrf.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.modulo6.videogamesrf.data.remote.model.GameDto
import com.modulo6.videogamesrf.databinding.GameElementBinding

class GameAdapter(
    private val games: MutableList<GameDto>,
    private val onGameClicked: (GameDto) -> Unit
): RecyclerView.Adapter<GameViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = GameElementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
            )
        return GameViewHolder(binding)
    }

    override fun getItemCount(): Int = games.size

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(games[position])
        holder.itemView.setOnClickListener{
            onGameClicked(games[position])
        }
    }

}