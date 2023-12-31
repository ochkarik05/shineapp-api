package pro.shineapp.api

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import pro.shineapp.api.auth.plugins.configureSecurity
import pro.shineapp.api.di.AppComponent
import pro.shineapp.api.di.create
import pro.shineapp.api.plugins.configureMonitoring
import pro.shineapp.api.plugins.configureSerialization

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {

    val appComponent = AppComponent::class.create(environment = environment)

    configureMonitoring()
    configureSerialization()
    configureSecurity(appComponent.tokenConfig)
    routing {
        appComponent.router(this)
    }
}
