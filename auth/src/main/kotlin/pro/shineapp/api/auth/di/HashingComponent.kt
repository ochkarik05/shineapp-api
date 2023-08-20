package pro.shineapp.api.auth.di

import me.tatarka.inject.annotations.Provides
import pro.shineapp.api.auth.security.hashing.HashService
import pro.shineapp.api.auth.security.hashing.HashingService
import pro.shineapp.api.auth.security.hashing.SHA256HashingService
import pro.shineapp.api.auth.security.hashing.SaltService
import pro.shineapp.api.auth.security.token.JwtTokenService
import pro.shineapp.api.auth.security.token.TokenService
import pro.shineapp.api.di.Singleton

interface HashingComponent {

    @Provides
    fun saltService() = object : SaltService {}

    @Provides
    fun hasService() = object : HashService {}

    val SHA256HashingService.bind: HashingService
        @Provides @Singleton get() = this

    val JwtTokenService.bind: TokenService
        @Provides @Singleton get() = this
}