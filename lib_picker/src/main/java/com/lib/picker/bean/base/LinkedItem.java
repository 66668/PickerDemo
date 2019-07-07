package com.lib.picker.bean.base;


import com.lib.picker.wheelpicker.WheelItem;

/**
 * 用于联动选择器展示的条目
 * <br />
 * sjy 0914
 *
 */
public interface LinkedItem extends WheelItem {

    /**
     * 唯一标识，用于判断两个条目是否相同
     */
    Object getId();

}
