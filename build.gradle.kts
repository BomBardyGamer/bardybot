import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("org.springframework.boot") version "2.3.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"

    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"

    application
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

group = "dev.bombardy"
version = "1.0.2"
java.sourceCompatibility = JavaVersion.VERSION_1_8

application.mainClassName = "dev.bombardy.bardybot.BardyBotApplicationKt"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.integration:spring-integration-ip:5.3.1.RELEASE")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    // Discord-related
    implementation("net.dv8tion:JDA:4.1.1_165")
    implementation("com.sedmelluq:lavaplayer:1.3.50")
    implementation("me.mattstudios.utils:matt-framework-jda:1.1.10-BETA")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.2.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("BardyBot-${project.version}.jar")
}