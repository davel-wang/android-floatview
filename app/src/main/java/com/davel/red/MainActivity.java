package com.davel.red;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        createView();
        getService();
    }


    private WindowManager windowManager = null;
    private WindowManager.LayoutParams windowManagerParams = null;
    //悬浮框文本
    public TextView textView;

    @SuppressLint("ResourceAsColor")
    private void createView() {
        // 获取WindowManager
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        // 设置LayoutParams(全局变量）相关参数
        windowManagerParams = ((FloatApplication) getApplication()).getWindowParams();
        // 设置window type
        windowManagerParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        // 设置图片格式，效果为背景透明 PixelFormat.RGBA_8888
        windowManagerParams.format = PixelFormat.RGB_888;
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
        windowManagerParams.width = 300;
        windowManagerParams.height = 500;

        textView = new TextView(getApplicationContext());
        textView.setBackgroundColor(R.color.coloeBlack);
//        textView.getBackground().setAlpha(200);
        textView.append("a");
        textView.setOnClickListener(this);
        textView.setSingleLine(false);
        textView.setMaxLines(10);
        textView.setWidth(100);
        textView.setHeight(200);


//        android:scrollbars="vertical"
//        android:singleLine="false"
//        android:maxLines="10"
//        android:layout_width="match_parent"
//        android:layout_height="wrap_content"

        windowManager.addView(textView,windowManagerParams);
    }



    private HttpService httpService;
    private void getService() {
        ServiceConnection conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                httpService = ((HttpService.HttpServiceBinder)service).getService();
                if(httpService != null) {
                    httpService.setOnHttpServiceListener(new HttpService.HttpServiceListener() {
                        @Override
                        public void onResult(String msg) {
                            Log.e("HttpService","onResult receive :"+msg);
//                            if(textView.getText().length()>1000) {
//                                textView.setText("");
//                            }
                            textView.setText(msg);
                        }
                    });
                    Log.e("HttpService","doTask:");
                    httpService.doTask();
                } else {
                    Log.e("HttpService","null:");
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };

        Intent bindIntent = new Intent(this, HttpService.class);
        bindService(bindIntent, conn, BIND_AUTO_CREATE);
//        startService(bindIntent);
        Log.e("HttpService","getService");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void onDestroy() {
        super.onDestroy();
        // 在程序退出(Activity销毁）时销毁悬浮窗口
        windowManager.removeView(textView);
    }

    public void onClick(View v) {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
    }
}
