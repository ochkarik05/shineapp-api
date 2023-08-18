package pro.shineapp.api

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.testApplication
import pro.shineapp.api.plugins.configureRouting
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        environment {
            config = ApplicationConfig("application-test.conf")
        }
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}
