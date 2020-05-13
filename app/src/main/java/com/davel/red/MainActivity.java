package com.davel.red;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private WindowManager windowManager = null;
    private WindowManager.LayoutParams windowManagerParams = null;
    private FloatView floatView = null;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        createView();
    }

    @SuppressLint("ResourceAsColor")
    private void createView() {
//        floatView = new FloatView(getApplicationContext());
//        floatView.setOnClickListener(this);
//        floatView.setImageResource(R.drawable.ic_launcher_background); // 这里简单的用自带的icon来做演示

        // 获取WindowManager
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        // 设置LayoutParams(全局变量）相关参数
        windowManagerParams = ((FloatApplication) getApplication()).getWindowParams();
        // 设置window type
        windowManagerParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        // 设置图片格式，效果为背景透明 PixelFormat.RGBA_8888
        windowManagerParams.format = PixelFormat.RGBA_8888;
        // 设置Window flag
        windowManagerParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        /*
         * 注意，flag的值可以为：
         * LayoutParams.FLAG_NOT_TOUCH_MODAL 不影响后面的事件
         * LayoutParams.FLAG_NOT_FOCUSABLE 不可聚焦
         * LayoutParams.FLAG_NOT_TOUCHABLE 不可触摸
         */

        // 调整悬浮窗口至左上角，便于调整坐标
        windowManagerParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        // 以屏幕左上角为原点，设置x、y初始值
        windowManagerParams.x = 10;
        windowManagerParams.y = 10;
        // 设置悬浮窗口长宽数据
        windowManagerParams.width = 100;
        windowManagerParams.height = 300;
        // 显示myFloatView图像
        //windowManager.addView(floatView, windowManagerParams);

        tv = new TextView(getApplicationContext());
        tv.setBackgroundColor(R.color.coloeBlack);
        tv.getBackground().setAlpha(200);
        tv.append("aaaa");
        tv.setOnClickListener(this);
        windowManager.addView(tv,windowManagerParams);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void onDestroy() {
        super.onDestroy();
        // 在程序退出(Activity销毁）时销毁悬浮窗口
        windowManager.removeView(floatView);
    }

    public void onClick(View v) {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
    }
}
