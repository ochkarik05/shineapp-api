package pro.shineapp.api.di

import com.google.common.truth.Truth.assertThat
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.testApplication
import org.junit.Test
import pro.shineapp.api.auth.security.token.TokenConfig
import java.util.concurrent.TimeUnit


class AppComponentKtTest {

    @Test
    fun `tokenConfig is provided correctly`() = testApplication {

        environment {
            config = ApplicationConfig("application-test.conf")
        }

        application {
            val appComponent = AppComponent::class.create(environment)
            val tokenConfig by appComponent.tokenConfig
            assertThat(tokenConfig).isEqualTo(
                TokenConfig(
                    audience = environment.config.property("jwt.audience").getString(),
                    issuer = environment.config.property("jwt.issuer").getString(),
                    realm = environment.config.property("jwt.realm").getString(),
                    expiresIn = TimeUnit.DAYS.toMillis(365),
                    secret = environment.config.property("jwt.secret").getString(),
                )
            )
        }
    }
}