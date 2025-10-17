package org.autojs.autoxjs.ui.edit;

import static org.autojs.autoxjs.ui.edit.EditorView.EXTRA_CONTENT;
import static org.autojs.autoxjs.ui.edit.EditorView.EXTRA_NAME;
import static org.autojs.autoxjs.ui.edit.EditorView.EXTRA_PATH;
import static org.autojs.autoxjs.ui.edit.EditorView.EXTRA_READ_ONLY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.stardust.app.OnActivityResultDelegate;
import com.stardust.autojs.core.permission.OnRequestPermissionsResultCallback;
import com.stardust.autojs.core.permission.PermissionRequestProxyActivity;
import com.stardust.autojs.core.permission.RequestPermissionCallbacks;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.pio.PFiles;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.autojs.autoxjs.R;
import org.autojs.autoxjs.model.script.ScriptFile;
import org.autojs.autoxjs.model.script.Scripts;
import org.autojs.autoxjs.storage.file.TmpScriptFiles;
import org.autojs.autoxjs.theme.dialog.ThemeColorMaterialDialogBuilder;
import org.autojs.autoxjs.tool.Observers;
import org.autojs.autoxjs.ui.BaseActivity;
import org.autojs.autoxjs.ui.main.MainActivity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/1/29.
 */
@EActivity(R.layout.activity_edit)
public class EditActivity extends BaseActivity implements OnActivityResultDelegate.DelegateHost, PermissionRequestProxyActivity {

    private OnActivityResultDelegate.Mediator mMediator = new OnActivityResultDelegate.Mediator();
    private static final String LOG_TAG = "EditActivity";
    private ScriptExecution floatyExecution = null;
    public static boolean showEditFloaty = true;
    @ViewById(R.id.editor_view)
    EditorView mEditorView;

    private EditorMenu mEditorMenu;
    private RequestPermissionCallbacks mRequestPermissionCallbacks = new RequestPermissionCallbacks();
    private boolean mNewTask;

    public static void editFile(Context context, String path, boolean newTask) {
        editFile(context, null, path, newTask);
    }

    public static void editFile(Context context, Uri uri, boolean newTask) {
        context.startActivity(newIntent(context, newTask)
                .setData(uri));
    }

    public static void editFile(Context context, String name, String path, boolean newTask) {
        context.startActivity(newIntent(context, newTask)
                .putExtra(EXTRA_PATH, path)
                .putExtra(EXTRA_NAME, name));
    }

    public static void viewContent(Context context, String name, String content, boolean newTask) {
        context.startActivity(newIntent(context, newTask)
                .putExtra(EXTRA_CONTENT, content)
                .putExtra(EXTRA_NAME, name)
                .putExtra(EXTRA_READ_ONLY, true));
    }

