package com.stardust.autojs.engine;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.stardust.autojs.script.JavaScriptSource;
import com.stardust.autojs.script.ScriptSource;

import org.mozilla.javascript.ContinuationPending;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Stardust on 2017/7/28.
 */

public class LoopBasedJavaScriptEngine extends RhinoJavaScriptEngine {

    public interface ExecuteCallback {
        void onResult(Object r);

        void onException(Exception e);
    }

    private Handler mHandler;
    private AtomicBoolean mLooping = new AtomicBoolean(false);

    public LoopBasedJavaScriptEngine(Context context) {
        super(context);
    }

    @Override
    public Object execute(final JavaScriptSource source) {
        execute(source, null);
        return null;
    }

    public void execute(final ScriptSource source, final ExecuteCallback callback) {
        Runnable r = () -> {
            try {
                Object o = LoopBasedJavaScriptEngine.super.execute((JavaScriptSource) source);
                if (callback != null)
                    callback.onResult(o);
            } catch (ContinuationPending ignored) {
            } catch (Exception e) {
                if (callback == null) {
                    throw e;
                } else {
                    callback.onException(e);
                }
            }
        };
        synchronized (this) {
            if (mHandler != null) {
                mHandler.post(r);
            }
        }
        if (!mLooping.get() && Looper.myLooper() != Looper.getMainLooper()) {
            mLooping.set(true);
            while (true) {
                try {
                    Looper.loop();
                } catch (ContinuationPending ignored) {
                    continue;
                } catch (Throwable t) {
                    mLooping.set(false);
                    throw t;
                }
                break;
            }
        }
    }

    @Override
    public void forceStop() {
        WeakReference<Activity> activityRef = (WeakReference<Activity>) getTag("activity");
        if (activityRef != null && activityRef.get() != null) {
            activityRef.get().finish();
        }
        super.forceStop();
    }

    @Override
    public synchronized void destroy() {
        release();
        super.destroy();
    }

    private void release() {
        // 清理 Handler 的回调和消息
        synchronized (this) {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
        }

        // 停止 Looper
        if (mLooping.get()) {
            Objects.requireNonNull(Looper.myLooper()).quitSafely();
            mLooping.set(false);
        }
    }

    @Override
    public void init() {
        if (Looper.myLooper() == null) Looper.prepare();
        mHandler = new Handler();
        super.init();
    }
}



//
//public class LoopBasedJavaScriptEngine extends RhinoJavaScriptEngine {
//
//    public interface ExecuteCallback {
//        void onResult(Object r);
//
//        void onException(Exception e);
//    }
//
//    private Handler mHandler;
//    private boolean mLooping = false;
//
//    public LoopBasedJavaScriptEngine(Context context) {
//        super(context);
//    }
//
//    @Override
//    public Object execute(final JavaScriptSource source) {
//        execute(source, null);
//        return null;
//    }
//
//
//    public void execute(final ScriptSource source, final ExecuteCallback callback) {
//        Runnable r = () -> {
//            try {
//                Object o = LoopBasedJavaScriptEngine.super.execute((JavaScriptSource) source);
//                if (callback != null)
//                    callback.onResult(o);
//            } catch (ContinuationPending ignored) {
//            } catch (Exception e) {
//                if (callback == null) {
//                    throw e;
//                } else {
//                    callback.onException(e);
//                }
//            }
//        };
//        mHandler.post(r);
//        if (!mLooping && Looper.myLooper() != Looper.getMainLooper()) {
//            mLooping = true;
//            while (true) {
//                try {
//                    Looper.loop();
//                } catch (ContinuationPending ignored) {
//                    continue;
//                } catch (Throwable t) {
//                    mLooping = false;
//                    throw t;
//                }
//                break;
//            }
//        }
//    }
//
//    @Override
//    public void forceStop() {
//        Activity activity = (Activity) getTag("activity");
//        if (activity != null) {
//            activity.finish();
//        }
//        super.forceStop();
//    }
//
//    @Override
//    public synchronized void destroy() {
//        release();
//        super.destroy();
//    }
//
//    private void release() {
//        // 清理 Handler 的回调和消息
//        if (mHandler != null) {
//            mHandler.removeCallbacksAndMessages(null);
//            mHandler = null;
//        }
//
//        // 停止 Looper
//        if (mLooping) {
//            Objects.requireNonNull(Looper.myLooper()).quit();
//            mLooping = false;
//        }
//    }
//
//    @Override
//    public void init() {
//        if (Looper.myLooper() == null) Looper.prepare();
//        mHandler = new Handler();
//        super.init();
//    }
//}
