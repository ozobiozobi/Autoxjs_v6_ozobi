plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))
        }
    }
    buildFeatures {
        buildConfig = true
        aidl = true
    }
    lint.abortOnError = false
    sourceSets {
        named("main") {
//            jniLibs.srcDirs = listOf("src/main/jniLibs")
            res.srcDirs("src/main/res","src/main/res-i18n")
            // Added by ozobi - 2025/01/08 > aidl 文件路径设置
//            aidl.srcDirs("src/main/aidl")
//            java.srcDirs("src/main/java","src/main/aidl")
            // <
        }
    }
    namespace = "com.stardust.autojs"

}

dependencies {
    implementation(project(":ppocrv5"))

    implementation("com.madgag.spongycastle:core:1.58.0.0")
    implementation("com.madgag.spongycastle:prov:1.58.0.0")

    // Added by ozobi - 2025/03/07 > 添加 shizuku 依赖
    implementation(libs.api)
    implementation(libs.provider)
    // <

    implementation(libs.api)
    implementation(libs.provider)

    androidTestImplementation(libs.espresso.core)
    debugImplementation(libs.leakcanary.android)
    implementation(libs.leakcanary.`object`.watcher.android)
    testImplementation(libs.junit)

    implementation(libs.documentfile)
    implementation("androidx.preference:preference-ktx:1.2.0")
    api(libs.eventbus)
    api("net.lingala.zip4j:zip4j:1.3.2")
    api("com.afollestad.material-dialogs:core:0.9.2.3"){
        exclude(group = "com.android.support")
    }
    api(libs.material)
    api("com.github.hyb1996:EnhancedFloaty:0.31")
    api("com.makeramen:roundedimageview:2.3.0")
    // OkHttp
    api(libs.okhttp)
    // JDeferred
    api("org.jdeferred:jdeferred-android-aar:1.2.6")
    // RootShell
    api("com.github.Stericson:RootShell:1.6")
//    implementation("com.github.Stericson:RootShell:1.4")
    // Gson
    api(libs.google.gson)
    // log4j
    api(group = "de.mindpipe.android", name = "android-logging-log4j", version = "1.0.3")
    api(group = "log4j", name = "log4j", version = "1.2.17")
    api(project(path = ":common"))
    api(project(path = ":automator"))
    api(project(path = ":LocalRepo:libtermexec"))
    api(project(path = ":LocalRepo:emulatorview"))
    api(project(path = ":LocalRepo:term"))
    api(project(path = ":LocalRepo:p7zip"))
    api(project(path = ":LocalRepo:OpenCV"))
    api(project(":paddleocr"))
    // libs
    api(fileTree("../app/libs"){include("dx.jar", "rhino-1.7.14-jdk7.jar","AdbLib.jar")})// Modified by ozobi - 2025/01/19 : 添加远程 AdbShell > "AdbLib.jar"
    api("cz.adaptech:tesseract4android:4.1.1")
    api(libs.text.recognition)
    api(libs.text.recognition.chinese)
    api(libs.text.recognition.devanagari)
    api(libs.text.recognition.japanese)
    api(libs.text.recognition.korean)
}

