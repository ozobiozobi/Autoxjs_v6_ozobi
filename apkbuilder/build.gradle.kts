plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = Dependencies.compile

    defaultConfig {
        minSdk = Dependencies.mini
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildFeatures{
        buildConfig = true
    }
    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android.txt"),
                    "proguard-rules.pro"
                )
            )
        }
    }
    namespace = "com.stardust.autojs.apkbuilder"
}

dependencies {

    implementation(libs.okhttp)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core){
        exclude(group = "com.android.support",module = "support-annotations")
    }
    testImplementation(libs.junit)
    api(files("libs/tiny-sign-0.9.jar"))
    api(libs.commons.io)
    implementation(libs.core.ktx)
    implementation(kotlin("script-runtime"))
}
repositories {
    mavenCentral()
}
