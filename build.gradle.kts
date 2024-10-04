// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.androidx.room) apply false
    id("com.google.devtools.ksp") version "2.0.20-1.0.25"
    id("com.google.gms.google-services") version "4.4.2" apply false
    // Make sure that you have the Google services Gradle plugin
}
buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(libs.oss.licenses.plugin)
        classpath(libs.firebase.crashlytics.gradle)
    }
}
