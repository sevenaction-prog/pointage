plugins {
    id("com.android.application")
}

android {
    namespace = "be.sfl.pointage"
    compileSdk = 35

    defaultConfig {
        applicationId = "be.sfl.consultpresence"
        minSdk = 26
        targetSdk = 35
        versionCode = 4
        versionName = "0.2.1"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
