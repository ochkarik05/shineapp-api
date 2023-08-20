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
    implementation(project(":core"))

    implementation(libs.bson)
    implementation(libs.kotlin.inject.runtime)
    implementation(libs.ktor.client.apache.jvm)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)
    implementation(libs.ktor.server.auth.jvm)
    implementation(libs.ktor.server.auth.jwt.jvm)
    implementation(libs.ktor.client.content.negotiation)

    ksp(libs.kotlin.inject.compiler.ksp)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.ktor.server.tests.jvm)
    testImplementation(libs.truth)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.mockk)
}

tasks.test {
    useJUnitPlatform()
}
