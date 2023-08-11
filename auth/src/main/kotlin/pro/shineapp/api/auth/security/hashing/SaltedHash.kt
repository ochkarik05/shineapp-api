package pro.shineapp.api.auth.security.hashing

data class SaltedHash (
    val hash: String,
    val salt: String,
)