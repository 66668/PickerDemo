package com.lib.picker.bean;


import com.lib.picker.pickerutils.LinkedFirstItem;

import java.io.Serializable;
import java.util.List;


/**
 * 新版UI AddressLinkedPicker 房间号联动数据
 * 一级数据 和接口数据结构相同
 */
public class FirstBean implements LinkedFirstItem<SecondBean>, Serializable {
	private int nodeId;
	private int parentId;
	private String nodeName;
	private String level;
	
	List<SecondBean> lists;

	public FirstBean(int nodeId, int parentId, String nodeName, String level) {
		this.nodeId = nodeId;
		this.parentId = parentId;
		this.nodeName = nodeName;
		this.level = level;
	}
	
	public FirstBean(int nodeId, int parentId, String nodeName, String level, List<SecondBean> lists) {
		this.nodeId = nodeId;
		this.parentId = parentId;
		this.nodeName = nodeName;
		this.level = level;
		this.lists = lists;
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
		return nodeId;
	}

	@Override
	public String getName() {
		return nodeName;
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

	public List<SecondBean> getLists() {
		return lists;
	}

}
