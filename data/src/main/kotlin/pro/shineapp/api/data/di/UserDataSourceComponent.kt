package pro.shineapp.api.data.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import pro.shineapp.api.data.source.MongoUserDataSource
import pro.shineapp.api.data.source.UserDataSource

data class DbConfig(
    val connectionString: String,
    val dbName: String,
)

@Singleton
@Component
abstract class UserDataSourceComponent(
    private val dbConfig: DbConfig,
) {

    abstract val userDataSource: UserDataSource

    @Provides
    @Singleton
    protected fun providesDb() = KMongo.createClient(
        connectionString = dbConfig.connectionString
    ).coroutine.getDatabase(dbConfig.dbName)

    val MongoUserDataSource.bind: UserDataSource
        @Provides @Singleton get() = this
}