package com.davel.floatview;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class FloatActivity extends AppCompatActivity {
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

        //最小化
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private WindowManager windowManager = null;
    private WindowManager.LayoutParams windowManagerParams = null;
    //悬浮框文本
    private TextView textView;
    private ScrollView scrollView;
    LinearLayout mFloatLayout;
    private int lastTouchDownTime = 0;
    @SuppressLint("ClickableViewAccessibility")
    private void createView() {
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_view, null);

        windowManagerParams = ((FloatApplication) getApplication()).getWindowParams();
        // 添加悬浮窗至系统服务
        windowManagerParams.type = WindowManager.LayoutParams.TYPE_PHONE;   			// 设置window type
        windowManagerParams.format = PixelFormat.RGBA_8888;   			// 设置图片格式，效果为背景透明
        windowManagerParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowManagerParams.gravity = Gravity.LEFT | Gravity.TOP;

        // 设置悬浮窗口长宽数据
        windowManagerParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowManagerParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        DisplayMetrics dm = getResources().getDisplayMetrics();
        windowManagerParams.x = dm.widthPixels - windowManagerParams.width - 10;
        windowManagerParams.y = dm.heightPixels - windowManagerParams.height - 10;

        // 获取WindowManager
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        scrollView = mFloatLayout.findViewById(R.id.float_view_scroll_view);
        textView = mFloatLayout.findViewById(R.id.float_view_text_view);

        // 拖动浮标时修改浮标位置
        scrollView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                //长按2秒是拖动
                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    lastTouchDownTime = (int) (System.currentTimeMillis()/1000);
                }
                int currTime = (int) (System.currentTimeMillis()/1000);
                if(currTime-lastTouchDownTime>1 && event.getAction()==MotionEvent.ACTION_MOVE) {
                    windowManagerParams.x = (int) event.getRawX() - v.getMeasuredWidth() / 2;
                    windowManagerParams.y = (int) event.getRawY() - v.getMeasuredHeight() / 2;
                    windowManager.updateViewLayout(mFloatLayout, windowManagerParams);
                }
                return false;  // 此处必须返回false，否则OnClickListener获取不到监听
            }
        });
        windowManager.addView(mFloatLayout, windowManagerParams);
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

                            Message message = Message.obtain();
                            message.obj = msg;
                            mainHandler.sendMessage(message);//sendMessage()用来传送Message类的值到mHandle
                        }
                    });
                    httpService.doTask();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };

        Intent bindIntent = new Intent(this, HttpService.class);
        bindService(bindIntent, conn, BIND_AUTO_CREATE);
    }
    //新建Handler对象。
    Handler mainHandler = new Handler(){
        //handleMessage为处理消息的方法
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(textView.getText().length()>1000) {
                textView.setText("");
            }

            textView.append(msg.obj + "");
            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    };

    public void onDestroy() {
        super.onDestroy();
        // 在程序退出(Activity销毁）时销毁悬浮窗口
        windowManager.removeView(scrollView);
    }
}