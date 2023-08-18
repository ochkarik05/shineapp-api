@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm") version "1.9.0"
    alias(libs.plugins.io.ktor)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

group = "pro.shineapp.api.data"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {

    implementation(libs.kmongo.coroutine)
    implementation(libs.kotlin.inject.runtime)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)
    ksp(libs.kotlin.inject.compiler.ksp)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}