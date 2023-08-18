package pro.shineapp.api.di

import com.google.common.truth.Truth.assertThat
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.testApplication
import org.junit.Test
import pro.shineapp.api.auth.security.token.TokenConfig
import java.util.concurrent.TimeUnit


class AppComponentKtTest {
    @Test
    fun `the app component instance always the same`() = testApplication {
        environment {
            config = ApplicationConfig("application-test.conf")
        }

        application {
            val comp1 = appComponent
            val comp2 = appComponent
            val comp3 = appComponent
            assertThat(comp1).isSameInstanceAs(comp2)
            assertThat(comp1).isSameInstanceAs(comp3)
        }
    }

    @Test
    fun `tokenConfig is provided correctly`() = testApplication {

        environment {
            config = ApplicationConfig("application-test.conf")
        }

        application {
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