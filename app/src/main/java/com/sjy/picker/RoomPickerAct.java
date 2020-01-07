package com.sjy.picker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sjy.roompicker.roompicker.RoomItemBean;
import com.sjy.roompicker.roompicker.RoomPicker;

import java.util.logging.Logger;

public class RoomPickerAct extends AppCompatActivity {
    RoomPicker picker;
    LinearLayout ly_room_rest;
    RelativeLayout ly_room;
    boolean isLand = false;
    boolean isUntouch = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_roompicker);
        initWidget();
        initFunc(true);
    }

    private void initWidget() {
        picker = findViewById(R.id.picker);
        ly_room = findViewById(R.id.ly_room);
        ly_room_rest = findViewById(R.id.ly_room_rest);
        ly_room_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFunc(false);
            }
        });
    }

    private void initFunc(boolean isInit) {
        if (isInit) {

            picker.setPort(!isLand);//横屏false
            picker.setUntouch(isUntouch);//是否是 非触屏设备
            picker.setRoomPickerCallback(pickerCallBack);
            picker.buildView(isInit);
        } else {
            if (picker.getCallback() == null) {
                picker.setRoomPickerCallback(pickerCallBack);
            }
            //重新设置竖屏+触屏UI高度
            if (picker.resetPickerView()) {
                changeRoomPickerView(false);
            }
            picker.setUntouch(isUntouch);//是否是 非触屏设备
            picker.buildView(false);
        }
    }

    /**
     * 房间筛选事件回调
     */
    RoomPicker.RoomPickerCallback pickerCallBack = new RoomPicker.RoomPickerCallback() {
        @Override
        public void onRoomSuccess(RoomItemBean bean) {
            Log.d("SJY", "获取呼叫房间号：" + bean.toString());
        }

        @Override
        public void onRoomFailed(String e) {

        }

        @Override
        public void hideReset() {
            ly_room_rest.setVisibility(View.GONE);
        }

        @Override
        public void showReset() {
            ly_room_rest.setVisibility(View.VISIBLE);
        }

        @Override
        public void hidePickerView() {

        }

        @Override
        public void changeToMaxView(boolean toMax) {
            changeRoomPickerView(toMax);
        }

    };

    /**
     * 设置房间高度
     *
     * @param toMax true->设置最大高度/false->回归高度
     */
    private void changeRoomPickerView(boolean toMax) {
        if (toMax) {//修改高度
            Log.i("SJY", "设置筛选器高度");
            if (isLand) {//横屏
                ViewGroup.LayoutParams params01 = this.picker.getLayoutParams();
                params01.height = (int) getResources().getDimension(R.dimen.room_land_picker_height_02);
                picker.setLayoutParams(params01);
                //
                ViewGroup.LayoutParams ly = ly_room.getLayoutParams();
                ly.height = (int) getResources().getDimension(R.dimen.room_land_total_height_02);
                ly_room.setLayoutParams(ly);
            } else {//竖屏
                ViewGroup.LayoutParams params01 = picker.getLayoutParams();
                params01.height = (int) getResources().getDimension(R.dimen.room_picker_total_height_02);
                picker.setLayoutParams(params01);
                //
                ViewGroup.LayoutParams ly = ly_room.getLayoutParams();
                ly.height = (int) getResources().getDimension(R.dimen.room_port_total_height_02);
                ly_room.setLayoutParams(ly);
            }
        } else {//回归高度
            Log.i("SJY", "回归筛选器高度");
            if (isLand) {//横屏
                ViewGroup.LayoutParams params01 = picker.getLayoutParams();
                params01.height = (int) getResources().getDimension(R.dimen.room_land_picker_height_01);
                picker.setLayoutParams(params01);
                //
                ViewGroup.LayoutParams ly = ly_room.getLayoutParams();
                ly.height = (int) getResources().getDimension(R.dimen.room_land_total_height_01);
                ly_room.setLayoutParams(ly);
            } else {//竖屏
                ViewGroup.LayoutParams params01 = picker.getLayoutParams();
                params01.height = (int) getResources().getDimension(R.dimen.room_picker_total_height_01);
                picker.setLayoutParams(params01);
                //
                ViewGroup.LayoutParams ly = ly_room.getLayoutParams();
                ly.height = (int) getResources().getDimension(R.dimen.room_port_total_height_01);
                ly_room.setLayoutParams(ly);
            }

        }
    }

}
