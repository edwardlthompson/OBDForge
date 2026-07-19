plugins {
    id("org.jetbrains.kotlin.jvm") version "2.4.10"
}
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.4.10")
}

