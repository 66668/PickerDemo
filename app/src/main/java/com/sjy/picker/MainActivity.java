package com.sjy.picker;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.Toast;


import com.lib.picker.AddressLinkedPicker;
import com.lib.picker.pickerutils.AddressData;
import com.lib.picker.bean.FifthBean;
import com.lib.picker.bean.FirstBean;
import com.lib.picker.bean.FourthBean;
import com.lib.picker.pickerutils.OnWheelLinkedListener;
import com.lib.picker.bean.SecondBean;
import com.lib.picker.bean.ThirdBean;
import com.lib.picker.pickerutils.WheelView;
import com.sjy.picker.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    AddressLinkedPicker picker;

    private AddressData provider;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showAddressPicker();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        picker = findViewById(R.id.picker);

        //**************************************
        new Thread(new Runnable() {
            @Override
            public void run() {
                createData();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void showAddressPicker() {
        picker.setData(provider);
        picker.setOffset(2);
        picker.setUseWeight(true);
        // //设置默认选中位置
        picker.setDefaultPosition(0, 0, 0, 0, 0);
        picker.setShadowColor(0xFF88CCAA);
        picker.setDividerRatio(WheelView.DividerConfig.FILL);
        picker.setOnWheelLinkedListener(new OnWheelLinkedListener() {
            @Override
            public void onWheelLinked(String nodeID, String room) {
                Toast.makeText(MainActivity.this, "选中：" + nodeID + "--" + room, Toast.LENGTH_SHORT).show();
            }
        });
        picker.buildView();
    }

    private void createData() {
        //自定义生成
        provider = DataUtils.getData();
    }
}
