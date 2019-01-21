package com.sjy.picker.ui.pickerutils;

/**
 * 滑动过程数据联动监听
 */
public interface OnWheelLinkedListener {
    //TODO 想要什么参数，自己设置，这里本人设置选中位置
    void onWheelLinked(int firstPosition, int secondPosition, int thirdPosition, int fourthPosition, int fifthPosition);
}
