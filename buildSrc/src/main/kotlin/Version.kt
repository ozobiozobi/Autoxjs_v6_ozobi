import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.gradle.internal.impldep.com.google.gson.annotations.SerializedName
import java.io.File

lateinit var versions: Versions
    private set

const val kotlin_version = "1.6.21"
const val compose_version = "1.2.0-rc01"

fun initVersions(file: File) {
    val json = file.readText()
    versions = Gson().fromJson(json, Versions::class.java)
    println(GsonBuilder().setPrettyPrinting().create().toJson(versions))
}

data class Versions(
    @SerializedName("appVersionCode")
    val appVersionCode: Int = 6586,
    @SerializedName("appVersionName")
    val appVersionName: String = "6.5.8.6",
    @SerializedName("buildTool")
    val buildTool: String = "35.0.0",
    @SerializedName("compile")
    val compile: Int = 35,
    @SerializedName("devVersionCode")
    val devVersionCode: Int = 6586,
    @SerializedName("devVersionName")
    val devVersionName: String = "6.5.8.6",
    @SerializedName("IDE")
    val ide: String = "Android Studio Koala Feature Drop | 2024.1.2",
    @SerializedName("JDK")
    val jdk: String = "17",
    @SerializedName("mini")
    val mini: Int = 21,
    @SerializedName("target")
    val target: Int = 28
)
