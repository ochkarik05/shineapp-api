package pro.shineapp.api.data.source

import pro.shineapp.api.data.model.User

interface UserDataSource {
    suspend fun getByName(username: String): User?
    suspend fun addUser(user: User): Boolean
}