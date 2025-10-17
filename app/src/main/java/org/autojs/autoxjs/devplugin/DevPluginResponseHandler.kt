package org.autojs.autoxjs.devplugin

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.stardust.app.GlobalAppContext.toast
import com.stardust.autojs.execution.ScriptExecution
import com.stardust.autojs.project.ProjectLauncher
import com.stardust.autojs.script.StringScriptSource
import com.stardust.io.Zip
import com.stardust.pio.PFiles
import com.stardust.util.MD5
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.autojs.autoxjs.Pref
import org.autojs.autoxjs.R
import org.autojs.autoxjs.autojs.AutoJs
import org.autojs.autoxjs.model.script.Scripts.run
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Created by Stardust on 2017/5/11.
 */
class DevPluginResponseHandler(private val cacheDir: File) : Handler {

    companion object {
        val TAG = DevPluginResponseHandler::class.java.simpleName
    }

    private val router = Router.RootRouter("type")
        .handler("command", Router("command")
            .handler("run") { data: JsonObject ->
                val script = data["script"].asString
                val name = getName(data) ?: ""
                val id = data["id"].asString
                runScript(id, name, script)
                true
            }
            .handler("captureToGoScoper") { data: JsonObject ->
                val script = """ 
                function collectNodeInfo(node) {
                    if (!node) return null;
                    let nodeInfo = { text: node.text(), id: node.id(), desc: node.desc(), clz: node.className(), pkg: node.packageName(), bounds: node.bounds(), index: node.indexInParent(), depth: node.depth(), clickable: node.clickable(), longClickable: node.longClickable(), checkable: node.checkable(), checked: node.checked(), selected: node.selected(), enabled: node.enabled(), visibleToUser: node.visibleToUser(), row: node.row(), column: node.column(), rowSpan: node.rowSpan(), columnSpan: node.columnSpan(), rowCount: node.rowCount(), columnCount: node.columnCount(), drawingOrder: node.drawingOrder(), children: [] };
                    let children = node.children();
                    if (children && children.length > 0) {
                        for (let i = 0; i < children.length; i++) {
                            let childInfo = collectNodeInfo(children[i]);
                            if (childInfo) {
                                nodeInfo.children.push(childInfo);
                            }
                        }
                    }
                    return nodeInfo;
                }
                function removeOldFiles() {
                    let list = files.listDir("/sdcard/", function (_name) {
                        return _name.startsWith("nodeInfo_") || _name.startsWith("nodeScreen_");
                    });
                    list.forEach((file) => {
                        files.remove("/sdcard/" + file);
                    });
                }
                function saveNodeTree() {
                    let rootNode = auto.root;
                    let nodeTree = collectNodeInfo(rootNode);
                    if (!nodeTree) {
                        toastLog("获取节点信息失败");
                        return;
                    }
                    let timestamp = new Date().getTime();
                    let jsonPath = "/sdcard/nodeInfo_" + timestamp + ".json";
                    let imgPath = "/sdcard/nodeScreen_" + timestamp + ".png";
                    try {
                        files.write(jsonPath, JSON.stringify(nodeTree, null, 2));
                        captureScreen(imgPath);
                        uploadFilesToAPI(imgPath, jsonPath);
                    } catch (e) {}
                }
                function uploadFilesToAPI(screenshotPath, jsonPath) {
                    try {
                        let res = http.postMultipart(API_ENDPOINT, { screenshot: open(screenshotPath), nodeJson: open(jsonPath) });
                        if (res.statusCode >= 200 && res.statusCode < 300) {
                            toastLog("上传成功");
                        }
                    } catch (e) {}
                }
                if (!requestScreenCapture()) {
                    exit();
                }
                removeOldFiles();
                const API_ENDPOINT = "http://__ip__:8000/api/screenshots/upload";
                saveNodeTree();
                """
                val name = getName(data) ?: ""
                val id = data["id"].asString
                val replaceScript = DevPlugin.serverAddress?.let {
                    script.replace("__ip__", it)
                }
                if(replaceScript != null){
                    runScript(id, name, replaceScript)
                }else{
                    Log.d("ozobiLog","ip 为空")
                }
                true
            }
            .handler("stop") { data: JsonObject ->
                val id = data["id"].asString
                stopScript(id)
                true
            }
            .handler("save") { data: JsonObject ->
                val script = data["script"].asString
                val name = getName(data) ?: ""
                saveScript(name, script)
                true
            }
            .handler("rerun") { data: JsonObject ->
                val id = data["id"].asString
                val script = data["script"].asString
                val name = getName(data) ?: ""
                try {
                    stopScript(id)
                } catch (e: Exception) {
                }
                runScript(id, name, script)
                true
            }
            .handler("stopAll") { data: JsonObject? ->
                AutoJs.getInstance().scriptEngineService.get()?.stopAllAndToast()
                true
            })
        .handler("bytes_command", Router("command")
            .handler("run_project") { data: JsonObject ->
                launchProject(data["dir"].asString)
                true
            }
            .handler("save_project") { data: JsonObject ->
                saveProject(data["name"].asString, data["dir"].asString)
                true
            })

