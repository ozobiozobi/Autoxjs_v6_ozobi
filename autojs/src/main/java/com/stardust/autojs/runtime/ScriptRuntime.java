package com.stardust.autojs.runtime;

import static com.stardust.autojs.util.StringTools.createStringArray;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ozobi.ppocrv5.PPOCRV5;
import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.R;
import com.stardust.autojs.ScriptEngineService;
import com.stardust.autojs.annotation.ScriptVariable;
import com.stardust.autojs.core.accessibility.AccessibilityBridge;
import com.stardust.autojs.core.accessibility.SimpleActionAutomator;
import com.stardust.autojs.core.accessibility.UiSelector;
import com.stardust.autojs.core.activity.ActivityInfoProvider;
import com.stardust.autojs.core.image.Colors;
import com.stardust.autojs.core.image.capture.ScreenCaptureRequester;
import com.stardust.autojs.core.looper.Loopers;
import com.ozobi.adbkeyboard.AdbIME;
import com.ozobi.remoteadb.AdbShell;
import com.stardust.autojs.core.permission.Permissions;
import com.stardust.autojs.core.util.ProcessShell;
import com.stardust.autojs.rhino.AndroidClassLoader;
import com.stardust.autojs.rhino.TopLevelScope;
import com.stardust.autojs.rhino.continuation.Continuation;
import com.stardust.autojs.runtime.api.AbstractShell;
import com.stardust.autojs.runtime.api.AppUtils;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.autojs.runtime.api.Device;
import com.stardust.autojs.runtime.api.Dialogs;
import com.stardust.autojs.runtime.api.Engines;
import com.stardust.autojs.runtime.api.Events;
import com.stardust.autojs.runtime.api.Files;
import com.stardust.autojs.runtime.api.Floaty;
import com.stardust.autojs.runtime.api.GoogleMLKit;
import com.stardust.autojs.runtime.api.Images;
import com.stardust.autojs.runtime.api.Media;
import com.stardust.autojs.runtime.api.Plugins;
import com.stardust.autojs.runtime.api.Sensors;
import com.stardust.autojs.runtime.api.SevenZip;
import com.stardust.autojs.runtime.api.Threads;
import com.stardust.autojs.runtime.api.Timers;
import com.stardust.autojs.runtime.api.UI;
import com.stardust.autojs.runtime.exception.ScriptEnvironmentException;
import com.stardust.autojs.runtime.exception.ScriptException;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.autojs.util.ObjectWatcher;
import com.stardust.concurrent.VolatileDispose;
import com.stardust.lang.ThreadCompat;
import com.stardust.pio.UncheckedIOException;
import com.stardust.util.ClipboardUtil;
import com.stardust.util.ScreenMetrics;
import com.stardust.util.SdkVersionUtil;
import com.stardust.util.Supplier;
import com.stardust.util.UiHandler;
import com.stardust.util.ViewUtil;

import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptStackElement;
import org.mozilla.javascript.Scriptable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dalvik.system.DexClassLoader;

/**
 * Created by Stardust on 2017/1/27.
 */

public class ScriptRuntime {

    private static final String TAG = "ScriptRuntime";

    public static class Builder {
        private UiHandler mUiHandler;
        private Console mConsole;
        private AccessibilityBridge mAccessibilityBridge;
        private Supplier<AbstractShell> mShellSupplier;
        private ScreenCaptureRequester mScreenCaptureRequester;
        private AppUtils mAppUtils;
        private ScriptEngineService mEngineService;

        public Builder() {

        }

        public Builder setUiHandler(UiHandler uiHandler) {
            mUiHandler = uiHandler;
            return this;
        }

        public Builder setConsole(Console console) {
            mConsole = console;
            return this;
        }

        public Builder setAccessibilityBridge(AccessibilityBridge accessibilityBridge) {
            mAccessibilityBridge = accessibilityBridge;
            return this;
        }

        public Builder setShellSupplier(Supplier<AbstractShell> shellSupplier) {
            mShellSupplier = shellSupplier;
            return this;
        }

        public Builder setScreenCaptureRequester(ScreenCaptureRequester requester) {
            mScreenCaptureRequester = requester;
            return this;
        }

        public Builder setAppUtils(AppUtils appUtils) {
            mAppUtils = appUtils;
            return this;
        }

