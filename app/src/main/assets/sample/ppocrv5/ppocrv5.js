// >>> 按顺序填好模型路径 <<<
ppocrv5.init("/sdcard/脚本/ppocrv5/det_mobile.param", "/sdcard/脚本/ppocrv5/det_mobile.bin", "/sdcard/脚本/ppocrv5/rec_mobile.param", "/sdcard/脚本/ppocrv5/rec_mobile.bin");

images.requestScreenCapture();

let img = images.captureScreen();

// 可适当对图片做些处理
img = images.grayscale(img);

// let img = images.read("/sdcard/test3.jpg");

let result = ppocrv5.ocr(img);
//let result = ppocrv5.ocr(img, x, y, width, height);// 裁剪后识别（返回的坐标为相对屏幕左上角的坐标，需要相对坐标可自行计算）

log(result);

const SIZE_320 = 0;
const SIZE_400 = 1;
const SIZE_480 = 2;
const SIZE_560 = 3;
const SIZE_640 = 4; // 默认
// ppocrv5.setTargetSize(SIZE_560); // 设置模型分辨率（与精度成正比，速度成反比）

const CPU = 0; // 默认
const GPU = 1;
const TURNIP = 2;
// ppocrv5.setCpuGpu(CPU); // 设置推理方式

// RecResult(
// ========文本内容==========
//     text=动态,
// ========可信度==========
//     prob=0.9528275,
// ========文本方向 0：水平 1：垂直==========
//     ori=0,
// ========旋转边界矩形==========
//     rRect=RotatedRect(
//         center=Point(x=896, y=2318),
//         size=Size(width=29.25, height=66.74999), angle=90.0
//     ),
// ========顶点坐标==========
//     vertices=Vertices(topLeft=Point(x=863, y=2303), topRight=Point(x=930, y=2303), bottomRight=Point(x=930, y=2332), bottomLeft=Point(x=863, y=2332)),
// ========边界矩形==========
//     rect=Rect(left=863, top=2303, right=930, bottom=2332)
// )