    private static Intent newIntent(Context context, boolean newTask) {
        Intent intent = new Intent(context, EditActivity_.class);
        if (newTask || !(context instanceof Activity)) {
            // 添加 FLAG_ACTIVITY_CLEAR_TASK 标志
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewTask = (getIntent().getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0;
        showEditFloaty = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(this.getString(R.string.ozobi_key_show_edit_floaty), false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_S){
            onCtrlSPressed();
            return true;
        }else if(event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_Y){
            onCtrlYPressed();
            return true;
        }else if(event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_Z){
            onCtrlZPressed();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    private void onCtrlZPressed(){
        mEditorView.undo();
    }

    private void onCtrlYPressed(){
        mEditorView.redo();
    }

    private void onCtrlSPressed(){
        mEditorView.saveFile();
    }

    @SuppressLint("CheckResult")
    @AfterViews
    void setUpViews() {
        mEditorView.handleIntent(getIntent())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Observers.emptyConsumer(),
                        ex -> onLoadFileError(ex.getMessage()));
        mEditorMenu = new EditorMenu(mEditorView);
        setUpToolbar();
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return super.onWindowStartingActionMode(callback);
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
        return super.onWindowStartingActionMode(callback, type);
    }

    private void onLoadFileError(String message) {
        new ThemeColorMaterialDialogBuilder(this)
                .title(getString(R.string.text_cannot_read_file))
                .content(message)
                .positiveText(R.string.text_exit)
                .cancelable(false)
                .onPositive((dialog, which) -> finish())
                .show();
    }

    private void setUpToolbar() {
        TextView filePath = findViewById(R.id.file_path);
        String scriptFloatyFilePath = mEditorView.getUri().getPath();
        filePath.setText(scriptFloatyFilePath);
        if(showEditFloaty){
            String cwd = scriptFloatyFilePath.substring(0, scriptFloatyFilePath.lastIndexOf("/"));
            String scriptString = "let scriptFw = floaty.window( <horizontal id=\"root\" padding=\"0\"><frame id=\"toggle\" padding=\"5\" bg=\"#80000000\" gravity=\"center\"><img id=\"collapsedIcon\" src=\"@drawable/ic_keyboard_arrow_up_black_48dp\" tint=\"#ffffff\" w=\"24dp\" h=\"24dp\" /><img id=\"expandedIcon\" src=\"@drawable/ic_keyboard_arrow_down_black_48dp\" tint=\"#ffffff\" w=\"24dp\" h=\"24dp\" /> </frame> <vertical id=\"expanded\" padding=\"5\" bg=\"#80000000\"> <horizontal gravity=\"left\" margin=\"0\" padding=\"0\"> <text id=\"scriptName\" text=\"TTS.js\" textColor=\"#ffffff\" textSize=\"10sp\" /> </horizontal> <horizontal gravity=\"center\" margin=\"0\"> <img id=\"start\" src=\"@drawable/ic_play_arrow_black_48dp\" tint=\"#ffffff\" w=\"24dp\" h=\"24dp\" marginLeft=\"8\" /> <img id=\"stop\" src=\"@drawable/ic_stop_black_48dp\" tint=\"#ffffff\" w=\"24dp\" h=\"24dp\" marginLeft=\"8\" /> <img id=\"log\" src=\"@drawable/ic_assignment_black_48dp\" tint=\"#ffffff\" w=\"22dp\" h=\"22dp\" marginLeft=\"8\" /> <img id=\"exit\" src=\"@android:drawable/ic_menu_close_clear_cancel\" tint=\"#ffffff\" w=\"24dp\" h=\"24dp\" marginLeft=\"8\" /> </horizontal> </vertical> </horizontal>);let isExpanded = true;let isMoving = false;let x, y;let downX, downY;let windowWidth, isStickToRight, toggleWidth;let checkMoveInterval;let scriptFilePath = files.path(\""+ scriptFloatyFilePath +"\");scriptFw.setPosition(0, device.getCurHeight() / 3);setTimeout(() => {ui.run(() => {scriptFw.collapsedIcon.visibility = 8;scriptFw.expandedIcon.visibility = 0;scriptFw.expanded.visibility = 0;scriptFw.setSize(-2, -2);scriptFw.scriptName.setText(scriptFilePath.slice(scriptFilePath.lastIndexOf(\"/\") + 1))})}, 200);setTimeout(() => {ui.run(() => {let layoutParams = scriptFw.toggle.getLayoutParams();layoutParams.height = scriptFw.getContentView().getHeight();scriptFw.toggle.setLayoutParams(layoutParams)});windowWidth = scriptFw.getContentView().getWidth()}, 600);scriptFw.toggle.click(() => {});scriptFw.toggle.addListener(\"touch\", (e) => {if (e.action == 2) {if (isMoving) {scriptFw.setPosition(x + (e.getRawX() - downX), y + (e.getRawY() - downY))}} else if (e.action == 1) {if (isMoving) {isMoving = false} else {if (isExpanded) {scriptFw.collapsedIcon.visibility = 0;scriptFw.expandedIcon.visibility = 8;scriptFw.expanded.visibility = 8;scriptFw.setSize(-2, -2);if (toggleWidth == undefined) {setTimeout(() => {toggleWidth = scriptFw.getContentView().getWidth();moveToSide()}, 500)} else {moveToSide()}} else {scriptFw.collapsedIcon.visibility = 8;scriptFw.expandedIcon.visibility = 0;scriptFw.expanded.visibility = 0;scriptFw.setSize(-2, -2);if (isStickToRight) {scriptFw.setPosition(device.getCurWidth() - windowWidth, scriptFw.getY())}};isExpanded = !isExpanded};if (checkMoveInterval) {clearInterval(checkMoveInterval);checkMoveInterval = null};if (!isExpanded && toggleWidth) {moveToSide()}} else if (e.action == 0) {x = scriptFw.getX();y = scriptFw.getY();downX = e.getRawX();downY = e.getRawY();checkMoveInterval = setInterval(() => {if (Math.abs(e.getRawX() - downX) > 20 || Math.abs(e.getRawY() - downY) > 20) {isMoving = true;clearInterval(checkMoveInterval);checkMoveInterval = null}}, 50)}});scriptFw.root.setOnTouchListener(function (view, event) {switch (event.getAction()) {case event.ACTION_DOWN:;isMoving = false;x = scriptFw.getX();y = scriptFw.getY();downX = event.getRawX();downY = event.getRawY();return true;case event.ACTION_MOVE:;if (Math.abs(event.getRawX() - downX) > 20 || Math.abs(event.getRawY() - downY) > 20) {isMoving = true};if (isMoving) {scriptFw.setPosition(x + (event.getRawX() - downX), y + (event.getRawY() - downY))};return true;case event.ACTION_UP:;isMoving = false;return false};return false});function moveToSide() {if (scriptFw.getX() < device.getCurWidth() / 2) {isStickToRight = false;scriptFw.setPosition(0, scriptFw.getY())} else {isStickToRight = true;scriptFw.setPosition(device.getCurWidth() - toggleWidth, scriptFw.getY())}};function 停止当前脚本() {engines.all().forEach((ScriptEngine) => {if (engines.myEngine().toString() == ScriptEngine.toString()) {ScriptEngine.forceStop();log(\"停止脚本引擎: \" + engines.myEngine().source)}})};scriptFw.start.click(function () {toast(\"启动脚本\");engines.execScriptFile(scriptFilePath,{path:\""+cwd+"\"})});scriptFw.stop.click(function () {toast(\"停止脚本\");engines.all().forEach((ScriptEngine) => {if (engines.myEngine().toString() != ScriptEngine.toString()) {ScriptEngine.forceStop();log(\"停止脚本引擎: \" + engines.myEngine().source)}})});scriptFw.log.click(() => {toast(\"日志\");app.startActivity(\"console\")});scriptFw.exit.click(() => {toast(\"退出悬浮窗\");scriptFw.close();停止当前脚本()});events.on(\"exit\", () => {console.log(\"脚本已退出\");scriptFw.close()});setInterval(() => {}, 3000);";
            File ScriptFile = new File(this.getFilesDir(),"__scriptControlFloaty.js");
            try{
                // 将字符串写入文件
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Files.write(Paths.get(ScriptFile.getPath()), scriptString.getBytes(StandardCharsets.UTF_8));
                    floatyExecution = Scripts.INSTANCE.run(new ScriptFile(ScriptFile));
                    Toast.makeText(this,"悬浮窗运行不会保存",Toast.LENGTH_SHORT).show();
                }
            }catch(Exception e){
                Log.d("ozobiLog","e: "+e);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mEditorMenu.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onPrepareOptionsMenu: " + menu);
        boolean isScriptRunning = mEditorView.getScriptExecutionId() != ScriptExecution.NO_ID;
        try{
            MenuItem forceStopItem = menu.findItem(R.id.action_force_stop);
            forceStopItem.setEnabled(isScriptRunning);
        }catch (Exception e){
            Log.d("ozobiLog",e.toString());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        Log.d(LOG_TAG, "onActionModeStarted: " + mode);
        Menu menu = mode.getMenu();
        MenuItem item = menu.getItem(menu.size() - 1);
        // 以下两项在64位版存在失效bug，暂时注释掉，相关功能可通过编辑菜单使用
//        menu.add(item.getGroupId(), R.id.action_delete_line, 10000, R.string.text_delete_line);
//        menu.add(item.getGroupId(), R.id.action_copy_line, 20000, R.string.text_copy_line);
        super.onActionModeStarted(mode);
    }

    @Override
    public void onSupportActionModeStarted(@NonNull androidx.appcompat.view.ActionMode mode) {
        Log.d(LOG_TAG, "onSupportActionModeStarted: mode = " + mode);
        super.onSupportActionModeStarted(mode);
    }

    @Nullable
    @Override
    public androidx.appcompat.view.ActionMode onWindowStartingSupportActionMode(@NonNull androidx.appcompat.view.ActionMode.Callback callback) {
        Log.d(LOG_TAG, "onWindowStartingSupportActionMode: callback = " + callback);
        return super.onWindowStartingSupportActionMode(callback);
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        Log.d(LOG_TAG, "startActionMode: callback = " + callback + ", type = " + type);
        return super.startActionMode(callback, type);
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        Log.d(LOG_TAG, "startActionMode: callback = " + callback);
        return super.startActionMode(callback);
    }

    @Override
    public void onBackPressed() {
        if (!mEditorView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        if (mEditorView.isTextChanged()) {
            showExitConfirmDialog();
            return;
        }
        finishAndRemoveFromRecents();
    }

    private void finishAndRemoveFromRecents() {
        finishAndRemoveTask();
        if (mNewTask) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void showExitConfirmDialog() {
        new ThemeColorMaterialDialogBuilder(this)
                .title(R.string.text_alert)
                .content(R.string.edit_exit_without_save_warn)
                .positiveText(R.string.text_cancel)
                .negativeText(R.string.text_save_and_exit)
                .neutralText(R.string.text_exit_directly)
                .onNegative((dialog, which) -> {
                    mEditorView.saveFile();
                    finishAndRemoveFromRecents();
                })
                .onNeutral((dialog, which) -> finishAndRemoveFromRecents())
                .show();
    }

    @Override
    protected void onDestroy() {
        mEditorView.destroy();
        super.onDestroy();
        try {
            floatyExecution.getEngine().forceStop();
        } catch (Exception e) {
            Log.d("ozobiLog", "e: " + e);
        }
    }

    @NonNull
    @Override
    public OnActivityResultDelegate.Mediator getOnActivityResultDelegateMediator() {
        return mMediator;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mMediator.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!mEditorView.isTextChanged()) {
            return;
        }
        String text = mEditorView.getEditor().getText();
        if (text.length() < 256 * 1024) {
            outState.putString("text", text);
        } else {
            File tmp = saveToTmpFile(text);
            if (tmp != null) {
                outState.putString("path", tmp.getPath());
            }

        }
    }

    private File saveToTmpFile(String text) {
        try {
            File tmp = TmpScriptFiles.create(this);
            Observable.just(text)
                    .observeOn(Schedulers.io())
                    .subscribe(t -> PFiles.write(tmp, t));
            return tmp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String text = savedInstanceState.getString("text");
        if (text != null) {
            mEditorView.setRestoredText(text);
            return;
        }
        String path = savedInstanceState.getString("path");
        if (path != null) {
            Observable.just(path)
                    .observeOn(Schedulers.io())
                    .map(PFiles::read)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(t -> mEditorView.getEditor().setText(t), Throwable::printStackTrace);
        }
    }

    @Override
    public void addRequestPermissionsCallback(OnRequestPermissionsResultCallback callback) {
        mRequestPermissionCallbacks.addCallback(callback);
    }

    @Override
    public boolean removeRequestPermissionsCallback(OnRequestPermissionsResultCallback callback) {
        return mRequestPermissionCallbacks.removeCallback(callback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRequestPermissionCallbacks.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
