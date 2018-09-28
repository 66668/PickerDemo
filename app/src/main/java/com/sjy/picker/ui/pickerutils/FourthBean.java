package com.sjy.picker.ui.pickerutils;




import java.util.List;

/**
 * 三级数据
 */
public class FourthBean implements LinkedFourItem<FifthBean> {
    String name;
    List<FifthBean> lists;

    public FourthBean(String name, List<FifthBean> lists) {
        this.lists = lists;
        this.name = name;
    }

    public FourthBean(String name) {
        this.name = name;
    }

    public void setLists(List<FifthBean> lists) {
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
    public List<FifthBean> getFifths() {
        return lists;
    }
}
