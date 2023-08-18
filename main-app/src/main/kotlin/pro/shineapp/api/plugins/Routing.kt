package pro.shineapp.api.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.p
import pro.shineapp.api.auth.route.authenticate
import pro.shineapp.api.auth.route.login
import pro.shineapp.api.auth.route.secret
import pro.shineapp.api.auth.route.signUp
import pro.shineapp.api.auth.route.token
import pro.shineapp.api.di.appComponent

fun Application.configureRouting() {


    routing {
        get("/") {

            call.respondHtml {
                body {
                    p {
                        a("/google-login") { +"Login with Google" }
                    }
                }
            }
        }
        signUp(
            this@configureRouting.appComponent.hashingComponent.hashingService,
            this@configureRouting.appComponent.userDataSourceComponent.userDataSource,
        )
        login(
            this@configureRouting.appComponent.hashingComponent.hashingService,
            this@configureRouting.appComponent.userDataSourceComponent.userDataSource,
            this@configureRouting.appComponent.hashingComponent.tokenService,
            this@configureRouting.appComponent.tokenConfig.value,
        )
        authenticate()
        secret()
        token()
    }
}
