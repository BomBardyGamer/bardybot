enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://m2.dv8tion.net/releases")
        maven("https://jitpack.io")
        maven("https://nexus.velocitypowered.com/repository/maven-public/")
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "BardyBot"
