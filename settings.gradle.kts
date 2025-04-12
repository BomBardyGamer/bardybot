enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://m2.dv8tion.net/releases")
        maven("https://repo.kryptonmc.org/snapshots")
        maven("https://nexus.velocitypowered.com/repository/maven-public/")
        maven("https://maven.lavalink.dev/releases")
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "BardyBot"
