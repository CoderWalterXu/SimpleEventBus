package com.xlh.study.simpleeventbus;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.xlh.study.eventbuslibraray.WxEventBus;
import com.xlh.study.simpleeventbus.bean.MessageBean;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author: Watler Xu
 * time:2020/4/27
 * description:
 * version:0.0.1
 */
public class SecondActivity extends AppCompatActivity {

    Button btnSend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        btnSend = findViewById(R.id.btn_send);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });



    }

    private void sendMessage() {

        MessageBean bean = new MessageBean();
        bean.setTitle("SecondActivity标题");
        bean.setContent("SecondActivity内容");

        WxEventBus.getDefault().post(bean);

    }

}
