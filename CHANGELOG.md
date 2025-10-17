# Change Log

## 6.5.8.22

修复: 脚本运行结束后资源没有回收导致的内存泄露

添加: 脚本文件卡片创建快捷方式选项(有些手机可能不起作用, 快捷方式也可以通过安卓小部件创建)

修改: 通过 runtime.loadDex 或 runtime.loadJar 加载dex或包时返回 DexClassLoader

```
let dexClassLoader = runtime.loadDex("./test.dex")
```

修复: ppocrv5 内存泄露

修复: 模拟器编辑代码 ctrl + s 会使 app 崩溃

## 6.5.8.21

提示: 因为换了签名, 所以需要先卸载之前版本

修复: 排序改变之后无法操作正确的卡片

添加: 创建项目选项, 项目文件夹打包和运行按钮

修改: 主页不再将文件和文件夹分成两个列表

添加: ppocrv5(只有 autox app 可用, 通用的还没弄好), 具体使用看示例脚本

修复: 某些情况主页搜索会使app崩溃

添加: 任务卡片长按操作

修复: 运行中的脚本路径太长导致关闭按钮被挤出屏幕

修复: 非脚本文件重命名按钮显示不完整

添加: Storage 实例方法: getAll、getAllKeys、getPref

## 6.5.8.20

修复: 名称降序排序闪退

添加: 管理页面排序

添加: 管理页面文件卡片显示上次修改时间和大小

添加: 管理页面刷新按钮

修复: 脚本在 app 关闭页面时结束运行无法移除运行记录

修改: 搜索时忽略大小写

修复: 搜索时无法操作正确的文件

修改: http

重写: 使用 Compose 重写 app 首页和管理页面

修复: 打包后无障碍服务判断问题

## 6.5.8.19

修复: 脚本退出时触发两次 onExit

调整: 抽屉页面和脚本例表控件按钮

添加: 代码编辑器编辑菜单(另存为)

修复: 某些设备 RootAutomator 滑动无效

修复: 打包后每次打开都会跳转到所有文件访问权限页面

修复: 打包前后 autojs 版本不一致

## 6.5.8.18

修复: switch、button 控件设置字体颜色不生效

添加: termux 执行参数: options( outputPath、callback、runBackground、top、sessionAction、clean、checkGap、checkCount)

添加: 全局方法 getTermuxCommandIntent、stringArray

添加: termux 示例代码

添加: app 代码编辑器悬浮窗开关

优化: termux 执行命令(zryyoung)

修复: switch 控件不显示文本

修复: 通过 app 代码编辑器悬浮窗运行时 cwd 不是脚本所在路径

高版本 bug 太多，sdk 改回 28

增强: 解决微信控件混乱问题

```
如果还是不行的话，估计是环境异常了
```

## 6.5.8.17

修复: app 前台服务无法使用

修复: 打包后权限判断问题

添加: 通知权限

添加: 打包后授予全部文件访问权限

修复: 安卓 15 存储权限问题

添加: app 编辑脚本时的控制悬浮窗(zryyoung)

## 6.5.8.16

修改(658): 悬浮窗停止脚本(与app一样)

添加: 设置 input 色调

```js
<input id="input" tint="#ff0000|#00ff00" />
ui.input.setTint("#00ff00|#ff0000")
```

添加: 设置 checkbox 色调

```js
<checkbox id="checkbox" tint="#ff0000|#00ff00" />
ui.checkbox.setTint("#00ff00|#ff0000")
```

修改: checkbox 控件为 androidx 控件

添加：设置 button 渐变背景

```js
<button id="btn" w="88" h="88" gradient="shape=oval|colors=#ff00ff,#584EF0|ori=bottom_top|type=linear">
ui.btn.setBackgroundGradient("shape=rect|corner=88");
参数：shape: rect(方形-默认)、line(线)、ring(圆环)、oval(椭圆)
     colors: 渐变颜色数组
     ori: 渐变方向 top_bottom、bottom_top、left_right、right_left、tl_br、br_tl、tr_bl、bl_tr
     type: 渐变类型 linear(线性-默认) radial(辐射) sweep(扫描)
     center: 渐变中心 0.5,0.5  默认(x:0.5, y:0.5)
     corner: 圆角 默认16
有些可能不符合预期，暂时不深入研究 *.*
```

添加: 设置 radio 色调

```js
<radio id="radio" tint="#ff0000|#00ff00" />
ui.radio.setTint("#00ff00|#ff0000")
注：未选中|选中 （只有一个颜色则一样）
```

修改：button、input、spinner、radio、text、toolbar 控件为 androidx 的控件

## 6.5.8.15

