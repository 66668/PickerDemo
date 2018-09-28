package com.sjy.picker.ui.pickerutils;


import java.util.List;

/**
 * 用于联动选择器展示的第三级条目
 * <br />
 * sjy 0914
 */
public interface LinkedFourItem<Fiv> extends LinkedItem {

    List<Fiv> getFifths();

}