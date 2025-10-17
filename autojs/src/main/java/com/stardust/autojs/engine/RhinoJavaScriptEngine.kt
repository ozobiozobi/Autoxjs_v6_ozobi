package com.stardust.autojs.engine

import android.util.Log
import android.view.View
import com.stardust.autojs.core.ui.ViewExtras
import com.stardust.autojs.engine.module.AssetAndUrlModuleSourceProvider
import com.stardust.autojs.engine.module.ScopeRequire
import com.stardust.autojs.execution.ExecutionConfig
import com.stardust.autojs.project.ScriptConfig
import com.stardust.autojs.rhino.AndroidContextFactory
import com.stardust.autojs.rhino.AutoJsContext
import com.stardust.autojs.rhino.RhinoAndroidHelper
import com.stardust.autojs.rhino.TopLevelScope
import com.stardust.autojs.runtime.ScriptRuntime
import com.stardust.autojs.script.JavaScriptSource
import com.stardust.autojs.script.StringScriptSource
import com.stardust.pio.UncheckedIOException
import org.mozilla.javascript.Context
import org.mozilla.javascript.Script
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created by Stardust on 2017/4/2.
 */
// ozobi : 修复运行 UI 脚本导致的内存泄露
open class RhinoJavaScriptEngine(private val mAndroidContext: android.content.Context) :
    JavaScriptEngine() {

    private val wrapFactory = WrapFactory()
    private var context: Context? = enterContext()
    private var mScriptable: TopLevelScope? = createScope(context)
    private var thread: Thread? = null
    private var initScript: Script? = null


    fun getContext():Context?{
        return context
    }

    override fun put(name: String, value: Any?) {
        mScriptable?.let {
            ScriptableObject.putProperty(it, name, Context.javaToJS(value, it))
        }
    }

    override fun setRuntime(runtime: ScriptRuntime) {
        super.setRuntime(runtime)
        runtime.bridges.setup(this)
        runtime.topLevelScope = mScriptable
    }

    public override fun doExecution(source: JavaScriptSource): Any? {
        val newSource = modifyScriptString(source)
        val reader = newSource.nonNullScriptReader
        try {
            val script = context?.compileReader(reader, newSource.toString(), 1, null)
            return if (hasFeature(ScriptConfig.FEATURE_CONTINUATION)) {
                context?.executeScriptWithContinuations(script, mScriptable)
            } else {
                script?.exec(context, mScriptable)
            }
        } catch (e: IOException) {
            throw UncheckedIOException(e)
        }
    }

    fun modifyScriptString(source: JavaScriptSource): JavaScriptSource {
        val sourseName = source.name
        val reader = source.nonNullScriptReader
        val scriptString = reader.readText()
        val cwd = runtime.engines.myEngine().cwd()
        val reg = "(?s)<img[^>]*.*?>".toRegex()
        val reg2 = "src=\"./".toRegex()
        val matches = reg.findAll(scriptString)
        var replaceString = scriptString
        for (match in matches) {
            val tempStr = match.value.replace(reg2, "src=\"file://$cwd/")
            replaceString = replaceString.replace(match.value, tempStr)
        }
        return StringScriptSource(sourseName, replaceString)
    }

    fun hasFeature(feature: String): Boolean {
        val config = getTag(ExecutionConfig.tag) as ExecutionConfig?
        return config != null && config.scriptConfig.hasFeature(feature)
    }

    override fun forceStop() {
        Log.d(LOG_TAG, "forceStop: interrupt Thread: $thread")
        thread?.interrupt()
    }

    @Synchronized
    override fun destroy() {
        super.destroy()
        Log.d(LOG_TAG, "on destroy")
        Context.exit()
        context = null
        mScriptable = null
        thread = null
        initScript = null
    }

    override fun init() {
        thread = Thread.currentThread()
        mScriptable?.let {
            ScriptableObject.putProperty(it, "__engine__", this)
            initRequireBuilder(context, it)
            try {
                initScript = context?.compileReader(
                    InputStreamReader(mAndroidContext.assets.open("init.js")),
                    SOURCE_NAME_INIT,
                    1,
                    null
                )
                initScript?.exec(context, it)
            } catch (e: IOException) {
                throw UncheckedIOException(e)
            }
        }
    }

    private fun initRequireBuilder(context: Context?, scope: Scriptable) {
        val provider = AssetAndUrlModuleSourceProvider(
            mAndroidContext,
            listOf(
                AssetAndUrlModuleSourceProvider.MODULE_DIR,
                AssetAndUrlModuleSourceProvider.NPM_MODULE_DIR
            )
        )
        val require = context?.let {
            ScopeRequire(
                it, scope, SoftCachingModuleScriptProvider(provider),
                null, null, false
            )
        }
        require?.install(scope)
    }

    private fun createScope(context: Context?): TopLevelScope {
        val topLevelScope = TopLevelScope()
        topLevelScope.initStandardObjects(context, false)
        return topLevelScope
    }

    fun enterContext(): Context {
        val context = RhinoAndroidHelper(mAndroidContext).enterContext()
        setupContext(context)
        return context
    }

    fun setupContext(context: Context) {
        context.wrapFactory = wrapFactory
        (context as? AutoJsContext)?.let {
            it.rhinoJavaScriptEngine = this
        }
    }

    private inner class WrapFactory : AndroidContextFactory.WrapFactory() {
        override fun wrapAsJavaObject(
            cx: Context?,
            scope: Scriptable,
            javaObject: Any?,
            staticType: Class<*>?
        ): Scriptable? {
            return if (javaObject is View) {
                ViewExtras.getNativeView(scope, javaObject, staticType, runtime)
            } else {
                super.wrapAsJavaObject(cx, scope, javaObject, staticType)
            }
        }
    }

    companion object {
        const val SOURCE_NAME_INIT = "<init>"
        private const val LOG_TAG = "RhinoJavaScriptEngine"
    }
}



