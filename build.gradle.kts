plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.1.0"
}

group = "io.github.liyulin-s"
version = "1.0-0"

repositories {
    mavenCentral()
}

dependencies {
    runtimeOnly("org.slf4j:slf4j-nop:1.7.36")
    implementation(platform("org.http4k:http4k-bom:6.0.1.0"))

    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-undertow")
    implementation("org.http4k:http4k-client-apache")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}