修复: 闪退或打不开 app

添加: JsSwitch 开关控件

```js
<switch id="switch"></switch>
// 以下用 xxx 代替 thumb(滑块) 或 track(轨道)
// 色调: xxxTint="#ff00ff" | xxxTint="#cfcfcf|#ff00ff"
// 大小|形状: xxxShape="168|88" | xxxShape="168|88,88,36,36"
// 注: "宽[高](dp) | (圆角半径)左上水平,左上垂直, 右上水平,右上垂直, 右下水平,右下垂直, 左下水平,左下垂直"
// 背景: xxxBg="file:///sdcard/logo.png"
------
let Switch = ui.switch;
Switch.setThumbTint("#ff00ff")// 设置滑块色调
Switch.setTrackTint("#ff00ff")// 设置轨道色调
Switch.setThumbShape("168|88")// 设置滑块大小形状
Switch.setTrackShape("168|88")// 设置轨道大小形状
Switch.setThumbBackground("file:///sdcard/logo.png")// 设置滑块背景
Switch.setTrackBackground("file:///sdcard/logo.png")// 设置轨道背景
// 如果需要设置多项, 推荐的顺序为: bg -> shape -> tint
// 若出现不符合预期效果, 那应该是冲突了
```

添加: 布局分析窗口选择开关

添加: 布局分析窗口选择(开启延迟捕获无法使用)

添加: MQTT(来自前人的智慧)

## 6.5.8.14

修复(一半): 打包后无法安装

```
偶尔可能出现无法直接安装，自己用MT管理器签名即可
```

修复: 申请截图权限失败

添加: 授予管理所有文件权限

升级: 将 targetSdk 改为 35(安卓 15)

```
有可能会出现一些未知的 bug
```

修复(658): app 打包签名报毒(升级到安卓 15 之后又变成了另一个 bug @\_@)

修复(魔改): looper 初始化之前创建 AdbIME 对象导致报错闪退

修复(658): 多选对话框无法使用

## 6.5.8.13

修改(658): 无障碍服务类名

添加: 一些编辑器提示栏符号

添加: 编辑器编辑菜单粘贴

修复(魔改): 两个内存泄露

修改: app 文档服务和 v1 本地文档改为新版 v1 文档

添加: 悬浮窗保持屏幕常亮

```js
floaty.keepScreenOn();
// 之后创建的<第一个>悬浮窗将会使屏幕保持常亮
```

添加: 设置布局分析捕获完成提示

## 6.5.8.12

(L.)添加(vscode 插件): goScoper

https://github.com/ozobiozobi/Auto.js-VSCode-Extension/releases

修复(尽力局): app 无法停止脚本

```
这应该是最后一次修这个bug了，如果还是不行的话，只能靠你们自己的代码解决了
(脚本是一个线程，只能通过 thread.interrupt() 优雅地结束)
```

修复(658): 悬浮窗点击输入无法弹出输入法

添加: App 开机自启 (需要后台弹出界面 `自启动` 权限)

添加: 打包后开机自启 (需要后台弹出界面 `自启动` 权限)

更新: v1 文档 ui 控件使用方法 (BMX)

## 6.5.8.11

添加: 时间转时间戳

```js
// dateStr: 时间字符串 (如: 2025-01-20)
// pattern: 时间字符串对应的模式 (如: yyyy-MM-dd)
let ts = dateToTimestamp(dateStr, pattern);
```

添加: v1 在线文档、社区 (由 BMX 提供)

修复(6.5.8.11): App 停止脚本后打开日志页面返回闪退

添加: 魔改充电

修复(6.5.8.10): App 无法停止脚本 (这回应该没问题了)

添加: Shizuku - 开关 (哈哈)

## 6.5.8.10

优化: 布局层次分析页面

```
修复 compose 无法触发重组
调整按钮大小和位置
将标记箭头改为方框，并在拖动时跟随
在隐显按钮和当前选中节点边界之间添加连接线
```

移除(6.5.8): 新版编辑器

修复(6.5.8): App 无法停止脚本 ( 好像可以秒停 @.@ )

添加: networkUtils

```js
networkUtils.isWifiAvailable();
networkUtils.getWifiIPv4();
networkUtils.getIPList();
```

添加: 文档服务

```
vscode, 启动!
什么, 文档404了?
没事, 还有后背隐藏能源
```

## 6.5.8.9

优化(6.5.8): 还是布局层次分析页面

```
就, 好看了一点吧 (也可能是我谦虚了
```

修复(6.5.8): 布局层次分析页面

```
显示选中不唯一
返回无法关闭页面
```

添加: 布局层次分析页面:

