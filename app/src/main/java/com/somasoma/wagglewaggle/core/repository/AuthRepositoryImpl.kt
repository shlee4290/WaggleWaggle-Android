package com.somasoma.wagglewaggle.core.repository

import android.app.Application
import com.firebase.ui.auth.AuthUI
import com.somasoma.wagglewaggle.core.di.hilt.qualifier.ForAuthAPI
import com.somasoma.wagglewaggle.core.model.dto.auth.FirebaseRequest
import com.somasoma.wagglewaggle.core.model.dto.auth.FirebaseResponse
import com.somasoma.wagglewaggle.core.model.dto.auth.RefreshRequest
import com.somasoma.wagglewaggle.core.model.dto.auth.RefreshResponse
import com.somasoma.wagglewaggle.core.service.AuthService
import com.somasoma.wagglewaggle.core.usecase.UserConnectedStateUseCase
import io.reactivex.Single
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val application: Application,
    @ForAuthAPI private val authRetrofits: Pair<Retrofit, Retrofit>,
    private val userConnectedStateUseCase: UserConnectedStateUseCase
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
        userConnectedStateUseCase.postCurrentUserOffline()

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
        userConnectedStateUseCase.postCurrentUserOffline()

        AuthUI.getInstance()
            .delete(application)
            .addOnCompleteListener { // 유저 계정 삭제 성공 시
                onSuccessCallback()
                signOut({}, {})
            }.addOnCanceledListener {
                onFailureCallback()
            }
    }

    override fun postFirebase(firebaseRequest: FirebaseRequest): Single<Response<FirebaseResponse?>> =
        publicAuthService.postFirebase(firebaseRequest)

    override fun postRefresh(refreshRequest: RefreshRequest): Single<Response<RefreshResponse?>> =
        authService.postRefresh(refreshRequest)
}