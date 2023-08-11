package com.shineapp.api.security.hashing

data class SaltedHash (
    val hash: String,
    val salt: String,
)