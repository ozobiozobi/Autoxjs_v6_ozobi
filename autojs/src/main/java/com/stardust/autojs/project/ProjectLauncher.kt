package com.stardust.autojs.project

import android.util.Log
import com.stardust.autojs.ScriptEngineService
import com.stardust.autojs.execution.ExecutionConfig
import com.stardust.autojs.script.JavaScriptFileSource
import java.io.File


/**
 * Modified by wilinz on 2022/5/23
 */
class ProjectLauncher(private val mProjectDir: String) {
    private val mProjectConfig: ProjectConfig = ProjectConfig.fromProject(File(mProjectDir))!!
    private val mMainScriptFile: File? = mProjectConfig.mainScript?.let { File(mProjectDir, it) }
    fun launch(service: ScriptEngineService) {
        val config = ExecutionConfig()
        config.workingDirectory = mProjectDir
        config.scriptConfig.features = mProjectConfig.features
        service.execute(mMainScriptFile?.let { JavaScriptFileSource(it) }, config)
    }

}