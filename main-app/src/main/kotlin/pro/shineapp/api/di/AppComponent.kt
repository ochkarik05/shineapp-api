package pro.shineapp.api.di

import io.ktor.server.application.ApplicationEnvironment
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import pro.shineapp.api.auth.di.HashingComponent
import pro.shineapp.api.auth.security.token.TokenConfig
import pro.shineapp.api.data.di.DbConfig
import pro.shineapp.api.data.di.UserDataSourceComponent
import pro.shineapp.api.plugins.router
import java.util.concurrent.TimeUnit

private val DB_NAME = "test-db"

@Suppress("unused")
@Component
@Singleton
abstract class AppComponent(
    private val environment: ApplicationEnvironment,
) : HashingComponent, UserDataSourceComponent {
    abstract val tokenConfig: Lazy<TokenConfig>
    abstract val router: router

    @Provides
    protected fun tokenConfig() = TokenConfig(
        audience = environment.config.property("jwt.audience").getString(),
        issuer = environment.config.property("jwt.issuer").getString(),
        realm = environment.config.property("jwt.realm").getString(),
        expiresIn = TimeUnit.DAYS.toMillis(365),
        secret = environment.config.property("jwt.secret").getString(),
    )

    @Provides
    @Singleton
    protected fun dbConfig() = DbConfig(
                        connectionString = "mongodb+srv://ochkarik05:${
                            environment.config.property("mongoDb.password").getString()
                        }@cluster0.dyepgrv.mongodb.net/$DB_NAME?retryWrites=true&w=majority",
                        dbName = DB_NAME
                    )
}

