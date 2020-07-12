import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.3.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"

    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"

    application
    id("com.github.johnrengelman.shadow") version "6.0.0"
    id("org.jetbrains.dokka") version "0.10.1"
}

group = "dev.bombardy"
version = "1.5-BETA"
java.sourceCompatibility = JavaVersion.VERSION_1_8

application.mainClassName = "dev.bombardy.bardybot.BardyBotApplicationKt"

repositories {
    mavenCentral()
    jcenter()

    maven {
        url = uri("https://repo.prevarinite.com/repository/maven-releases/")
    }

    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    // Discord-related
    implementation("net.dv8tion:JDA:4.2.0_177")
    implementation("com.github.FredBoat:Lavalink-Client:41e1025cd4")
    implementation("com.sedmelluq:lavaplayer:1.3.50")
    implementation("dev.bombardy:octo:1.0.2")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("io.sentry:sentry-spring:1.7.30")
    implementation("io.sentry:sentry-logback:1.7.30")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.time.ExperimentalTime")
        jvmTarget = "1.8"
    }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("BardyBot-${project.version}.jar")
}

tasks.withType<DokkaTask> {
    outputFormat = "html"
}