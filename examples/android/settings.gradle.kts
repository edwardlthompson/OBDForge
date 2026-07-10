pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "obdforge-android"
include(":app")
include(":core:transport")
include(":core:protocol")
include(":core:vehicle")
include(":core:demo")
include(":core:safety")
include(":core:data")
include(":core:ai")
include(":core:plugin-api")
include(":feature:dashboard")
include(":feature:logging")
include(":feature:browser")
include(":feature:voice")
include(":feature:shop")
include(":feature:performance")
include(":feature:enhanced-oem")
include(":feature:rawcan")
include(":feature:flash")
include(":feature:education")
include(":feature:integration")
