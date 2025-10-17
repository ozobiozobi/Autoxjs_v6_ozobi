plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.ozobi.ppocrv5"
    compileSdk = Versions.compileSdk

    ndkVersion = "26.1.10909125"
    
    defaultConfig {

        minSdk = Versions.minSdk

//        externalNativeBuild {
//            cmake {
//                arguments("-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON")
//            }
//        }
    }

//    dependencies {
//        implementation(libs.support.v4)
//    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}
dependencies {
    implementation(libs.core.ktx)
}
