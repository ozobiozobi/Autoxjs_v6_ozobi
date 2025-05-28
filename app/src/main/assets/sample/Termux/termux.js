function myTermux(command, options) {
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
myTermux("ls /sdcard/", {
    outputPath: "/sdcard/termux_output.txt",
    callback: (result) => {
        log("输出结果:\n" + result);
        toast(result);
    },
    runBackground: false, // 需要打开 termux 的悬浮窗、后台弹出界面权限
    sessionAction: 0, //指定 会话动作
    clean: true, // 执行完后清理输出文件
    top: true, // 不创建新的活动
    checkGap: 100, // 检查结果间隔时间，单位毫秒
    checkCount: 600, // 最大检查次数
});

// 自己构建 intent
function getTermuxIntent(command, runBackground, sessionAction, top) {
    let intent = new Intent();
    let args = stringArray("-c", command);
    intent.setClassName("com.termux", "com.termux.app.RunCommandService");
    intent.setAction("com.termux.RUN_COMMAND");
    intent.putExtra("com.termux.RUN_COMMAND_PATH", "/data/data/com.termux/files/usr/bin/bash");
    intent.putExtra("com.termux.RUN_COMMAND_ARGUMENTS", args);
    intent.putExtra("com.termux.RUN_COMMAND_WORKDIR", "/data/data/com.termux/files/home");
    intent.putExtra("com.termux.RUN_COMMAND_BACKGROUND", runBackground);
    intent.putExtra("com.termux.RUN_COMMAND_SESSION_ACTION", sessionAction);
    if (top) {
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }
    return intent;
}
let myIntent = getTermuxIntent("echo 'termux 运行成功' > " + files.cwd() + "/termuxRunResult.autox", true, 0, true);
// 发送 intent 执行 Termux 命令
sendTermuxIntent(myIntent);
