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
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pro.shineapp.api.auth.security.hashing.HashingService
import pro.shineapp.api.auth.security.hashing.SaltedHash
import pro.shineapp.api.data.model.AuthRequest
import pro.shineapp.api.data.model.User
import pro.shineapp.api.data.source.UserDataSource
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation

class AuthRouteSignupTest {

    private val hashingService = mockk<HashingService>(relaxed = true)
    private val userDataSource = mockk<UserDataSource>(relaxed = true)

    @BeforeEach
    fun setUp() {
        every { hashingService.generateSaltedHash(any(), any()) } returns SaltedHash(
            hash = "hashed-something",
            salt = "salt"
        )

        coEvery { userDataSource.addUser(any()) } returns true
    }


    @Test
    fun `response is OK if user and password meets criteria`() = testApplication {

        val username = "user"
        val password = "password123"

        val response: HttpResponse = httpResponse(username, password)
        val body = response.body<String>()
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        assertThat(body).doesNotContain("Error")
    }

    @Test
    fun `response is Conflict if user is empty`() = testApplication {

        val username = ""
        val password = "password123"

        val response = httpResponse(username, password)

        assertThat(response.status).isEqualTo(HttpStatusCode.Conflict)
    }

    @Test
    fun `response is Conflict if password is short`() = testApplication {
        environment {
            config = ApplicationConfig("application-test.conf")
        }

        val username = ""
        val password = "password"

        val response = httpResponse(username, password)

        assertThat(response.status).isEqualTo(HttpStatusCode.Conflict)
    }


    @Test
    fun `addUser was called`() = testApplication {

        val username = "user"
        val password = "password123"
        val hash = "***hash***"
        val salt = "***salt***"
        every { hashingService.generateSaltedHash(any(), any()) } returns SaltedHash(
            hash = hash,
            salt = salt
        )


        val response = httpResponse(username, password)

        val slot = slot<User>()

        coVerify { userDataSource.addUser(capture(slot)) }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        assertThat(slot.captured.salt).isEqualTo(salt)
        assertThat(slot.captured.password).isEqualTo(hash)
        assertThat(slot.captured.username).isEqualTo(username)
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
            signUp(
                hashingService,
                userDataSource,
            )
        }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        val response = client.post("signup") {
            contentType(ContentType.Application.Json)
            setBody(AuthRequest(username, password))
        }
        return response
    }
}