plugins {
    kotlin("jvm") version "2.0.0"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "gg.airbrush"
version = "0.1.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(project(":spark-common"))
    compileOnly("net.minestom:minestom-snapshots:4305006e6b")
    compileOnly("gg.airbrush:server:0.3.2")
}
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}