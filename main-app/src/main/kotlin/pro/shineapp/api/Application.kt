package pro.shineapp.api

import io.ktor.server.application.*
import pro.shineapp.api.auth.plugins.configureSecurity
import pro.shineapp.api.di.appComponent
import pro.shineapp.api.plugins.configureMonitoring
import pro.shineapp.api.plugins.configureRouting
import pro.shineapp.api.plugins.configureSerialization

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}


@Suppress("unused")
fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureSecurity(appComponent.tokenConfig)
    configureRouting()
}
