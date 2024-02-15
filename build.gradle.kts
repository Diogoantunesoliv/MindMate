buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false // FIXME 1.9.0
    id("com.android.library") version "8.2.1" apply false // FIXME
    id("com.google.devtools.ksp") version "1.9.21-1.0.16" apply false // FIXME
    id("com.google.dagger.hilt.android") version "2.48" apply false // FIXME
    id("com.google.gms.google-services") version "4.4.1" apply false
    // Add the dependency for the Performance Monitoring Gradle plugin
    id("com.google.firebase.firebase-perf") version "1.4.2" apply false
}