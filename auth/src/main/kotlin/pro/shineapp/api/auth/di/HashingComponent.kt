package pro.shineapp.api.auth.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import pro.shineapp.api.auth.security.hashing.HashService
import pro.shineapp.api.auth.security.hashing.HashingService
import pro.shineapp.api.auth.security.hashing.SHA256HashingService
import pro.shineapp.api.auth.security.hashing.SaltService

@Singleton
@Component
interface HashingComponent {

    val hashingService: HashingService

    @Provides
    fun saltService() = object : SaltService {}

    @Provides
    fun hasService() = object : HashService {}

    val SHA256HashingService.bind: HashingService
        @Provides @Singleton get() = this

}