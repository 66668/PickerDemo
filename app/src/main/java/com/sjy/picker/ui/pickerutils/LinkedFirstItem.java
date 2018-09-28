package com.sjy.picker.ui.pickerutils;


import java.util.List;

/**
 * 用于联动选择器展示的第一级条目
 * <br />
 *sjy 0914
 */
public interface LinkedFirstItem<Snd> extends LinkedItem {
    //
    List<Snd> getSeconds();
}