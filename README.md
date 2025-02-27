# Autoxjs_v6_ozobi

    基于 Autoxjs v658 魔改

#### ======== 随缘更新 ========

#### QQ 交流群: 1014521824 / 1035653475

    好东西群友先吃 >.<

---

# >>> 6588 -> 6589

---

### 优化(658): 还是布局层次分析页面

    就, 好看了一点吧(也可能是我谦虚了

### 修复(658): 布局层次分析页面:

    显示选中不唯一

    返回无法关闭页面

### 添加: 布局层次分析页面:

    施法按钮
        数数？为什么不用法术(@-@)

    显示描述和文本

    显示当前选中节点的所有直系长辈(大概就这个意思-.-)

    显示当前选中节点的孩子

    标记当前选中节点的兄弟

    给当前选中节点周围添加标记
        没有火眼金睛? 不要紧, 我来助你

    切换是否可以折叠(化bug为功能:D)

    布局分析, 为所欲为QwQ

---

# >>> 6587 -> 6588

---

### 优化: 夜间模式

### 优化: 布局层次分析页面:

    修复展开后不可收起

    隐藏按钮可拖动

### 修复(6587): 布局分析相关 bug

### 更改(658): app抽屉页面使用随机彩色图标

### 修复(6587): app布局分析刷新显示不全

    一般用不到刷新, 除非画面发生变动之后捕获结果没有改变
    (刷新会比等待捕获多花 2-3 倍的时间)

### 添加: app布局分析等待捕获、延迟捕获开关

    布局分析, 随心所欲(~.-

### 添加: 截图是否返回新的对象

    let img1 = images.captureScreen(true) 
    let img2 = images.captureScreen(true) 
    即使一直使用同一张缓存图像(屏幕没有发生变化), img1 和 img2 都不会是同一个对象 
    反之如果不加参数 true, img1 === img2 

---

# >>> 6586 -> 6587

---

### 添加: 获取屏幕实时宽高

    let curW = device.getCurWidth()
    let curH = device.getCurHeight()
    let size = device.getCurScreenSize()
    size.x == curW
    size.y == curH

### 添加: 获取当前屏幕方向

    let ori = getCurOrientation()
    竖屏: 1  横屏: 2

### 添加: 布局分析刷新开关

    有些情况刷新会出问题(比如某音极速版啥的)，可以关掉刷新，点开悬浮窗后，自己看情况等上一段时间再点分析

### 添加: 通过 setClip 复制的文本会发送到 vscode 的输出

    例如: 布局分析复制控件属性/生成代码后点击复制
    脚本使用 setClip
    (长按手动复制不会触发)

### 优化(658): 减少 app 悬浮窗点击响应时长(慢不了一点

### 更改: app 抽屉页面

### 添加: 将 adbConnect、termux、adbIMEShellCommand、sendTermuxIntent 添加到全局

### 添加: viewUtils

    let v = viewUtils.findParentById(view,id)
    let sp = viewUtils.pxToSp(px)
    let px = viewUtils.dpToPx(dp)
    let dp = viewUtils.pxToDp(px)
    let px = viewUtils.spToPx(sp)

### 添加: 获取[raw]悬浮窗 contentView

    let fw = floaty.window(<frame id="content"></frame>)
    let contentView = fw.getContentView()
    contentView === fw.content

---

# >>> 6585 -> 6586

---

### 优化: 启动 app 自动连接不显示 toast

### 升级: SDK35、gradle-8.7、AGP-8.6.0

### 添加: 获取状态栏高度(px)

    let h = getStatusBarHeight()

### 添加: 获取当前存在的本地存储 名称[路径] 数组

    let arr = storages.getExisting([returnPath])

### 添加: 布局分析截图开关

---

# >>> 6584 -> 6585

---

### 修复(6582): 布局分析影响脚本截图服务

### 添加: 跟踪堆栈行号打印

    traceLog("嘿嘿"[,path(输出到文件)])
    (让 bug 无处可藏>_>)

### 添加: 时间戳格式化

    let ts = Date.now();
    let fm = dateFormat(ts[,format]);
    format: 时间格式, 默认为 "yyyy-MM-dd HH:mm:ss.SSS"

### 添加: 设置 http 代理(options)

    设置代理: http.get(url, {proxyHost:"192.168.1.10", proxyPort:7890})
    身份认证: {userName:"ozobi", password:1014521824}

### 添加: 设置 http 尝试次数、单次尝试超时时间(options)

    比如: http.get(url, {maxTry:3, timeout: 5000})
    一共尝试 3 次(默认3), 每次 5s (默认10s)超时

### 修改(658): 将布局层次分析页面的彩色线条数量改为与 depth 相等

### 优化(6582): 布局分析不显示异常截图(宽高异常/全黑截图)

---

# >>> 6583 -> 6584

---

### 修复(658): 某些设备 RootAutomator 不生效

### 修复(6583): 找不到方法 runtime.adbConnect(string, number)

### 修复(6583): 布局分析时反复申请投影权限

### 添加: Adb 输入法

    let adbIMESC = runtime.adbIMEShellCommand;
    let command = adbIMESC.inputText("嘿嘿");
    执行命令: adb shell + command;
    将输出文本 嘿嘿 到当前光标所在位置(需要先启用然后设置为当前输入法)

    以下命令皆是 adbIMESC.xxx
    enableAdbIME() 启用adb输入法
    setAdbIME() 设置adb输入法为当前输入法
    resetIME() 重置输入法
    clearAllText() 清除所有文本
    inputTextB64(text) 如果inputText没用试试这个
    inputKey(keyCode) 输入按键
    inputCombKey(metaKey, keyCode) 组合键
    inputCombKey(metaKey[], keyCode) 多meta组合键

    meta 键对照:
    SHIFT == 1
    SHIFT_LEFT == 64
    SHIFT_RIGHT == 128
    CTRL == 4096
    CTRL_LEFT == 8192
    CTRL_RIGHT == 16384
    ALT == 2
    ALT_LEFT == 16
    ALT_RIGHT == 32

    输入组合键: ctrl + shift + v:
    adb shell + runtime.adbIMEShellCommand.inputCombKey([4096,1], 50);

### 增强: 调用 termux

    安装 termux(版本需0.95以上)
    编辑 ~/.termux/termux.properties 文件, 将 allow-external-apps=true 前面的注释#去掉, 保存退出
    安装 adb 工具
    pkg update
    pkg install android-tools
    adb连接手机后授权 autoxjs(打包后的应用也需要授权)
    (如果有)手机需要开启 USB调试(安全设置)
    adb shell pm grant 包名 com.termux.permission.RUN_COMMAND
    调用: runtime.termux("adb shell input keyevent 3") 返回桌面
    这里默认后台执行, 若想使用自己构建的 intent 可以使用 runtime.sendTermuxIntent(intent)

---

# >>> 6582 -> 6583

---

### 添加: 远程 AdbShell

    (好像不支持远程配对, 手机需要设置监听 adb 端口)
    使用 let adbShell = runtime.adbConnect(host,port) 连接设备
    使用 adbShell.exec("ls /") 执行命令
    adbShell.close() 断开连接
    adbShell.connection.getHost() 获取当前连接主机名
    adbShell.connection.getPost() 获取当前连接端口

### 修改(658): 将悬浮窗位置改为以屏幕左上角为原点

    (终于可以指哪打哪了>_<)

### 修复(6582): 脚本请求截图权限后再进行布局分析时打不开悬浮窗

### 增强(658): 使用相对路径显示本地图片

    <img src=./pic.png />
    ./ 等于 file://当前引擎的工作目录/

---

# >>> 658 -> 6582

---

### 优化?(658): vscode 插件运行项目

    vscode打开项目新建一个 project.json 文件,里面有{}就可以, 再将主脚本文件命名为 main.js 即可

### 修复(658): 老版编辑器长按删除崩溃

### 添加: 添加 v2 本地、在线文档

### app 功能

    添加连上为止
    软件启动时会尝试连接电脑一次
    打开之后会一直尝试连接电脑，直到连上为止，除非手动关闭
    被动和主动断开连接电脑，都会触发一直尝试连接，除非手动关闭(可能还是有bug, 某些情况会连接多次

### app 布局分析

    每次分析都会刷新页面节点信息，下拉状态栏可打断刷新，同时会大概率丢失页面节点信息
    添加延迟选项。选择其中一个选项之后会延迟相应的时间之后进行布局分析，等待期间无法再次打开布局分析对话框。
    添加显示上次节点信息选项。可重新分析上一次刷新的节点信息

### app 布局范围分析

    根据控件属性使用不同的颜色
    绿色：可点击
    紫色：有描述
    紫红色：有文本
    白色：上面三个都没有
    同一控件显示颜色优先级顺序同上
    如果两个控件bounds重叠，子控件的颜色会盖住父控件的

### app 布局层次分析

    将控件的 depth、是否可点击、是否有描述、是否有文本 显示在外面
    添加展开按钮(展开当前选中的控件的全部孩子控件)
    添加转到布局范围按钮
    这个层次分析页面还有待改进

### app 布局分析属性

    将控件的常用属性（个人认为）往前排

### 代码布局分析

    给 UiSelector.find() 添加刷新参数
    例如：text('嘿嘿').find(true);
    将会先刷新页面节点信息，然后再返回刷新后的寻找结果
    ？怎么知道有用呢？可以拿某手国际版来开刀，试试刷新和不刷新的区别
