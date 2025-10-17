
module.exports = function (runtime, global) {
    importClass("java.text.SimpleDateFormat");
    // Added by ozobi - 2025/02/01 > 添加: 跟踪打印
    global.traceLog = function(_msg, _logToFilePath){
        let err = new Error();
        let lines = err.stack.split("\n");
        lines.shift();
        lines.pop();
        lines.reverse();
        let callInfo = "<time>: " + global.dateFormat(Date.now(),"yyyy-MM-dd HH:mm:ss.SSS");
        for (let index = 0; index < lines.length; index++) {
            let info = lines[index].replace("\tat ", "");
            let lineNum = info.slice(info.lastIndexOf(":") + 1, info.length);
            let msg = `\n\t<line>: ${lineNum}`;
            callInfo += msg;
        }
        callInfo += `\n<msg>: ${_msg}\n`;
        if (_logToFilePath) {
            try {
                _logToFilePath = runtime.files.path(_logToFilePath);
                runtime.files.createWithDirs(_logToFilePath);
                if (files.isFile(_logToFilePath)) {
                    runtime.files.append(_logToFilePath, callInfo + "\n");
                    log("log to file: " + _logToFilePath);
                } else {
                    log("+++ failed to create file: " + _logToFilePath);
                }
            } catch (e) {
                log("try to log to file failed:" + _logToFilePath);
                log(e);
            }
        }
        log(callInfo);
        return callInfo;
    }
    // <
    // Added by ozobi - 2025/02/01 > 时间戳转换
    global.dateFormat = function(timestamp, format){
        let fm = format === undefined ? "yyyy-MM-dd HH:mm:ss.SSS" : format;
        let date = timestamp === undefined ? new Date() : new Date(timestamp); // 创建Date对象
        let sdf = new SimpleDateFormat(fm); // 定义格式
        let formattedDate = sdf.format(date); // 格式化时间
        return formattedDate;
    }
    // <
    global.dateToTimestamp = function(dateTimeString, pattern){
        return com.stardust.util.Ozobi.dateTimeToTimestamp(dateTimeString, pattern)
    }
    // Added by ozobi - 2025/02/06 > 获取状态栏高度
    global.getStatusBarHeight = runtime.getStatusBarHeight;
    // <
    // Added by ozobi - 2025/02/14 > 添加: viewUtils
    global.viewUtils = {
        findParentById:function(view,id){
            return com.stardust.util.ViewUtils.findParentById(view, id)
        },
        pxToSp:function(_px) {
            let px = _px * 1.0;
            return com.stardust.util.ViewUtils.pxToSp(context, px);
        },
        dpToPx:function(_dp) {
            let dp = Math.floor(_dp);
            return com.stardust.util.ViewUtils.dpToPx(context, dp);
        },
        pxToDp:function(_px) {
            let px = Math.floor(_px);
            return com.stardust.util.ViewUtils.pxToDp(context, px);
        },
        spToPx:function(_sp) {
            let sp = _sp * 1.0;
            return com.stardust.util.ViewUtils.spToPx(context, sp);
        }
    };

    global.ppocrv5 = runtime.ppocrv5;

    // <
    // Added by ozobi - 2025/02/14 > 将 adbConnect、termux、adbIMEShellCommand、sendTermuxIntent 添加到全局
    global.adbConnect = runtime.adbConnect;
    global.stringArray = runtime.stringArray;
    global.termux = function(command, options){
        try {
            let outputPath = options.outputPath === undefined ? files.cwd() + "/" + Date.now() + "termux_output" : options.outputPath;
            let callback = options.callback;
            let runBackground = options.runBackground === undefined ? true : options.runBackground;
            let sessionAction = options.sessionAction === undefined ? 0 : options.sessionAction;
            let top = options.top === undefined ? true : options.top;
            let clean = options.clean === undefined ? true : options.clean;
            files.createWithDirs(outputPath);
            files.write(outputPath, "");
            // 包裹命令，加输出重定向
            if (outputPath) {
                command += " > " + outputPath + " 2>&1";
                if (callback) {
                    command += ";echo '##termuxDoneExec##' >> " + outputPath;
                }
            }
            runtime.termux(command, runBackground, sessionAction, top);
            console.log("termux 执行命令: ", command);
            if (!files.exists(outputPath)) {
                log("+++输出文件不存在, 无法获取返回结果");
                return;
            }
            let checkGap = options.checkGap === undefined ? 100 : options.checkGap;
            let checkCount = options.checkCount === undefined ? 600 : options.checkCount;
            let total = (checkGap * checkCount) / 1000;
            let checkInterval = setInterval(() => {
                let result, isDone;
                if (files.exists(outputPath)) {
                    result = files.read(outputPath);
                    let index = result.indexOf("##termuxDoneExec##");
                    if (index != -1) {
                        isDone = true;
                        result = result.slice(0, index);
                        if (!clean) {
                            files.write(outputPath, result);
                        }
                    }
                }
                if (isDone) {
                    if (callback) {
                        callback(result ? result.trim() : "执行失败, Termux未运行或命令出错");
                    }
                    if (clean) {
                        files.remove(outputPath); // 清理文件
                    }
                    clearInterval(checkInterval);
                    checkInterval = null;
                    return;
                } else if (checkCount-- <= 0) {
                    clearInterval(checkInterval);
                    checkInterval = null;
                    throw `timeout(${total}s) - 执行超时`;
                }
            }, checkGap); // 可按命令执行速度调节时间
        } catch (e) {
            toast("执行失败: " + e);
            log("termuxRun 错误: " + e);
        }
    }
    global.adbIMEShellCommand = runtime.adbIMEShellCommand;
    global.sendTermuxIntent = runtime.sendTermuxIntent;
    global.getTermuxCommandIntent = runtime.getTermuxCommandIntent;
    // <
    // Added by ozobi - 2025/02/16 > 添加: 获取当前屏幕方向
    global.getCurOrientation = function(){
         return context.getResources().getConfiguration().orientation
    }
    // Added by ozobi - 2025/03/04 > 添加: networkUtils
    global.networkUtils = function(){ }
    global.networkUtils.isWifiAvailable = function(){
        return com.stardust.util.NetworkUtils.isWifiAvailable(context)
    }
    global.networkUtils.getWifiIPv4 = function(){
        return com.stardust.util.NetworkUtils.getWifiIPv4(context)
    }
    global.networkUtils.getIPList = function(){
        return com.stardust.util.NetworkUtils.getIPList(context)
    }
    // <


    global.toast = function (text) {
        runtime.toast(text);
    }

    global.toastLog = function (text) {
        runtime.toast(text);
        global.log(text);
    }

    global.sleep = function (t) {
        if (ui.isUiThread()) {
            throw new Error("不能在ui线程执行阻塞操作，请使用setTimeout代替");
        }
        runtime.sleep(t);
    }

    global.isStopped = function () {
        return runtime.isStopped();
    }

    global.isShuttingDown = global.isShopped;

    global.notStopped = function () {
        return !isStopped();
    }

    global.isRunning = global.notStopped;

    global.exit = runtime.exit.bind(runtime);

    global.stop = global.exit;

    global.setClip = function (text) {
        runtime.setClip(text);
    }

    global.getClip = function (text) {
        return runtime.getClip();
    }

    global.currentPackage = function () {
        global.auto();
        return runtime.info.getLatestPackage();
    }

    global.currentActivity = function () {
        global.auto();
        return runtime.info.getLatestActivity();
    }

    global.waitForActivity = function (activity, period) {
        ensureNonUiThread();
        period = period || 200;
        while (global.currentActivity() != activity) {
            sleep(period);
        }
    }

    global.waitForPackage = function (packageName, period) {
        ensureNonUiThread();
        period = period || 200;
        while (global.currentPackage() != packageName) {
            sleep(period);
        }
    }

    function ensureNonUiThread() {
        if (ui.isUiThread()) {
            throw new Error("不能在ui线程执行阻塞操作，请在子线程或子脚本执行，或者使用setInterval循环检测当前activity和package");
        }
    }

    global.random = function (min, max) {
        if (arguments.length == 0) {
            return Math.random();
        }
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    global.setScreenMetrics = runtime.setScreenMetrics.bind(runtime);

    global.requiresApi = runtime.requiresApi.bind(runtime);
    global.requiresAutojsVersion = function (version) {
        if (typeof (version) == 'number') {
            if (compare(version, app.autojs.versionCode) > 0) {
                throw new Error("需要Auto.js版本号" + version + "以上才能运行");
            }
        } else {
            if (compareVersion(version, app.autojs.versionName) > 0) {
                throw new Error("需要Auto.js版本" + version + "以上才能运行");
            }
        }
    }

    var buildTypes = {
        release: 100,
        beta: 50,
        alpha: 0
    }

    function compareVersion(v1, v2) {
        v1 = parseVersion(v1);
        v2 = parseVersion(v2);
        log(v1, v2);
        return v1.major != v2.major ? compare(v1.major, v2.major) :
            v1.minor != v2.minor ? compare(v1.minor, v2.minor) :
                v1.revision != v2.revision ? compare(v1.revision, v2.revision) :
                    v1.buildType != v2.buildType ? compare(v1.buildType, v2.buildType) :
                        compare(v1.build, v2.build);
    }

    function compare(a, b) {
        return a > b ? 1 :
            a < b ? -1 :
                0;
    }

    function parseVersion(v) {
        var m = /(\d+)\.(\d+)\.(\d+)[ ]?(Alpha|Beta)?(\d*)/.exec(v);
        if (!m) {
            throw new Error("版本格式不合法: " + v);
        }
        return {
            major: parseInt(m[1]),
            minor: parseInt(m[2]),
            revision: parseInt(m[3]),
            buildType: buildType(m[4]),
            build: m[5] ? parseInt(m[5]) : 1
        };
    }

    function buildType(str) {
        if (str == 'Alpha') {
            return buildTypes.alpha;
        }
        if (str == 'Beta') {
            return buildTypes.beta;
        }
        return buildTypes.release;
    }
    global.Buffer = require("buffer").Buffer;
    const getBytes = com.stardust.autojs.util.ArrayBufferUtil.getBytes;
    const fromBytes = com.stardust.autojs.util.ArrayBufferUtil.fromBytes;
    global.Buffer.prototype.getBytes = function () {
        return getBytes(this.buffer)
    };
    global.Buffer.fromBytes = function (byteArr) {
        const arrBuffer = new ArrayBuffer(byteArr.length);
        fromBytes(byteArr, arrBuffer);
        return global.Buffer.from(arrBuffer)
    }

    global.zips = Object.create(runtime.zips);
    global.gmlkit = Object.create(runtime.gmlkit);
    // global.paddle = Object.create(runtime.paddle);
}