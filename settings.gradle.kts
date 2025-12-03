pluginManagement {
    repositories {
<<<<<<< HEAD
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
=======
        google()
>>>>>>> 327fbe68a7604cec06676b7e0fb4b0bf92ba2aeb
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

<<<<<<< HEAD
rootProject.name = "FitFuelIE"
include(":app")
 
=======
rootProject.name = "FitFuel IE"
include(":app")
>>>>>>> 327fbe68a7604cec06676b7e0fb4b0bf92ba2aeb
