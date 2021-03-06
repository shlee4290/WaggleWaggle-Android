package com.somasoma.wagglewaggle.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.somasoma.wagglewaggle.presentation.getAvatarResourceId
import com.somasoma.wagglewaggle.data.Avatar
import com.somasoma.wagglewaggle.databinding.AvatarSelectPagerItemBinding

class AvatarSelectPagerAdapter :
    ListAdapter<Avatar, AvatarSelectPagerAdapter.ViewHolder>(AvatarsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(val binding: AvatarSelectPagerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AvatarSelectPagerItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(avatar: Avatar) {
            Glide.with(binding.root)
                .load(getAvatarResourceId(avatar))
                .centerInside()
                .into(binding.imgAvatar)
        }
    }
}

class AvatarsDiffCallback : DiffUtil.ItemCallback<Avatar>() {
    override fun areItemsTheSame(oldItem: Avatar, newItem: Avatar): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Avatar, newItem: Avatar): Boolean {
        return oldItem == newItem
    }
}