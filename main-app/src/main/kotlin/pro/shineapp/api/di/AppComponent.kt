package pro.shineapp.api.di

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationEnvironment
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import pro.shineapp.api.auth.di.HashingComponent
import pro.shineapp.api.auth.di.create
import pro.shineapp.api.auth.security.token.TokenConfig
import pro.shineapp.api.data.di.DbConfig
import pro.shineapp.api.data.di.UserDataSourceComponent
import pro.shineapp.api.data.di.create
import java.util.concurrent.TimeUnit

private val DB_NAME = "test-db"

@Suppress("unused")
@Component
abstract class AppComponent(
    val hashingComponent: HashingComponent,
    val userDataSourceComponent: UserDataSourceComponent,
    private val environment: ApplicationEnvironment,
) {
    abstract val tokenConfig: Lazy<TokenConfig>

    @Provides
    protected fun tokenConfig() = TokenConfig(
        audience = environment.config.property("jwt.audience").getString(),
        issuer = environment.config.property("jwt.issuer").getString(),
        realm = environment.config.property("jwt.realm").getString(),
        expiresIn = TimeUnit.DAYS.toMillis(365),
        secret = environment.config.property("jwt.secret").getString(),
    )
}

val Application.appComponent: AppComponent
    get() = ComponentHolder.getInstance(this.environment).appComponent

private class ComponentHolder private constructor(environment: ApplicationEnvironment) {
    val appComponent: AppComponent

    init {
        appComponent =
            AppComponent::class.create(
                HashingComponent::class.create(),
                UserDataSourceComponent::class.create(
                    DbConfig(
                        connectionString = "mongodb+srv://ochkarik05:${
                            environment.config.property("mongoDb.password").getString()
                        }@cluster0.dyepgrv.mongodb.net/$DB_NAME?retryWrites=true&w=majority",
                        dbName = DB_NAME
                    )
                ),
                environment
            )
    }

    companion object : SingletonHolder<ComponentHolder, ApplicationEnvironment>(::ComponentHolder)
}

open class SingletonHolder<out T : Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}
