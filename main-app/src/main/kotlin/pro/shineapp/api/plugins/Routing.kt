package pro.shineapp.api.plugins

import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.p
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import pro.shineapp.api.auth.route.authenticate
import pro.shineapp.api.auth.route.login
import pro.shineapp.api.auth.route.secret
import pro.shineapp.api.auth.route.signUp
import pro.shineapp.api.auth.route.token
import pro.shineapp.api.auth.security.hashing.HashingService
import pro.shineapp.api.auth.security.token.TokenConfig
import pro.shineapp.api.auth.security.token.TokenService
import pro.shineapp.api.data.source.UserDataSource

typealias router = (Routing) -> Unit

@Inject
fun router(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    @Assisted routing: Routing,
) {


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
            hashingService,
            userDataSource,
        )
        login(
            hashingService,
            userDataSource,
            tokenService,
            tokenConfig,
        )
        authenticate()
        secret()
        token()
    }
}
