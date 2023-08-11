package com.shineapp.api.di

import com.shineapp.api.data.source.MongoUserDataSource
import com.shineapp.api.data.source.UserDataSource
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

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