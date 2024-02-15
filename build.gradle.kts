


plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
}

group = "kiradk"
version =libs.versions.kiradk

allprojects {
    repositories {
        mavenCentral()
    }
}
