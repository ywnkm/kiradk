
plugins {
    // alias(libs.plugins.kotlin.jvm)
    kotlin("jvm")
}

kotlin {
    explicitApi()
}

dependencies {
    api(libs.ktor.client.core)
    api(libs.ktor.network.tls)
}