```
(标记/施法)按钮
    数数？为什么不用法术(@-@)
    选择第一个节点之后点击标记
    再选择第二个节点然后点击施法
    生成从第一个节点到第二个节点的路径
    例如：.parent().child(1)

显示描述和文本

显示当前选中节点的所有直系长辈 ( 大概就这个意思 -.- )

显示当前选中节点的孩子

标记当前选中节点的兄弟

给当前选中节点周围添加标记
    没有火眼金睛? 不要紧, 我来助你

切换是否可以折叠 ( 化 bug 为功能:D )

布局分析, 为所欲为 QwQ
```

## 6.5.8.8

优化: 夜间模式

优化: 布局层次分析页面:

```
修复展开后不可收起
隐藏按钮可拖动
```

修复(6.5.8.7): 布局分析相关 bug

更改(6.5.8): App 抽屉页面使用随机彩色图标

修复(6.5.8.7): App 布局分析刷新显示不全

```
一般用不到刷新, 除非画面发生变动之后捕获结果没有改变
(刷新会比等待捕获多花 2-3 倍的时间)
```

添加: App 布局分析等待捕获、延迟捕获开关

```
布局分析, 随心所欲(~.-
```

添加: 截图是否返回新的对象

```js
// 即使一直使用同一张缓存图像(屏幕没有发生变化), img1 和 img2 都不会是一个对象
// 反之如果不加参数 true, img1 === img2
let img1 = images.captureScreen(true);
let img2 = images.captureScreen(true);
```

## 6.5.8.7

添加: 获取屏幕实时宽高

```js
let curW = device.getCurWidth();
let curH = device.getCurHeight();
let size = device.getCurScreenSize();
// size.x == curW
// size.y == curH
```

添加: 获取当前屏幕方向

```js
//竖屏: 1  横屏: 2
let ori = getCurOrientation();
```

添加: 布局分析刷新开关

```
有些情况刷新会出问题(比如某音极速版啥的)，
可以关掉刷新，点开悬浮窗后，自己看情况等上一段时间再点分析
```

添加: 通过 setClip 复制的文本会发送到 vscode 的输出

```
例如: 布局分析复制控件属性/生成代码后点击复制
脚本使用 setClip
(长按手动复制不会触发)
```

