@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm") version "1.9.0"
    alias(libs.plugins.io.ktor)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

group = "com.shineapp.api.core"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {

    implementation(libs.kotlin.inject.runtime)

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}