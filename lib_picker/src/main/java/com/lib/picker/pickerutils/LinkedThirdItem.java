package com.lib.picker.pickerutils;


import java.util.List;

/**
 * 用于联动选择器展示的第三级条目
 * <br />
 * sjy 0914
 */
public interface LinkedThirdItem<Fur> extends LinkedItem {

    List<Fur> getFours();

}