// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
}

// Ovaj fajl je samo za postavljanje osnovnih pluginova za projekat.