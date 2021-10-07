package com.somasoma.wagglewaggle.data.repository_impl

import com.somasoma.wagglewaggle.core.di.hilt.qualifier.ForWorldAPI
import com.somasoma.wagglewaggle.data.service.WorldService
import com.somasoma.wagglewaggle.domain.repository.WorldRepository
import retrofit2.Retrofit
import javax.inject.Inject

class WorldRepositoryImpl @Inject constructor(@ForWorldAPI private val worldRetrofit: Retrofit) :
    WorldRepository {

    private val worldService = worldRetrofit.create(WorldService::class.java)

    override fun getWorldList() = worldService.getWorldList()
}