    private val mScriptExecutions = HashMap<String, ScriptExecution?>()

    override fun handle(data: JsonObject): Boolean {
        return router.handle(data)
    }

    suspend fun handleBytes1(data: JsonObject, bytes: Bytes): File = withContext(Dispatchers.IO) {
        val id = data["data"].asJsonObject["id"].asString
        val projectDir = MD5.md5(id)
        val dir = File(cacheDir, projectDir)
        Zip.unzip(ByteArrayInputStream(bytes.bytes), dir)
        dir
    }

    private fun runScript(viewId: String, name: String, script: String) {
        val name1 = if (name.isEmpty()) "[$viewId]"
        else PFiles.getNameWithoutExtension(name)
        mScriptExecutions[viewId] = run(StringScriptSource("[remote]$name1", script))
    }

    private fun launchProject(dir: String) {
        try {
            AutoJs.getInstance().scriptEngineService.get()?.let {
                ProjectLauncher(dir)
                    .launch(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toast(R.string.text_invalid_project)
        }
    }

    private fun stopScript(viewId: String) {
        val execution = mScriptExecutions[viewId]
        execution?.engine?.forceStop()
        mScriptExecutions.remove(viewId)
    }

    private fun getName(data: JsonObject): String? {
        val element = data["name"]
        return if (element is JsonNull) {
            null
        } else element.asString
    }

    private fun saveScript(name: String, script: String) {
        val name1 = if (name.isEmpty()) "untitled" else PFiles.getName(name)
        //PFiles.getNameWithoutExtension(name);
//        if (!name1.endsWith(".js")) {
//            name = name + ".js";
//        }
        val file = File(Pref.getScriptDirPath(), name1)
        PFiles.ensureDir(file.path)
        PFiles.write(file, script)
        toast(R.string.text_script_save_successfully)
    }

    @SuppressLint("CheckResult")
    private fun saveProject(name: String, dir: String) {
        val name1 = if (name.isEmpty()) "untitled" else PFiles.getNameWithoutExtension(name)
        val toDir = File(Pref.getScriptDirPath(), name1)
        CoroutineScope(Dispatchers.IO).launch {
            flow<String> {
                copyDir(File(dir), toDir)
                emit(toDir.path)
            }
                .flowOn(Dispatchers.Main)
                .catch {
                    toast(R.string.text_project_save_error, it.message)
                }.collect {
                    toast(R.string.text_project_save_success, it)
                }
        }
    }

    private fun copyDir(fromDir: File, toDir: File) {
        toDir.mkdirs()
        val files = fromDir.listFiles()
        if (files == null || files.isEmpty()) {
            return
        }
        for (file in files) {
            if (file.isDirectory) {
                copyDir(file, File(toDir, file.name))
            } else {
                val fos = FileOutputStream(File(toDir, file.name))
                PFiles.write(FileInputStream(file), fos, true)
            }
        }
    }

    init {
        if (cacheDir.exists()) {
            if (cacheDir.isDirectory) {
                PFiles.deleteFilesOfDir(cacheDir)
            } else {
                cacheDir.delete()
                cacheDir.mkdirs()
            }
        }
    }
}