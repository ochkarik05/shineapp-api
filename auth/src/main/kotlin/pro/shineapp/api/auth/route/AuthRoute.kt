package pro.shineapp.api.auth.route

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pro.shineapp.api.auth.plugins.UserSession
import pro.shineapp.api.auth.security.hashing.HashingService
import pro.shineapp.api.auth.security.hashing.SaltedHash
import pro.shineapp.api.auth.security.token.TokenConfig
import pro.shineapp.api.auth.security.token.TokenService
import pro.shineapp.api.data.model.AuthRequest
import pro.shineapp.api.data.model.AuthResponse
import pro.shineapp.api.data.model.User
import pro.shineapp.api.data.source.UserDataSource

const val USER_ID_CLAIM = "userId"

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource,
) {
    post("signup") {
        runCatching {
            requireNotNull(call.receiveNullable<AuthRequest>()) { "Request is null" }
        }.mapCatching { authRequest ->
            check(validate(authRequest)) { "Validation failed" }
            check(insertUser(authRequest, userDataSource, hashingService)) { "Can't insert user" }
        }.fold(
            { call.respond(HttpStatusCode.OK) },
            { call.respond(getStatusCode(it), "Error: Can't process request: ${it.message}") },
        )
    }
}

fun Route.login(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
) {
    post("login") {
        runCatching {
            call.receive<AuthRequest>()
        }.mapCatching { (username, password) ->
            val user = checkNotNull(userDataSource.getByName(username)) { "User not found!" }
            check(hashingService.verify(password, SaltedHash(user.password, user.salt))) { "Password is incorrect" }
            val token = tokenService.generate(tokenConfig, USER_ID_CLAIM to user.id.toString())
            AuthResponse(token)
        }.fold(
            { call.respond(HttpStatusCode.OK, it) },
            { call.respond(getStatusCode(it), "Can't generate token, ${it.message}") }
        )
    }
}

fun Route.authenticate() {
    authenticate("auth-oauth-google", "jwt") {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}


fun Route.secret() {
    authenticate("auth-oauth-google", "jwt") {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim(USER_ID_CLAIM, String::class)
            call.respond(HttpStatusCode.OK, "Your user id is: $userId")
        }
    }
}


fun Route.token() {
    get("token") {
        val userSession: UserSession? = call.sessions.get()
        if (userSession != null) {
            val httpClient = HttpClient {
                install(ContentNegotiation) {
                    json()
                }
            }
            val userInfo: UserInfo? = try {
                httpClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer ${userSession.accessToken}")
                    }
                }.body()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            call.respondText("Hello, ${userInfo}!, token: ${userSession.accessToken}")
        } else {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}


private suspend fun insertUser(
    request: AuthRequest,
    userDataSource: UserDataSource,
    hashingService: HashingService,
): Boolean {
    val saltedHash = hashingService.generateSaltedHash(request.password)
    return userDataSource.addUser(
        User(
            username = request.username,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )
    )
}

fun validate(request: AuthRequest): Boolean {
    return request.password.isNotBlank() && request.username.isNotBlank() && request.password.length > 10
}

fun getStatusCode(it: Throwable): HttpStatusCode {
    return when (it) {
        is IllegalStateException -> HttpStatusCode.Conflict
        else -> HttpStatusCode.BadRequest
    }
}

@Serializable
data class UserInfo(
    val id: String,
    val name: String,
    @SerialName("given_name") val givenName: String,
    @SerialName("family_name") val familyName: String,
    val picture: String,
    val locale: String
)