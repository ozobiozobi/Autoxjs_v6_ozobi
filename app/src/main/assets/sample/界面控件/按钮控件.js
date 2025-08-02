"ui";

ui.layout(
    <vertical gravity="center">
        <button id="rect" marginTop="10" gravity="center" w="200" h="50" textColor="#ffffff">
            方形
        </button>
        <button id="capsule" marginTop="10" gravity="center" w="200" h="50" textColor="#ffffff" gradient="corner=50">
            胶囊
        </button>
        <button id="circle" marginTop="10" gravity="center" w="88" h="88" gradient="colors=#ff66FF,#ccccFF|corner=168|ori=tl_br">
            圆形
        </button>
    </vertical>
);

ui.rect.on("click", () => {
    toast("我被点啦");
});

ui.capsule.on("long_click", () => {
    toast("我被长按啦");
});

// 通过触摸事件动态改变渐变颜色方向
ui.circle.addListener("touch", (event) => {
    if (event.getAction() == 0) {
        toast("按下");
        ui.circle.setBackgroundGradient("colors=#ff66FF,#ccccFF|corner=150|ori=br_tl");
    } else if (event.getAction() == 1) {
        toast("抬起");
        ui.circle.setBackgroundGradient("colors=#ff66FF,#ccccFF|corner=150|ori=tl_br");
    }
});
