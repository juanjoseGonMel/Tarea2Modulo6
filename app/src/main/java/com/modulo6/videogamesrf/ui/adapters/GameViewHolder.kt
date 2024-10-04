package com.modulo6.videogamesrf.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.modulo6.videogamesrf.data.remote.model.GameDto
import com.modulo6.videogamesrf.databinding.GameElementBinding
import com.squareup.picasso.Picasso

class GameViewHolder(
    private val binding: GameElementBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(game: GameDto){
        binding.tvTitle.text = game.nombre
        binding.tvReleased.text = game.id
        //Glide

        Glide.with(binding.root.context)
            .load(game.imagenUrl)
            .into(binding.ivThumbnail)

        //Piccaso
        /*Picasso.get()
            .load(game.thumbnail)
            .into(binding.ivThumbnail)*/
    }

}