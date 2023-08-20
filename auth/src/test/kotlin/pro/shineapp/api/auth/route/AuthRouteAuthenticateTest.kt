package pro.shineapp.api.auth.route

import com.google.common.truth.Truth.assertThat
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import pro.shineapp.api.auth.plugins.configureSecurity
import pro.shineapp.api.auth.security.token.JwtTokenService
import pro.shineapp.api.auth.security.token.TokenConfig
import java.util.concurrent.TimeUnit

private const val ID = "some-user-id"

class AuthRouteAuthenticateTest {

    private val lazyConfig = mockk<Lazy<TokenConfig>>(relaxed = true)

    private val config = TokenConfig(
        issuer = "jwt-issuer",
        audience = "jwt-audience",
        secret = "secret",
        expiresIn = TimeUnit.DAYS.toMillis(365),
        realm = "jwt-realm"
    )

    private val tokenService = JwtTokenService()

    @Test
    fun `when is authenticated then no error`() = testApplication {

        val testHttpClient = createClient {
            this@testApplication.install(ContentNegotiation) {
                json()
            }
        }

        every { lazyConfig.value } returns config

        environment {
            config = ApplicationConfig("application-test.conf")
        }

        application {
            configureSecurity(lazyConfig)
        }

        routing {
            authenticate()
        }

        val response = testHttpClient.get("authenticate") {
            addJwtHeader()
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `when is not authenticated then 401`() = testApplication {

        externalServices {
            hosts("https://accounts.google.com") {
                install(ContentNegotiation) {
                    json()
                }

                routing {
                    get("o/oauth2/auth") {
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }
            }
        }

        val testHttpClient = createClient {
            this@testApplication.install(ContentNegotiation) {
                json()
            }
        }

        every { lazyConfig.value } returns config

        environment {
            config = ApplicationConfig("application-test.conf")
        }

        application {
            configureSecurity(lazyConfig)
        }

        routing {
            authenticate()
        }

        val response = testHttpClient.get("authenticate")

        assertThat(response.status).isEqualTo(HttpStatusCode.Unauthorized)
    }

    @Test
    fun `when is authenticated then secret path returns correct user id`() = testApplication {

        val testHttpClient = createClient {
            this@testApplication.install(ContentNegotiation) {
                json()
            }
        }

        every { lazyConfig.value } returns config

        environment {
            config = ApplicationConfig("application-test.conf")
        }

        application {
            configureSecurity(lazyConfig)
        }

        routing {
            secret()
        }

        val response = testHttpClient.get("secret") {
            addJwtHeader()
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        assertThat(response.body<String>()).contains(ID)
    }

    private fun HttpRequestBuilder.addJwtHeader() = headers.append("Authorization", "Bearer ${getToken()}")

    private fun getToken(): String {
        return tokenService.generate(config, USER_ID_CLAIM to ID)
    }
}