        public Builder setEngineService(WeakReference<ScriptEngineService> service) {
            mEngineService = service.get();
            return this;
        }


        public ScriptRuntime build() {
            return new ScriptRuntime(this);
        }

    }

    @ScriptVariable
    public final AppUtils app;

    @ScriptVariable
    public final Console console;

    @ScriptVariable
    public final SimpleActionAutomator automator;

    @ScriptVariable
    public final ActivityInfoProvider info;

    @ScriptVariable
    public final UI ui;

    @ScriptVariable
    public final Dialogs dialogs;

    @ScriptVariable
    public Events events;

    @ScriptVariable
    public final ScriptBridges bridges = new ScriptBridges();

    @ScriptVariable
    public Loopers loopers;

    @ScriptVariable
    public Timers timers;

    @ScriptVariable
    public Device device;

    @ScriptVariable
    public DeviceAdminReceiverMsg deviceAdminReceiverMsg;

    @ScriptVariable
    public DevicePolicyManager devicePolicyManager;

    @ScriptVariable
    public AdbIME adbIMEShellCommand;

//    @ScriptVariable
//    public SendEventCommand sendeventCommand;
    // <
    @ScriptVariable
    public final AccessibilityBridge accessibilityBridge;

    @ScriptVariable
    public final Engines engines;

    @ScriptVariable
    public Threads threads;

    @ScriptVariable
    public final Floaty floaty;

    @ScriptVariable
    public UiHandler uiHandler;

    @ScriptVariable
    public final Colors colors = new Colors();

    @ScriptVariable
    public final Files files;

    @ScriptVariable
    public SevenZip zips;

    @ScriptVariable
    public Sensors sensors;

    @ScriptVariable
    public final Media media;

    @ScriptVariable
    public final Plugins plugins;

    @ScriptVariable
    public final GoogleMLKit gmlkit;
//    @ScriptVariable
//    public final Paddle paddle;

    public final PPOCRV5 ppocrv5;

    private Images images;

    private static WeakReference<Context> applicationContext;
    private Map<String, Object> mProperties = new ConcurrentHashMap<>();
    private AbstractShell mRootShell;
    private Supplier<AbstractShell> mShellSupplier;
    private ScreenMetrics mScreenMetrics = new ScreenMetrics();
    private Thread mThread;
    private TopLevelScope mTopLevelScope;
    private final String logTag = "ozobiLog";

    protected ScriptRuntime(Builder builder) {
        uiHandler = builder.mUiHandler;
        Context context = uiHandler.getContext();
        app = builder.mAppUtils;
        console = builder.mConsole;
        accessibilityBridge = builder.mAccessibilityBridge;
        mShellSupplier = builder.mShellSupplier;
        ui = new UI(context, this);
        this.automator = new SimpleActionAutomator(accessibilityBridge, this);
        automator.setScreenMetrics(mScreenMetrics);
        this.info = accessibilityBridge.getInfoProvider();
        images = new Images(context, this, builder.mScreenCaptureRequester);
        engines = new Engines(builder.mEngineService, this);
        dialogs = new Dialogs(this);
        device = new Device(context);
        
        deviceAdminReceiverMsg = DeviceAdminReceiverMsg.INSTANCE;
        devicePolicyManager = DevicePolicyManager.INSTANCE;
//        sendeventCommand = new SendEventCommand(getApplicationContext());
        // <
        floaty = new Floaty(uiHandler, ui, this);
        files = new Files(this);
        media = new Media(context, this);
        plugins = new Plugins(context, this);
        zips = new SevenZip();
        gmlkit = new GoogleMLKit();
        ppocrv5 = new PPOCRV5(getApplicationContext());
//        paddle = new Paddle();
    }

    public void init() {
        if (loopers != null)
            throw new IllegalStateException("already initialized");
        threads = new Threads(this);
        timers = new Timers(this);
        loopers = new Loopers(this);
        events = new Events(uiHandler.getContext(), accessibilityBridge, this);
        mThread = Thread.currentThread();
        sensors = new Sensors(uiHandler.getContext(), this);
        AdbIME.packageName = getApplicationContext().getPackageName();
        adbIMEShellCommand = new AdbIME();
    }
    public Thread getmThread(){
        return mThread;
    }
    public Threads getThreads(){
        return threads;
    }
    public static AdbShell adbConnect(String host,int port){
        return new AdbShell(getApplicationContext(),host,port);
    }
    public static void termux(String command, Boolean runBackground, int sessionAction, Boolean top) {
        if (command == null || command.isEmpty()) {
            return;
        }
        Intent intent = getTermuxCommandIntent(command, runBackground, sessionAction, top);
        sendTermuxIntent(intent);
    }

