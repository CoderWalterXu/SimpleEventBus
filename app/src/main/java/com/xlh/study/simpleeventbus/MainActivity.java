package com.xlh.study.simpleeventbus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xlh.study.eventbuslibraray.WxEventBus;
import com.xlh.study.eventbuslibraray.WxSubscribe;
import com.xlh.study.simpleeventbus.bean.MessageBean;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TEST";

    Button btnJump;
    TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WxEventBus.getDefault().register(this);

        btnJump = findViewById(R.id.btn_jump);
        tvContent = findViewById(R.id.tv_content);

        btnJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSecondActivity();
            }
        });

    }

    private void startSecondActivity() {
        Intent intent = new Intent();
        intent.setClass(this, SecondActivity.class);
        startActivity(intent);

    }

    @WxSubscribe
    public void receiveMessage(MessageBean bean){
        tvContent.setText(String.format("接收到的消息：\n %s",bean.toString()));
        Log.e(TAG,"MainActivity-->Main Thread Name:"+Thread.currentThread().getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WxEventBus.getDefault().unregister(this);
    }
}
