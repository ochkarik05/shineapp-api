@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm") version "1.9.0"
    alias(libs.plugins.io.ktor)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

group = "com.shineapp.api"
version = "0.0.3"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {

    implementation(project(":auth"))
    implementation(project(":data"))
    implementation(project(":core"))

    implementation(libs.kmongo.coroutine)
    implementation(libs.kotlin.inject.runtime)
    implementation(libs.ktor.client.apache.jvm)
    implementation(libs.ktor.client.core.jvm)
    implementation(libs.ktor.server.html)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)
    implementation(libs.ktor.server.auth.jvm)
    implementation(libs.ktor.server.auth.jwt.jvm)
    implementation(libs.ktor.server.call.logging.jvm)
    implementation(libs.ktor.server.content.negotiation.jvm)
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.netty.jvm)
    implementation(libs.logback.classic)
    implementation(libs.symbol.processing.api)

    ksp(libs.kotlin.inject.compiler.ksp)

    testImplementation(libs.truth)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.ktor.server.tests.jvm)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.mockk)

    testImplementation("uk.org.webcompere:system-stubs-core:2.0.2")
    testImplementation("uk.org.webcompere:system-stubs-junit4:2.0.2")

}

tasks.register("runAllUnitTests", GradleBuild::class) {
    group = "verification"
    description = "Run all unit tests for debug configuration for all modules"
    tasks = listOf(
        ":main-app:test",
        ":auth:test",
        ":data:test",
    )
}
