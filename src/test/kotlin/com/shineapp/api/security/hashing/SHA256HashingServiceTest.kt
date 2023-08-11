package com.shineapp.api.security.hashing

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class SHA256HashingServiceTest{

    val sut = SHA256HashingService(
        saltService = object : SaltService{},
        hashService = object : HashService{},
    )
    @Test
    fun `test setup is implememted correctly`(){
       assertThat(true).isTrue()
    }

    @Test
    fun `test that verify returns true`(){
        val saltedHash = sut.generateSaltedHash("password")
        val verified = sut.verify("password", saltedHash)
        assertThat(verified).isTrue()
    }

    @Test
    fun `salt has correct length`(){
        val len8 = 8
        val result8 = sut.generateSaltedHash("passoword", saltLength = len8)
        assertThat(result8.salt).hasLength(len8 * 2)

        val len16 = 16
        val result16 = sut.generateSaltedHash("passoword", saltLength = len16)
        assertThat(result16.salt).hasLength(len16 * 2)
    }

    @Test
    fun `default salt length is correct`(){
        val defaultLenght = 32
        val result = sut.generateSaltedHash("passoword")
        assertThat(result.salt).hasLength(defaultLenght * 2)
    }
}