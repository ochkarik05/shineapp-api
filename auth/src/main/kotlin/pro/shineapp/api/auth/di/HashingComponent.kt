package pro.shineapp.api.auth.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import pro.shineapp.api.auth.security.hashing.HashService
import pro.shineapp.api.auth.security.hashing.HashingService
import pro.shineapp.api.auth.security.hashing.SHA256HashingService
import pro.shineapp.api.auth.security.hashing.SaltService
import pro.shineapp.api.auth.security.token.JwtTokenService
import pro.shineapp.api.auth.security.token.TokenService

@Singleton
@Component
abstract class HashingComponent {

    abstract val hashingService: HashingService
    abstract val tokenService: TokenService

    @Provides
    fun saltService() = object : SaltService {}

    @Provides
    fun hasService() = object : HashService {}

    protected val SHA256HashingService.bind: HashingService
        @Provides @Singleton get() = this

    protected val JwtTokenService.bind: TokenService
        @Provides @Singleton get() = this
}