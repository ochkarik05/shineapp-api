@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm") version "1.9.0"
    alias(libs.plugins.io.ktor)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

group = "pro.shineapp.api.auth"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":data"))

    implementation(libs.ktor.client.apache.jvm)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)
    implementation(libs.ktor.server.auth.jvm)
    implementation(libs.ktor.server.auth.jwt.jvm)

    implementation(libs.kotlin.inject.runtime)
    ksp(libs.kotlin.inject.compiler.ksp)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}
