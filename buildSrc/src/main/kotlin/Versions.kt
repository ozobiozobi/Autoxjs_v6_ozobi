object Versions {
    val appVersionName: String = "6.5.8.22"
    val appVersionCode: Int = appVersionName.replace(".","").toInt()

    val devVersionCode: Int = appVersionCode
    val devVersionName: String = appVersionName

    val minSdk: Int = 24
    val targetSdk: Int = 28

    const val kotlin_version = "1.6.21"
    const val compose_version = "1.2.0-rc01"

    val buildTool: String = "35.0.0"
    val compileSdk: Int = 35

    val ide: String = "Android Studio Koala Feature Drop | 2024.1.2"
    val jdk: String = "17"

}
