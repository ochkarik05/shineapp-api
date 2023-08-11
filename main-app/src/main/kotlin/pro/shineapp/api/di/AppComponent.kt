package pro.shineapp.api.di

import io.ktor.server.application.*
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import pro.shineapp.api.auth.di.HashingComponent
import pro.shineapp.api.auth.di.create
import pro.shineapp.api.data.source.MongoUserDataSource
import pro.shineapp.api.data.source.UserDataSource

val dbName = "test-db"

@Component
abstract class AppComponent(
    val hashingComponent: HashingComponent
) {

    private val mongoPw by lazy { System.getenv("MONGO_PW") }

    abstract val userDataSource: UserDataSource

    @Provides
    protected fun providesDb() = KMongo.createClient(
        connectionString = "mongodb+srv://ochkarik05:$mongoPw@cluster0.dyepgrv.mongodb.net/$dbName?retryWrites=true&w=majority"
    ).coroutine.getDatabase(dbName)

    protected val MongoUserDataSource.bind: UserDataSource
        @Provides get() = this
}

val Application.appComponent by lazy { AppComponent::class.create(HashingComponent::class.create()) }
