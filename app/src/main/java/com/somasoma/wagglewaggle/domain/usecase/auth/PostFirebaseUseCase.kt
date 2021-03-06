package com.somasoma.wagglewaggle.domain.usecase.auth

import com.somasoma.wagglewaggle.data.model.dto.auth.FirebaseRequest
import com.somasoma.wagglewaggle.domain.repository.AuthRepository
import javax.inject.Inject

class PostFirebaseUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend fun postFirebase(firebaseRequest: FirebaseRequest) =
        authRepository.postFirebase(firebaseRequest)
}