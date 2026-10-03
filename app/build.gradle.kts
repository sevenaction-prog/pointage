plugins {
    id("com.android.application")
}

android {
    namespace = "be.sfl.pointage"
    compileSdk = 35

    defaultConfig {
        applicationId = "be.sfl.pointage"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "0.2.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
