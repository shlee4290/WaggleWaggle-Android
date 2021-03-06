package com.somasoma.wagglewaggle.data.repository_impl

import android.app.Application
import com.firebase.ui.auth.AuthUI
import com.somasoma.wagglewaggle.presentation.di.hilt.qualifier.ForAuthAPI
import com.somasoma.wagglewaggle.data.model.dto.auth.FirebaseRequest
import com.somasoma.wagglewaggle.data.model.dto.auth.RefreshRequest
import com.somasoma.wagglewaggle.data.model.dto.auth.SignUpRequest
import com.somasoma.wagglewaggle.data.service.AuthService
import com.somasoma.wagglewaggle.domain.repository.AuthRepository
import retrofit2.Retrofit
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val application: Application,
    @ForAuthAPI private val authRetrofits: Pair<Retrofit, Retrofit>
) : AuthRepository {
    companion object {
        private const val FIREBASE_URL =
            "https://speak-world-default-rtdb.asia-southeast1.firebasedatabase.app"
    }

    private val publicAuthService = authRetrofits.second.create(AuthService::class.java)
    private val authService = authRetrofits.first.create(AuthService::class.java)

    override fun signOut(
        onSuccessCallback: () -> Unit,
        onFailureCallback: () -> Unit
    ) {
        AuthUI.getInstance()
            .signOut(application)
            .addOnCompleteListener {
                onSuccessCallback()
            }.addOnCanceledListener {
                onFailureCallback()
            }
    }

    override fun deleteAccount(
        onSuccessCallback: () -> Unit,
        onFailureCallback: () -> Unit
    ) {
        AuthUI.getInstance()
            .delete(application)
            .addOnCompleteListener { // 유저 계정 삭제 성공 시
                onSuccessCallback()
                signOut({}, {})
            }.addOnCanceledListener {
                onFailureCallback()
            }
    }

    override suspend fun postFirebase(firebaseRequest: FirebaseRequest) =
        publicAuthService.postFirebase(firebaseRequest)

    override suspend fun postRefresh(refreshRequest: RefreshRequest) =
        authService.postRefresh(refreshRequest)

    override suspend fun postSignUp(signUpRequest: SignUpRequest) =
        publicAuthService.postSignUp(signUpRequest)
}