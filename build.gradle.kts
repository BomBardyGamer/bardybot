import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.3.2.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"

    kotlin("jvm") version "1.4.32"
    kotlin("plugin.spring") version "1.4.32"
    kotlin("plugin.serialization") version "1.4.32"
    kotlin("plugin.jpa") version "1.4.32"

    application
    id("org.jetbrains.dokka") version "0.10.1"
}

group = "me.bardy"
version = "1.8-BETA"
java.sourceCompatibility = JavaVersion.VERSION_1_8

application.mainClassName = "me.bardy.bot.BardyBotApplicationKt"

repositories {
    mavenCentral()
    jcenter()

    maven("https://m2.dv8tion.net/releases")
    maven("https://jitpack.io")
    maven("https://libraries.minecraft.net")
}

object Versions {

    const val JDA = "4.2.0_252"
    const val LAVALINK = "6844df80d7"
    const val LAVAPLAYER = "1.3.73"
    const val EXPOSED = "0.29.1"
}

dependencies {

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Kotlin
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }

    // Discord-related
    implementation("net.dv8tion:JDA:${Versions.JDA}")
    implementation("com.github.Frederikam:Lavalink-Client:${Versions.LAVALINK}")
    implementation("com.sedmelluq:lavaplayer:${Versions.LAVAPLAYER}")

    // Database
    implementation("com.zaxxer:HikariCP:4.0.3")
    runtimeOnly("org.postgresql:postgresql:42.2.19")

    // Metrics
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Other
    implementation("com.mojang:brigadier:1.0.17")
    implementation("io.sentry:sentry-log4j2:1.7.30")
    implementation("com.github.ben-manes.caffeine:caffeine")
}

// we don't want this, as it gives us Logback, and we want to use Log4J 2
configurations.all {
    exclude("org.springframework.boot", "spring-boot-starter-logging")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.time.ExperimentalTime")
        }
    }
    withType<ProcessResources> {
        val tokens = mapOf(
            "version" to project.version,
            "jdaVersion" to Versions.JDA,
            "lavalinkVersion" to Versions.LAVALINK,
            "lavaplayerVersion" to Versions.LAVAPLAYER
        )
        filter<ReplaceTokens>("tokens" to tokens)
    }
}