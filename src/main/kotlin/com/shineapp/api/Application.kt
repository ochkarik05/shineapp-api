package com.shineapp.api

import com.shineapp.api.data.model.User
import com.shineapp.api.di.AppComponent
import com.shineapp.api.di.HashingComponent
import com.shineapp.api.di.create
import com.shineapp.api.plugins.*
import io.ktor.server.application.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

val Application.appComponent by lazy { AppComponent::class.create(HashingComponent::class.create()) }

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
