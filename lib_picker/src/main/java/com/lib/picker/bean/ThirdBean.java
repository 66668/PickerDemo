package com.lib.picker.bean;


import com.lib.picker.bean.base.LinkedThirdItem;

import java.io.Serializable;
import java.util.List;


/**
 * 新版UI AddressLinkedPicker 房间号联动数据
 * 三级数据
 */
public class ThirdBean implements LinkedThirdItem<FourthBean>, Serializable {
    private int nodeId;
    private int parentId;
    private String nodeName;
    private String level;
    //
    List<FourthBean> lists;

    public ThirdBean(int nodeId, int parentId, String nodeName, String level) {
        this.nodeId = nodeId;
        this.parentId = parentId;
        this.nodeName = nodeName;
        this.level = level;
    }

    public ThirdBean(int nodeId, int parentId, String nodeName, String level, List<FourthBean> lists) {
        this.nodeId = nodeId;
        this.parentId = parentId;
        this.nodeName = nodeName;
        this.level = level;
        this.lists = lists;
    }

    public void setLists(List<FourthBean> lists) {
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
    public List<FourthBean> getFours() {
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

    public List<FourthBean> getLists() {
        return lists;
    }

}
