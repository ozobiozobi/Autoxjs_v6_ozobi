package org.autojs.autoxjs.autojs;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import com.stardust.app.GlobalAppContext;
import com.stardust.autojs.core.console.GlobalConsole;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.autojs.runtime.accessibility.AccessibilityConfig;
import com.stardust.autojs.runtime.api.AppUtils;
import com.stardust.autojs.runtime.exception.ScriptException;
import com.stardust.autojs.runtime.exception.ScriptInterruptedException;
import com.stardust.view.accessibility.AccessibilityService;
import com.stardust.view.accessibility.LayoutInspector;
import com.stardust.view.accessibility.NodeInfo;
// Annotated by ibozo - 2024/10/31 >
//import org.autojs.autoxjs.BuildConfig;// <
import org.autojs.autoxjs.Pref;
import org.autojs.autoxjs.R;
import org.autojs.autoxjs.devplugin.DevPlugin;
import org.autojs.autoxjs.external.fileprovider.AppFileProvider;
import org.autojs.autoxjs.tool.AccessibilityServiceTool;
import org.autojs.autoxjs.ui.floating.FloatyWindowManger;
import org.autojs.autoxjs.ui.floating.FullScreenFloatyWindow;
import org.autojs.autoxjs.ui.floating.layoutinspector.LayoutBoundsFloatyWindow;
import org.autojs.autoxjs.ui.floating.layoutinspector.LayoutHierarchyFloatyWindow;
import org.autojs.autoxjs.ui.log.LogActivityKt;
import org.autojs.autoxjs.ui.settings.SettingsActivity;


/**
 * Created by Stardust on 2017/4/2.
 */

public class AutoJs extends com.stardust.autojs.AutoJs {

    private static AutoJs instance;

    public static AutoJs getInstance() {
        return instance;
    }

    private boolean enableDebugLog = false;

    public synchronized static void initInstance(Application application) {
        if (instance != null) {
            return;
        }
        instance = new AutoJs(application);
        
        
        PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext())
                .edit()
                .putBoolean(application.getApplicationContext().getString(R.string.key_cur_check_connection_status), false)
                .apply();
        // <
    }

    private interface LayoutInspectFloatyWindow {
        FullScreenFloatyWindow create(NodeInfo nodeInfo);
    }

    private AutoJs(final Application application) {
        super(application);
        getScriptEngineService().registerGlobalScriptExecutionListener(new ScriptExecutionGlobalListener());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LayoutBoundsFloatyWindow.class.getName());
        intentFilter.addAction(LayoutHierarchyFloatyWindow.class.getName());
        BroadcastReceiver mLayoutInspectBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    ensureAccessibilityServiceEnabled();
                    String action = intent.getAction();
                    if (LayoutBoundsFloatyWindow.class.getName().equals(action)) {
                        capture(LayoutBoundsFloatyWindow::new);
                    } else if (LayoutHierarchyFloatyWindow.class.getName().equals(action)) {
                        capture(LayoutHierarchyFloatyWindow::new);
                    }
                } catch (Exception e) {
                    if (Looper.myLooper() != Looper.getMainLooper()) {
                        throw e;
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(application).registerReceiver(mLayoutInspectBroadcastReceiver, intentFilter);
    }

    private void capture(LayoutInspectFloatyWindow window) {
        LayoutInspector inspector = getLayoutInspector();
        LayoutInspector.CaptureAvailableListener listener = new LayoutInspector.CaptureAvailableListener() {
            @Override
            public void onCaptureAvailable(NodeInfo capture) {
                inspector.removeCaptureAvailableListener(this);
                getUiHandler().post(() ->
                        FloatyWindowManger.addWindow(getApplication().getApplicationContext(), window.create(capture))
                );
            }
        };
        inspector.addCaptureAvailableListener(listener);
        // Modefied by ibozo - 2024/10/31
        if (inspector.captureCurrentWindow()) {
            inspector.removeCaptureAvailableListener(listener);
        }
    }

    @NonNull
    @Override
    protected AppUtils createAppUtils(Context context) {
        return new AppUtils(context, AppFileProvider.AUTHORITY);
    }

    @NonNull
    @Override
    protected GlobalConsole createGlobalConsole() {
        return new GlobalConsole(getUiHandler()) {
            @Override
            public String println(int level, CharSequence charSequence) {
                String log = super.println(level, charSequence);
                DevPlugin.INSTANCE.log(log);
                return log;
            }
        };
    }

    public void ensureAccessibilityServiceEnabled() {
        if (AccessibilityService.Companion.getInstance() != null) {
            return;
        }
        String errorMessage = null;
        if (AccessibilityServiceTool.isAccessibilityServiceEnabled(GlobalAppContext.get())) {
            errorMessage = GlobalAppContext.getString(R.string.text_auto_operate_service_enabled_but_not_running);
        } else {
            if (Pref.shouldEnableAccessibilityServiceByRoot()) {
                if (!AccessibilityServiceTool.enableAccessibilityServiceByRootAndWaitFor(2000)) {
                    errorMessage = GlobalAppContext.getString(R.string.text_enable_accessibility_service_by_root_timeout);
                }
            } else {
                errorMessage = GlobalAppContext.getString(R.string.text_no_accessibility_permission);
            }
        }
        if (errorMessage != null) {
            AccessibilityServiceTool.goToAccessibilitySetting();
            throw new ScriptException(errorMessage);
        }
    }

    @Override
    public void waitForAccessibilityServiceEnabled() {
        if (AccessibilityService.Companion.getInstance() != null) {
            return;
        }
        String errorMessage = null;
        if (AccessibilityServiceTool.isAccessibilityServiceEnabled(GlobalAppContext.get())) {
            errorMessage = GlobalAppContext.getString(R.string.text_auto_operate_service_enabled_but_not_running);
        } else {
            if (Pref.shouldEnableAccessibilityServiceByRoot()) {
                if (!AccessibilityServiceTool.enableAccessibilityServiceByRootAndWaitFor(2000)) {
                    errorMessage = GlobalAppContext.getString(R.string.text_enable_accessibility_service_by_root_timeout);
                }
            } else {
                errorMessage = GlobalAppContext.getString(R.string.text_no_accessibility_permission);
            }
        }
        if (errorMessage != null) {
            AccessibilityServiceTool.goToAccessibilitySetting();
            if (!AccessibilityService.Companion.waitForEnabled(-1)) {
                throw new ScriptInterruptedException();
            }
        }
    }

    @Override
    protected AccessibilityConfig createAccessibilityConfig() {
        AccessibilityConfig config = super.createAccessibilityConfig();
        
//        if (BuildConfig.CHANNEL.equals("coolapk")) {
//            assert config != null;
//            config.addWhiteList("com.coolapk.market");
//        }
        return config;
    }

    @NonNull
    @Override
    protected ScriptRuntime createRuntime() {
        ScriptRuntime runtime = super.createRuntime();
        runtime.putProperty("class.settings", SettingsActivity.class);
        runtime.putProperty("class.console", LogActivityKt.class);
        runtime.putProperty("broadcast.inspect_layout_bounds", LayoutBoundsFloatyWindow.class.getName());
        runtime.putProperty("broadcast.inspect_layout_hierarchy", LayoutHierarchyFloatyWindow.class.getName());
        return runtime;
    }

    public void debugInfo(String content) {
        if (this.enableDebugLog) {
            AutoJs.getInstance().getGlobalConsole().println(Log.VERBOSE, content);
        }
    }

    public void setDebugEnabled(boolean enableDebugLog) {
        this.enableDebugLog = enableDebugLog;
    }
}
