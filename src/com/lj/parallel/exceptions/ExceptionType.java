package com.lj.parallel.exceptions;

/**
 * 异常类型
 * 
 * @author James Date:2014-9-9 10:47:55
 * 
 */
public enum ExceptionType {

	zkConnection("zookeeper连接异常"), // zookeeper连接异常
	zkCreatePath("创建zookeeper节点异常");// 创建zookeeper节点异常

	private String context;// 异常描述

	ExceptionType(String context) {
		this.context = context;
	}

	public String getContext() {
		return context;
	}

	// public static void main(String[] args) {
	// System.out.println(ExceptionType.zkConnection.getContext());
	// System.out.println(ExceptionType.zkCreatePath.getContext());
	// }
}
