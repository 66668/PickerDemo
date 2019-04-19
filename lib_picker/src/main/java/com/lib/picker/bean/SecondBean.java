package com.lib.picker.bean;


import com.lib.picker.pickerutils.LinkedSecondItem;

import java.io.Serializable;
import java.util.List;


/**
 * 新版UI AddressLinkedPicker 房间号联动数据
 * 二级数据
 */
public class SecondBean implements LinkedSecondItem<ThirdBean>, Serializable {
	private int nodeId;
	private int parentId;
	private String nodeName;
	private String level;
	//
	List<ThirdBean> lists;

	public SecondBean(int nodeId, int parentId, String nodeName, String level) {
		this.nodeId = nodeId;
		this.parentId = parentId;
		this.nodeName = nodeName;
		this.level = level;
	}

	public SecondBean(int nodeId, int parentId, String nodeName, String level, List<ThirdBean> lists) {
		this.nodeId = nodeId;
		this.parentId = parentId;
		this.nodeName = nodeName;
		this.level = level;
		this.lists = lists;
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

	public List<ThirdBean> getLists() {
		return lists;
	}

}