    public static void sendTermuxIntent(Intent intent) {
        getApplicationContext().startService(intent);
    }

    public static @NonNull Intent getTermuxCommandIntent(String command, Boolean runBackground, int sessionAction, Boolean top) {
        Intent intent = new Intent();
        intent.setClassName("com.termux", "com.termux.app.RunCommandService");
        intent.setAction("com.termux.RUN_COMMAND");
        intent.putExtra("com.termux.RUN_COMMAND_PATH", "/data/data/com.termux/files/usr/bin/bash");
        intent.putExtra("com.termux.RUN_COMMAND_ARGUMENTS", new String[]{"-c", command});
        intent.putExtra("com.termux.RUN_COMMAND_WORKDIR", "/data/data/com.termux/files/home");
        intent.putExtra("com.termux.RUN_COMMAND_BACKGROUND", runBackground);
        intent.putExtra("com.termux.RUN_COMMAND_SESSION_ACTION", sessionAction);
        if(top){
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return intent;
    }

    public static String[] stringArray(String... strings){
        return createStringArray(strings);
    }

    public static int getStatusBarHeight(){
        return ViewUtil.getStatusBarHeight(getApplicationContext());
    }

    public TopLevelScope getTopLevelScope() {
        return mTopLevelScope;
    }

    public void setTopLevelScope(TopLevelScope topLevelScope) {
        if (mTopLevelScope != null) {
            throw new IllegalStateException("top level has already exists");
        }
        mTopLevelScope = topLevelScope;
    }

    public static void setApplicationContext(Context context) {
        applicationContext = new WeakReference<>(context);
    }

    public static Context getApplicationContext() {
        if (applicationContext == null || applicationContext.get() == null) {
            throw new ScriptEnvironmentException("No application context");
        }
        return applicationContext.get();
    }

    public UiHandler getUiHandler() {
        return uiHandler;
    }

    public AccessibilityBridge getAccessibilityBridge() {
        return accessibilityBridge;
    }

    public void toast(final String text) {
        uiHandler.toast(text);
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new ScriptInterruptedException();
        }
    }

