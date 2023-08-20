package pro.shineapp.api.data.di

import me.tatarka.inject.annotations.Provides
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import pro.shineapp.api.data.source.MongoUserDataSource
import pro.shineapp.api.data.source.UserDataSource
import pro.shineapp.api.di.Singleton

data class DbConfig(
    val connectionString: String,
    val dbName: String,
)

interface UserDataSourceComponent {

    @Provides
    @Singleton
    fun providesDb(dbConfig: DbConfig) = KMongo.createClient(
        connectionString = dbConfig.connectionString
    ).coroutine.getDatabase(dbConfig.dbName)

    val MongoUserDataSource.bind: UserDataSource
        @Provides get() = this
}