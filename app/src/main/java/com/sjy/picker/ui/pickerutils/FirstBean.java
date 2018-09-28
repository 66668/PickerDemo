package com.sjy.picker.ui.pickerutils;


import java.util.List;

/**
 * 一级数据
 */
public class FirstBean implements LinkedFirstItem<SecondBean> {
    String name;
    List<SecondBean> lists;

    public FirstBean(String name, List<SecondBean> lists) {
        this.lists = lists;
        this.name = name;
    }
    public FirstBean(String name) {
        this.name = name;
    }

    public void setLists(List<SecondBean> lists) {
        this.lists = lists;
    }

    @Override
    public List<SecondBean> getSeconds() {
        return lists;
    }

    @Override
    public Object getId() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }
}