优化(6.5.8): 减少 App 悬浮窗点击响应时长(慢不了一点

更改: App 抽屉页面

添加: 将 adbConnect、termux、adbIMEShellCommand、sendTermuxIntent 添加到全局

添加: viewUtils

```js
let v1 = viewUtils.findParentById(view, id);
let sp = viewUtils.pxToSp(px);
let px = viewUtils.dpToPx(dp);
let dp = viewUtils.pxToDp(px);
let px = viewUtils.spToPx(sp);
```

添加: 获取 raw 悬浮窗 contentView

```jsx
let fw = floaty.window(<frame id="content"></frame>);
let contentView = fw.getContentView();
// contentView === fw.content
```

## 6.5.8.6

优化: 启动 App 自动连接不显示 toast

升级: SDK35、gradle-8.7、AGP-8.6.0

添加: 获取状态栏高度(px)

```js
let h = getStatusBarHeight();
```

添加: 获取当前存在的本地存储 名称[路径] 数组

```js
let arr = storages.getExisting([returnPath]);
```

添加: 布局分析截图开关

## 6.5.8.5

修复(6.5.8.2): 布局分析影响脚本截图服务

添加: 跟踪堆栈行号打印

```js
// 让 bug 无处可藏 >_>
traceLog("嘿嘿"[,path(输出到文件)])
```

添加: 时间戳格式化

```js
// ts: 时间戳, 默认为当前时间戳
// format: 时间格式, 默认为 "yyyy-MM-dd HH:mm:ss.SSS"
let fm = dateFormat([ts, format]);
```

添加: 设置 http 代理(options)

```js
// 设置代理:
http.get(url, {proxyHost:"192.168.1.10", proxyPort:7890})
// 身份认证:
{userName:"ozobi", password:1014521824}
```

添加: 设置 http 尝试次数、单次尝试超时时间(options)

```js
// 一共尝试 3 次 ( 默认 3 ), 每次 5s ( 默认 10s ) 超时
http.get(url, { maxTry: 3, timeout: 5000 });
```

修改(6.5.8): 将布局层次分析页面的彩色线条数量改为与 depth 相等

优化(6.5.8.2): 布局分析不显示异常截图(宽高异常/全黑截图)

## 6.5.8.4

修复(6.5.8): 某些设备 RootAutomator 不生效

修复(6.5.8.3): 找不到方法 runtime.adbConnect(string, number)

修复(6.5.8.3): 布局分析时反复申请投影权限

添加: Adb 输入法

```js
let adbIMESC = runtime.adbIMEShellCommand;
let command = adbIMESC.inputText("嘿嘿");
// 执行命令: adb shell + command;
// 将输出文本 嘿嘿 到当前光标所在位置(需要先启用然后设置为当前输入法)

// 以下命令皆是 adbIMESC.xxx
enableAdbIME() // 启用adb输入法
setAdbIME() // 设置adb输入法为当前输入法
resetIME() // 重置输入法
clearAllText() // 清除所有文本
inputTextB64(text) // 如果inputText没用试试这个
inputKey(keyCode) // 输入按键
inputCombKey(metaKey, keyCode) // 组合键
inputCombKey(metaKey[], keyCode) // 多meta组合键

// meta 键对照:
// SHIFT == 1
// SHIFT_LEFT == 64
// SHIFT_RIGHT == 128
// CTRL == 4096
// CTRL_LEFT == 8192
// CTRL_RIGHT == 16384
// ALT == 2
// ALT_LEFT == 16
// ALT_RIGHT == 32

// 输入组合键: ctrl + shift + v:
adb shell + runtime.adbIMEShellCommand.inputCombKey([4096,1], 50);
```

增强: 调用 termux

```
安装 termux (版本需 0.95 以上)
编辑 ~/.termux/termux.properties 文件, 将 allow-external-apps=true 面的注释#去掉, 保存退出
安装 adb 工具
pkg update
pkg install android-tools
adb 连接手机后授权 autoxjs (打包后的应用也需要授权)
(如果有)手机需要开启 USB 调试 (安全设置)
adb shell pm grant 包名 com.termux.permission.RUN_COMMAND
调用: runtime.termux("adb shell input keyevent 3") 返回桌面
这里默认后台执行, 若想使用自己构建的 intent 可以使用 runtimesendTermuxIntent(intent)
```

## 6.5.8.3

添加: 远程 AdbShell

```js
// (好像不支持远程配对, 手机需要设置监听 adb 端口)
// 连接设备
let adbShell = runtime.adbConnect(host, port);
// 执行命令
adbShell.exec("ls /");
// 断开连接
adbShell.close();
// 获取当前连接主机名
adbShell.connection.getHost();
// 获取当前连接端口
adbShell.connection.getPost();
```

修改(6.5.8): 将悬浮窗位置改为以屏幕左上角为原点

```
终于可以指哪打哪了>_<
```

修复(6.5.8.2): 脚本请求截图权限后再进行布局分析时打不开悬浮窗

增强(6.5.8): 使用相对路径显示本地图片

```js
// ./ 等于 file://当前引擎的工作目录/
<img src=./pic.png />
```

## 6.5.8.2

优化(6.5.8): vscode 插件运行项目

```
vscode 打开项目新建一个 project.json 文件,
里面有 {} 就可以,
再将主脚本文件命名为 main.js 即可
```

修复(6.5.8): 老版编辑器长按删除崩溃

添加: 添加 v2 本地、在线文档

App 功能

```
添加连上为止
软件启动时会尝试连接电脑一次
打开之后会一直尝试连接电脑，直到连上为止，除非手动关闭
被动和主动断开连接电脑，都会触发一直尝试连接，除非手动关闭(可能还是bug, 某些情况会连接多次
```

App 布局分析

```
每次分析都会刷新页面节点信息，下拉状态栏可打断刷新，同时会大概率丢失面节点信息
添加延迟选项。选择其中一个选项之后会延迟相应的时间之后进行布局分析，待期间无法再次打开布局分析对话框。
添加显示上次节点信息选项。可重新分析上一次刷新的节点信息
```

App 布局范围分析

```
根据控件属性使用不同的颜色
绿色：可点击
紫色：有描述
紫红色：有文本
白色：上面三个都没有
同一控件显示颜色优先级顺序同上
如果两个控件bounds重叠，子控件的颜色会盖住父控件的
```

App 布局层次分析

```
将控件的 depth、是否可点击、是否有描述、是否有文本 显示在外面
添加展开按钮(展开当前选中的控件的全部孩子控件)
添加转到布局范围按钮
这个层次分析页面还有待改进
```

App 布局分析属性

```
将控件的常用属性（个人认为）往前排
```

代码布局分析

```
给 UiSelector.find() 添加刷新参数
例如：text('嘿嘿').find(true);
将会先刷新页面节点信息，然后再返回刷新后的寻找结果
怎么知道有用呢？可以拿某手国际版来开刀，试试刷新和不刷新的区别
```
