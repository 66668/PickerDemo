package com.sjy.roompicker.roompicker;

import com.alibaba.fastjson.JSONArray;

import java.io.Serializable;

/**
 * 描述： 随机码开门
 * <p>
 * Created by audienl@qq.com on 2018/10/10.
 */
public class RoomItemBean implements Serializable {
    String node_name;
    public String level;
    int parent_id;
    int node_id;
    JSONArray child;

    public RoomItemBean(String node_name, String level, int parent_id, int node_id, JSONArray child) {
        this.node_name = node_name;
        this.level = level;
        this.parent_id = parent_id;
        this.node_id = node_id;
        this.child = child;
    }

    public String getNode_name() {
        return node_name;
    }

    public void setNode_name(String node_name) {
        this.node_name = node_name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public int getNode_id() {
        return node_id;
    }

    public void setNode_id(int node_id) {
        this.node_id = node_id;
    }

    public JSONArray getChild() {
        return child;
    }

    public void setChild(JSONArray child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return "RoomItemBean{" +
                "node_name='" + node_name + '\'' +
                ", level='" + level + '\'' +
                ", parent_id=" + parent_id +
                ", node_id=" + node_id +
                ", child=" + child +
                '}';
    }
}
