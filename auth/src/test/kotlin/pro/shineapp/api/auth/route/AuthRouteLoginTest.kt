package pro.shineapp.api.auth.route

import com.google.common.truth.Truth.assertThat
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pro.shineapp.api.auth.security.hashing.HashingService
import pro.shineapp.api.auth.security.hashing.SaltedHash
import pro.shineapp.api.auth.security.token.TokenConfig
import pro.shineapp.api.auth.security.token.TokenService
import pro.shineapp.api.data.model.AuthRequest
import pro.shineapp.api.data.model.AuthResponse
import pro.shineapp.api.data.model.User
import pro.shineapp.api.data.source.UserDataSource
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation

private const val MOCK_TOKEN = "GENERATED-JWT-TOKEN-MOCK"

class AuthRouteLoginTest {

    private val hashingService = mockk<HashingService>(relaxed = true)
    private val userDataSource = mockk<UserDataSource>(relaxed = true)
    private val tokenService = mockk<TokenService>(relaxed = true)
    private val tokenConfig = TokenConfig(
        issuer = "Issuer",
        realm = "Realm",
        expiresIn = 1000,
        secret = "Secret",
        audience = "Audience",
    )

    @BeforeEach
    fun setUp() {

        val mockHash = "hashed-something"
        val mockSalt = "salt"
        val mockId = ObjectId()
        every { hashingService.generateSaltedHash(any(), any()) } returns SaltedHash(
            hash = mockHash,
            salt = mockSalt,
        )

        coEvery { userDataSource.getByName(any()) } answers {
            val name = arg<String>(0)
            User(
                username = name,
                password = mockHash,
                salt = mockSalt,
                mockId
            )
        }

        every { hashingService.verify(any(), any()) } returns true

        every { tokenService.generate(any(), any()) } returns MOCK_TOKEN
    }

    @Test
    fun `Error is returned when credentials are not correct`() = testApplication {
        val username = "user"
        val password = "password123"
        every { hashingService.verify(any(), any()) } returns false
        val response = httpResponse(username, password)

        assertThat(response.status).isEqualTo(HttpStatusCode.Conflict)
        assertThat(response.body<String>()).contains("Password is incorrect")
    }

    @Test
    fun `Error is returned when user not found`() = testApplication {
        val username = "user"
        val password = "password123"
        coEvery { userDataSource.getByName(any()) } returns null

        val response = httpResponse(username, password)

        assertThat(response.status).isEqualTo(HttpStatusCode.Conflict)
        assertThat(response.body<String>()).contains("User not found")
    }

    @Test
    fun `token is returned when credentials are correct`() = testApplication {
        val username = "user"
        val password = "password123"
        val response = httpResponse(username, password)
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        assertThat(response.body<AuthResponse>().token).isEqualTo(MOCK_TOKEN)
    }

    private suspend fun ApplicationTestBuilder.httpResponse(
        username: String,
        password: String
    ): HttpResponse {

        environment {
            config = ApplicationConfig("application-test.conf")
        }
        install(ServerContentNegotiation) {
            json()
        }

        routing {
            login(
                hashingService,
                userDataSource,
                tokenService,
                tokenConfig
            )
        }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        val response = client.post("login") {
            contentType(ContentType.Application.Json)
            setBody(AuthRequest(username, password))
        }
        return response
    }
}