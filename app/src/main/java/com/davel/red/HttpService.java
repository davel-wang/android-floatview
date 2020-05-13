package com.davel.red;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class HttpService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new HttpServiceBinder();
    }
    public final class HttpServiceBinder extends Binder {
        public HttpService getService() {
            return HttpService.this;
        }
        public void startDownload() {

        }
    }


    public interface HttpServiceListener {
        void onResult(String msg);
    }
    private HttpServiceListener httpServiceListener;
    //注册回调接口的方法，供外部调用
    public void setOnHttpServiceListener(HttpServiceListener listener) {
        this.httpServiceListener = listener;
    }
    //开始做任务
    public void doTask() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                HttpRequest httpRequest = new HttpRequest();
                while(true){
                    try {
//                        String msg = httpRequest.get("https://2020.ip138.com/");
                        String msg = "hhhh";
                        Log.e("HttpService","get:"+msg);
                        //进度发生变化通知调用方
                        if(httpServiceListener != null){
                            httpServiceListener.onResult(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}



