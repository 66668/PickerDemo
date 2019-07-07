package com.lib.picker.bean;


import com.lib.picker.bean.base.LinkedItem;

import java.io.Serializable;

/**
 * 新版UI AddressLinkedPicker 房间号联动数据
 * 5级数据
 */
public class FifthBean implements LinkedItem, Serializable {
	private int nodeId;
	private int parentId;
	private String nodeName;
	private String level;

	public FifthBean(int nodeId, int parentId, String nodeName, String level) {
		this.nodeId = nodeId;
		this.parentId = parentId;
		this.nodeName = nodeName;
		this.level = level;
	}

	@Override
	public Object getId() {
		return null;
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

}
