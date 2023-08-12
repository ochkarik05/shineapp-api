package pro.shineapp.api.data.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import pro.shineapp.api.data.source.MongoUserDataSource
import pro.shineapp.api.data.source.UserDataSource

private val DB_NAME = "test-db"

@Singleton
@Component
abstract class UserDataSourceComponent() {

    abstract val userDataSource: UserDataSource

    private val mongoPw by lazy { System.getenv("MONGO_PW") }

    @Provides
    protected fun providesDb() = KMongo.createClient(
        connectionString = "mongodb+srv://ochkarik05:$mongoPw@cluster0.dyepgrv.mongodb.net/$DB_NAME?retryWrites=true&w=majority"
    ).coroutine.getDatabase(DB_NAME)

    val MongoUserDataSource.bind: UserDataSource
        @Provides @Singleton get() = this
}