package com.sjy.picker.ui.pickerutils;




import java.util.List;

/**
 * 二级数据
 */
public class SecondBean implements LinkedSecondItem<ThirdBean> {
    String name;
    List<ThirdBean> lists;

    public SecondBean(String name, List<ThirdBean> lists) {
        this.lists = lists;
        this.name = name;
    }

    public SecondBean(String name) {
        this.name = name;
    }

    public void setLists(List<ThirdBean> lists) {
        this.lists = lists;
    }

    @Override
    public List<ThirdBean> getThirds() {
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
