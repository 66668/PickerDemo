package com.lib.picker.wheelpicker.bean;


import com.lib.picker.wheelpicker.bean.base.LinkedFourItem;

import java.io.Serializable;
import java.util.List;


/**
 * 新版UI AddressLinkedPicker 房间号联动数据
 * 三级数据
 */
public class FourthBean implements LinkedFourItem<FifthBean>, Serializable {
    private int nodeId;
    private int parentId;
    private String nodeName;
    private String level;
    //

    List<FifthBean> lists;

    public FourthBean(int nodeId, int parentId, String nodeName, String level) {
        this.nodeId = nodeId;
        this.parentId = parentId;
        this.nodeName = nodeName;
        this.level = level;
    }

    public FourthBean(int nodeId, int parentId, String nodeName, String level, List<FifthBean> lists) {
        this.nodeId = nodeId;
        this.parentId = parentId;
        this.nodeName = nodeName;
        this.level = level;
        this.lists = lists;
    }

    public void setLists(List<FifthBean> lists) {
        this.lists = lists;
    }

    @Override
    public Object getId() {
        return nodeId;
    }

    @Override
    public String getName() {
        return nodeName;
    }

    @Override
    public List<FifthBean> getFifths() {
        return lists;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<FifthBean> getLists() {
        return lists;
    }

}
