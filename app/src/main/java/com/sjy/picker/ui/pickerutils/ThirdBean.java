package com.sjy.picker.ui.pickerutils;




import java.util.List;

/**
 * 三级数据
 */
public class ThirdBean implements LinkedThirdItem<FourthBean> {
    String name;
    List<FourthBean> lists;

    public ThirdBean(String name, List<FourthBean> lists) {
        this.lists = lists;
        this.name = name;
    }

    public ThirdBean(String name) {
        this.name = name;
    }

    public void setLists(List<FourthBean> lists) {
        this.lists = lists;
    }

    @Override
    public Object getId() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<FourthBean> getFours() {
        return lists;
    }
}