    public void setClip(final String text) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ClipboardUtil.setClip(uiHandler.getContext(), text);
            return;
        }
        VolatileDispose<Object> dispose = new VolatileDispose<>();
        uiHandler.post(() -> {
            ClipboardUtil.setClip(uiHandler.getContext(), text);
            dispose.setAndNotify(text);
        });
        dispose.blockedGet();
    }

    public String getClip() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            return ClipboardUtil.getClipOrEmpty(uiHandler.getContext()).toString();
        }
        final VolatileDispose<String> clip = new VolatileDispose<>();
        uiHandler.post(() -> clip.setAndNotify(ClipboardUtil.getClipOrEmpty(uiHandler.getContext()).toString()));
        return clip.blockedGetOrThrow(ScriptInterruptedException.class);
    }

    public AbstractShell getRootShell() {
        ensureRootShell();
        return mRootShell;
    }

    private void ensureRootShell() {
        if (mRootShell == null) {
            mRootShell = mShellSupplier.get();
            mRootShell.SetScreenMetrics(mScreenMetrics);
            mShellSupplier = null;
        }
    }

    public AbstractShell.Result shell(String cmd, int root) {
        return ProcessShell.execCommand(cmd, root != 0);
    }

    public UiSelector selector() {
        return new UiSelector(accessibilityBridge);
    }

    public boolean isStopped() {
        return Thread.currentThread().isInterrupted();
    }

    public static void requiresApi(int i) {
        if (Build.VERSION.SDK_INT < i) {
            throw new ScriptException(GlobalAppContext.getString(R.string.text_requires_sdk_version_to_run_the_script) + SdkVersionUtil.sdkIntToString(i));
        }
    }

    public void requestPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        Context context = uiHandler.getContext();
        permissions = Permissions.getPermissionsNeedToRequest(context, permissions);
        if (permissions.length == 0)
            return;
        Permissions.requestPermissions(context, permissions);
    }

    public DexClassLoader loadJar(String path) {
        path = files.path(path);
        try {
            return ((AndroidClassLoader) ContextFactory.getGlobal().getApplicationClassLoader()).loadJar(new File(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public DexClassLoader loadDex(String path) {
        path = files.path(path);
        try {
            return ((AndroidClassLoader) ContextFactory.getGlobal().getApplicationClassLoader()).loadDex(new File(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void exit() {
        mThread.interrupt();
        engines.myEngine().forceStop();
        threads.exit();
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new ScriptInterruptedException();
        }
    }

    public void exit(Throwable e) {
        engines.myEngine().uncaughtException(e);
        exit();
    }

    @Deprecated
    public void stop() {
        exit();
    }


    public void setScreenMetrics(int width, int height) {
        mScreenMetrics.setScreenMetrics(width, height);
    }

    public ScreenMetrics getScreenMetrics() {
        return mScreenMetrics;
    }

    public void ensureAccessibilityServiceEnabled() {
        accessibilityBridge.ensureServiceEnabled();
    }

    public void onExit() {
        Log.d(TAG, "on exit");
        //清除interrupt状态
        ThreadCompat.interrupted();
        //悬浮窗需要第一时间关闭以免出现恶意脚本全屏悬浮窗屏蔽屏幕并且在exit中写死循环的问题
        ignoresException(floaty::closeAll);
        try {
            events.emit("exit");
            if (console.isAutoHide()) {
                console.log("系统消息：任务结束,3秒后该窗口关闭");
                uiHandler.postDelayed(console::hide, 4000);
            }
        } catch (Throwable e) {
            console.error("exception on exit: ", e);
        }
        ignoresException(threads::shutDownAll);
        ignoresException(events::recycle);
        ignoresException(media::recycle);
        ignoresException(loopers::recycle);
        ignoresException(() -> {
            if (mRootShell != null) mRootShell.exit();
            mRootShell = null;
            mShellSupplier = null;
            adbIMEShellCommand = null;
            deviceAdminReceiverMsg = null;
            devicePolicyManager = null;
        });
        ignoresException(images::releaseScreenCapturer);
        ignoresException(sensors::unregisterAll);
        ignoresException(timers::recycle);
        ignoresException(ui::recycle);
//        ignoresException(paddle::release);
         ObjectWatcher.Companion.getDefault().watch(this, engines.myEngine().toString() + "::" + TAG);
//        if(BuildConfig.DEBUG){
            //引用检查
            // release 状态不启用监听
            // AppWatcher.INSTANCE.getObjectWatcher().expectWeaklyReachable(this,
            //         engines.myEngine().toString() + "::" + TAG);
//        }
    }

    private void ignoresException(Runnable r) {
        try {
            r.run();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public Object getImages() {
        return images;
    }

    public Object getProperty(String key) {
        return mProperties.get(key);
    }

    public Object putProperty(String key, Object value) {
        return mProperties.put(key, value);
    }

    public Object removeProperty(String key) {
        return mProperties.remove(key);
    }

    public Continuation createContinuation() {
        return Continuation.Companion.create(this, mTopLevelScope);
    }

    public Continuation createContinuation(Scriptable scope) {
        return Continuation.Companion.create(this, scope);
    }

    public static String getStackTrace(Throwable e, boolean printJavaStackTrace) {
        String message = e.getMessage();
        StringBuilder scriptTrace = new StringBuilder(message == null ? "" : message + "\n");
        if (e instanceof RhinoException) {
            RhinoException rhinoException = (RhinoException) e;
            scriptTrace.append(rhinoException.details()).append("\n");
            for (ScriptStackElement element : rhinoException.getScriptStack()) {
                element.renderV8Style(scriptTrace);
                scriptTrace.append("\n");
            }
            if (printJavaStackTrace) {
                scriptTrace.append("- - - - - - - - - - -\n");
            } else {
                return scriptTrace.toString();
            }
        }
        try {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            e.printStackTrace(writer);
            writer.close();
            BufferedReader bufferedReader = new BufferedReader(new StringReader(stringWriter.toString()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                scriptTrace.append("\n").append(line);
            }
            return scriptTrace.toString();
        } catch (IOException e1) {
            e1.printStackTrace();
            return message;
        }
    }

}
