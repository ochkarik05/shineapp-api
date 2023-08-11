package com.shineapp.api.data.source

import com.shineapp.api.data.model.User
import me.tatarka.inject.annotations.Inject
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

@Inject
class MongoUserDataSource(
    db: CoroutineDatabase
): UserDataSource {
    private val users by lazy { db.getCollection<User>() }
    override suspend fun getByName(username: String): User? {
        return users.findOne(User::username eq username)
    }

    override suspend fun addUser(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }
}