package com.shineapp.api.di

import com.shineapp.api.security.hashing.HashService
import com.shineapp.api.security.hashing.HashingService
import com.shineapp.api.security.hashing.SHA256HashingService
import com.shineapp.api.security.hashing.SaltService
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Singleton
@Component
interface HashingComponent {

    val hashingService: HashingService

    @Provides
    fun saltService() = object : SaltService{}

    @Provides
    fun hasService() = object : HashService {}

    val SHA256HashingService.bind: HashingService
        @Provides @Singleton get() = this

}