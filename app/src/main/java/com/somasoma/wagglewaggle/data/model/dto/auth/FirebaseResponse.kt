package com.somasoma.wagglewaggle.data.model.dto.auth

data class FirebaseResponse(
    val isNewMember: String?,
    val accessToken: String?,
    val refreshToken: String?,
    val grantType: String?,
    val accessTokenExpiresIn: Long?
)