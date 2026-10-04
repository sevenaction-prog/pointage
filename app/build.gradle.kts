plugins {
    id("com.android.application")
}

android {
    namespace = "be.sfl.consultpresence.cleanroom"
    compileSdk = 35

    defaultConfig {
        applicationId = "be.sfl.consultpresence.cleanroom"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0-cleanroom"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
