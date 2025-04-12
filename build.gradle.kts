import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependencyManagement)

    application
    alias(libs.plugins.dokka)
}

group = "me.bardy"
version = "2.0"

java.sourceCompatibility = JavaVersion.VERSION_21
application.mainClass.set("me.bardy.bot.BardyBotKt")

dependencies {
    // Spring
    implementation(libs.spring.web)
    implementation(libs.spring.log4j)
    implementation(libs.spring.actuator)
    implementation(libs.spring.cache)
    annotationProcessor(libs.spring.configurationProcessor)

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    // Discord-related
    implementation(libs.jda)
    implementation(libs.lavalink)

    // Metrics
    implementation(libs.micrometer.core)
    implementation(libs.micrometer.prometheus)

    // Other
    implementation(libs.brigadier)
    implementation(libs.sentry)
    implementation(libs.caffeine)

    testImplementation(libs.spring.test)
}

// We don't want this, as it gives us Logback, and we want to use Log4J 2
configurations.all {
    exclude("org.springframework.boot", "spring-boot-starter-logging")
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-Xjsr305=strict")
        }
    }
    withType<ProcessResources> {
        val tokens = mapOf(
            "version" to project.version.toString(),
            "jdaVersion" to libs.versions.jda.get(),
            "lavalinkVersion" to libs.versions.lavalink.get()
        )
        filter<ReplaceTokens>("tokens" to tokens)
    }
}
