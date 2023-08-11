package com.shineapp.api

import com.shineapp.api.di.AppComponent
import com.shineapp.api.di.HashingComponent
import com.shineapp.api.di.create
import com.shineapp.api.plugins.configureMonitoring
import com.shineapp.api.plugins.configureRouting
import com.shineapp.api.plugins.configureSecurity
import com.shineapp.api.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

val Application.appComponent by lazy { AppComponent::class.create(HashingComponent::class.create()) }

@Suppress("unused")
fun Application.module() {
    appComponent.hashingComponent.hashingService.also {
        println("### hashCode: ${it.hashCode()}")
    }.generateSaltedHash("@@@").also {
        println("### $it")
    }
    appComponent.hashingComponent.hashingService.also {
        println("### hashCode: ${it.hashCode()}")
    }.generateSaltedHash("@@@").also {
        println("### $it")
    }
    configureMonitoring()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
