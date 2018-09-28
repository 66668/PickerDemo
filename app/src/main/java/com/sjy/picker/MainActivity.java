package com.sjy.picker;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.sjy.picker.ui.AddressLinkedPicker;
import com.sjy.picker.ui.pickerutils.AddressData;
import com.sjy.picker.ui.pickerutils.FifthBean;
import com.sjy.picker.ui.pickerutils.FirstBean;
import com.sjy.picker.ui.pickerutils.FourthBean;
import com.sjy.picker.ui.pickerutils.OnWheelLinkedListener;
import com.sjy.picker.ui.pickerutils.SecondBean;
import com.sjy.picker.ui.pickerutils.ThirdBean;
import com.sjy.picker.ui.pickerutils.WheelView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    AddressLinkedPicker picker;
    private String[] labels;
    private AddressData provider;
    private int showNum;
    private List<FirstBean> firstBeans;
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
        picker.setShadowColor(0xFF88CCAA);
        picker.setDividerRatio(WheelView.DividerConfig.FILL);
        picker.setOnWheelLinkedListener(new OnWheelLinkedListener() {
            @Override
            public void onWheelLinked(int firstPosition, int secondPosition, int thirdPosition, int fourthPosition, int fifthPosition) {
                Toast.makeText(MainActivity.this, "选中：" + firstPosition + "--" + secondPosition + "--" + thirdPosition + "--" + fourthPosition + "--" + fifthPosition + "--", Toast.LENGTH_SHORT).show();
            }
        });
        picker.buildView();
    }

    private void createData() {
        labels = new String[]{"小区", "楼", "单元", "层", "房间号"};
        showNum = 5;
        /**
         * 五层for循环，小心脏扑腾的
         */
        //01小区信息
        firstBeans = new ArrayList<>();
        for (int i = 0; i < 2; i++) {//01小区
            FirstBean bean = new FirstBean(i + "小区");
            //02楼信息
            List<SecondBean> secondBeans = new ArrayList<>();

            for (int j = 0; j < 9; j++) {//02楼
                if (i == 0) {
                    SecondBean bean2 = new SecondBean("10-" + j);
                    //03 单元信息
                    List<ThirdBean> thirdBeans = new ArrayList<>();

                    for (int k = 0; k < 3; k++) {//03单元
                        ThirdBean bean3 = new ThirdBean("0" + (k + 1));
                        //04 层信息
                        List<FourthBean> fourthBeans = new ArrayList<>();
                        for (int a = 0; a < 6; a++) {
                            FourthBean bean4 = new FourthBean("0" + (a + 1));

                            //05房间号信息
                            List<FifthBean> fifthBeans = new ArrayList<>();
                            fifthBeans.add(new FifthBean("0101"));
                            fifthBeans.add(new FifthBean("0102"));
                            fifthBeans.add(new FifthBean("0201"));
                            fifthBeans.add(new FifthBean("0202"));
                            fifthBeans.add(new FifthBean("0301"));
                            fifthBeans.add(new FifthBean("0302"));
                            fifthBeans.add(new FifthBean("0401"));
                            fifthBeans.add(new FifthBean("0402"));
                            fifthBeans.add(new FifthBean("0501"));
                            fifthBeans.add(new FifthBean("0502"));
                            fifthBeans.add(new FifthBean("0601"));
                            fifthBeans.add(new FifthBean("0602"));

                            //04添加楼号信息
                            bean4.setLists(fifthBeans);
                            fourthBeans.add(bean4);
                        }

                        //03添加层信息
                        bean3.setLists(fourthBeans);
                        thirdBeans.add(bean3);
                    }
                    //02 添加单元信息
                    bean2.setLists(thirdBeans);
                    secondBeans.add(bean2);
                } else {

                    SecondBean bean2 = new SecondBean("20-" + j);
                    //03 单元信息
                    List<ThirdBean> thirdBeans = new ArrayList<>();

                    for (int k = 0; k < 3; k++) {//03单元
                        ThirdBean bean3 = new ThirdBean("00" + (k + 1));
                        //04 层信息
                        List<FourthBean> fourthBeans = new ArrayList<>();
                        for (int a = 0; a < 5; a++) {
                            FourthBean bean4 = new FourthBean("0" + (a + 1));

                            //05房间号信息
                            List<FifthBean> fifthBeans = new ArrayList<>();
                            fifthBeans.add(new FifthBean("0101"));
                            fifthBeans.add(new FifthBean("0102"));
                            fifthBeans.add(new FifthBean("0201"));
                            fifthBeans.add(new FifthBean("0202"));
                            fifthBeans.add(new FifthBean("0301"));
                            fifthBeans.add(new FifthBean("0302"));
                            fifthBeans.add(new FifthBean("0401"));
                            fifthBeans.add(new FifthBean("0402"));
                            fifthBeans.add(new FifthBean("0501"));
                            fifthBeans.add(new FifthBean("0502"));

                            //04添加楼号信息
                            bean4.setLists(fifthBeans);
                            fourthBeans.add(bean4);
                        }

                        //03添加层信息
                        bean3.setLists(fourthBeans);
                        thirdBeans.add(bean3);
                    }
                    //02 添加单元信息
                    bean2.setLists(thirdBeans);
                    secondBeans.add(bean2);
                }
            }

            //01添加楼信息
            bean.setLists(secondBeans);
            firstBeans.add(bean);
        }

        //构建数据源给选择器使用
        provider = new AddressData(firstBeans, showNum, labels);
    }
}
