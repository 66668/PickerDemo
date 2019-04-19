package com.lib.picker.pickerutils;


import java.util.List;

/**
 * 用于联动选择器展示的第二级条目
 * <br />
 * sjy 0914
 */
public interface LinkedSecondItem<Trd> extends LinkedItem {

    List<Trd> getThirds();

}