//
//open class RhinoJavaScriptEngine(private val mAndroidContext: android.content.Context) :
//    JavaScriptEngine() {
//
//    private val wrapFactory = WrapFactory()
//    val context: Context = enterContext()
//    private var mScriptable: TopLevelScope = createScope(this.context)
//    lateinit var thread: Thread
//        private set
//
//    private val initScript: Script by lazy<Script> {
//        try {
//            val reader = InputStreamReader(mAndroidContext.assets.open("init.js"))
//            val script = context.compileReader(reader, SOURCE_NAME_INIT, 1, null)
//            script
//        } catch (e: IOException) {
//            throw UncheckedIOException(e)
//        }
//    }
//
//    val scriptable: Scriptable
//        get() = mScriptable
//
//    init {
//
//    }
//
//    override fun put(name: String, value: Any?) {
//        ScriptableObject.putProperty(mScriptable, name, Context.javaToJS(value, mScriptable))
//    }
//
//    override fun setRuntime(runtime: ScriptRuntime) {
//        super.setRuntime(runtime)
//        runtime.bridges.setup(this)
//        runtime.topLevelScope = mScriptable
//    }
//
//    public override fun doExecution(source: JavaScriptSource): Any? {
//        val newSource =  modifyScriptString(source)
//        val reader = newSource.nonNullScriptReader
//        try {
//            val script = context.compileReader(reader, newSource.toString(), 1, null)
//            return if (hasFeature(ScriptConfig.FEATURE_CONTINUATION)) {
//                context.executeScriptWithContinuations(script, mScriptable)
//            } else {
//                script.exec(context, mScriptable)
//            }
//        } catch (e: IOException) {
//            throw UncheckedIOException(e)
//        }
//    }
//
//    fun modifyScriptString(source: JavaScriptSource):JavaScriptSource{
//        val sourseName = source.name
//        val reader = source.nonNullScriptReader
//        val scriptString = reader.readText()
//        val cwd = runtime.engines.myEngine().cwd()
//        val reg = "(?s)<img[^>]*.*?>".toRegex()
//        val reg2 = "src=\"./".toRegex()
//        val matches = reg.findAll(scriptString)
//        var replaceString = scriptString
//        for(match in matches){
//            val tempStr = match.value.replace(reg2,"src=\"file://$cwd/")
//            replaceString = replaceString.replace(match.value,tempStr)
//        }
//        val newSource = StringScriptSource(sourseName,replaceString)
//
//        return newSource
//    }
//    // <
//
//    fun hasFeature(feature: String): Boolean {
//        val config = getTag(ExecutionConfig.tag) as ExecutionConfig?
//        return config != null && config.scriptConfig.hasFeature(feature)
//    }
//
//
//    override fun forceStop() {
//        Log.d(LOG_TAG, "forceStop: interrupt Thread: $thread")
//        thread.interrupt()
//    }
//
//    @Synchronized
//    override fun destroy() {
//        super.destroy()
//        Log.d(LOG_TAG, "on destroy")
//        Context.exit()
//    }
//
//    override fun init() {
//        thread = Thread.currentThread()
//        ScriptableObject.putProperty(mScriptable, "__engine__", this)
//        initRequireBuilder(context, mScriptable)
//        try {
//            context.executeScriptWithContinuations(initScript, mScriptable)
//        } catch (e: IllegalArgumentException) {
//            if ("Script argument was not a script or was not created by interpreted mode " == e.message) {
//                initScript.exec(context, mScriptable)
//            } else {
//                throw e
//            }
//        }
//    }
//
//    private fun initRequireBuilder(context: Context, scope: Scriptable) {
//        val provider = AssetAndUrlModuleSourceProvider(
//            mAndroidContext,
//            listOf(
//                AssetAndUrlModuleSourceProvider.MODULE_DIR,
//                AssetAndUrlModuleSourceProvider.NPM_MODULE_DIR
//            )
//        )
//        val require = ScopeRequire(
//            context, scope, SoftCachingModuleScriptProvider(provider),
//            null, null, false
//        )
//        require.install(scope)
//    }
//
//    private fun createScope(context: Context): TopLevelScope {
//        val topLevelScope = TopLevelScope()
//        topLevelScope.initStandardObjects(context, false)
//        return topLevelScope
//    }
//
//    fun enterContext(): Context {
//        val context = RhinoAndroidHelper(mAndroidContext).enterContext()
//        setupContext(context)
//        return context
//    }
//
//    fun setupContext(context: Context) {
//        context.wrapFactory = wrapFactory
//        (context as? AutoJsContext)?.let {
//            it.rhinoJavaScriptEngine = this
//        }
//    }
//
//
//    private inner class WrapFactory : AndroidContextFactory.WrapFactory() {
//        override fun wrapAsJavaObject(
//            cx: Context?,
//            scope: Scriptable,
//            javaObject: Any?,
//            staticType: Class<*>?
//        ): Scriptable? {
//            //Log.d(LOG_TAG, "wrapAsJavaObject: java = " + javaObject + ", result = " + result + ", scope = " + scope);
//            return if (javaObject is View) {
//                ViewExtras.getNativeView(scope, javaObject, staticType, runtime)
//            } else {
//                super.wrapAsJavaObject(cx, scope, javaObject, staticType)
//            }
//        }
//
//    }
//
//    companion object {
//        const val SOURCE_NAME_INIT = "<init>"
//        private const val LOG_TAG = "RhinoJavaScriptEngine"
//
//    }
//
//
//}
