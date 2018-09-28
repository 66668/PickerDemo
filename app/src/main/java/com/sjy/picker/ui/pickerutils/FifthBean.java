package com.sjy.picker.ui.pickerutils;


/**
 * 5级数据
 */
public class FifthBean implements LinkedItem {
    String name;

    public FifthBean(String name) {
        this.name = name;
    }

    @Override
    public Object getId() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}
