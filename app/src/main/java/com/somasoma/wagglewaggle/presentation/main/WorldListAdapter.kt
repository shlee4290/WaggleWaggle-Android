package com.somasoma.wagglewaggle.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.somasoma.wagglewaggle.R
import com.somasoma.wagglewaggle.data.model.dto.world.WorldRoom
import com.somasoma.wagglewaggle.databinding.WorldListItemBinding

class WorldListAdapter :
    ListAdapter<WorldRoom, WorldListAdapter.ViewHolder>(WorldRoomDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(val binding: WorldListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): WorldListAdapter.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = WorldListItemBinding.inflate(layoutInflater, parent, false)
                return WorldListAdapter.ViewHolder(binding)
            }
        }

        fun bind(worldRoom: WorldRoom) {
            binding.txtCurrentUserCount.text = (worldRoom.people ?: 0).toString()
            binding.txtWorldName.text = worldRoom.map
            binding.txtWorldTitle.text = worldRoom.topic
            Glide.with(binding.root)
                .load(R.drawable.map_jongmyo)
                .centerCrop()
                .into(binding.imgMap)
            binding.root.clipToOutline = true
        }
    }
}

class WorldRoomDiffCallback : DiffUtil.ItemCallback<WorldRoom>() {
    override fun areItemsTheSame(oldItem: WorldRoom, newItem: WorldRoom): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: WorldRoom, newItem: WorldRoom): Boolean {
        return oldItem == newItem
    }
}