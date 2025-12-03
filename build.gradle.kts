// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
<<<<<<< HEAD
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
=======
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.android.library") version "8.1.4" apply false
    id("androidx.room") version "2.6.1" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.5" apply false
}
>>>>>>> 327fbe68a7604cec06676b7e0fb4b0bf92ba2aeb
