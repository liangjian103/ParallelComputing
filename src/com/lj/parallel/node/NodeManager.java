package com.lj.parallel.node;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lj.parallel.exceptions.ZKException;

/***
 * 节点管理器，选举主节点，节点数据分发规则控制
 * 
 * @author James 2014-9-5 09:35:13
 * 
 */
public class NodeManager {

	private static final Logger log = LoggerFactory.getLogger(NodeManager.class);

	private static final String rootNode = "/parallel";
	private static final String serverNode = "/server";
	private static String serverNodeSequential = null;
	private static boolean isLeader = false;

	NodeManager() {
		try {
			ZKManager.INSTANCE.zkConnection("192.168.112.129:2181,192.168.112.130:2181,192.168.112.131:2181", 100000);
		} catch (ZKException e) {
			log.error(e.getMessage(), e);
		}
	}

	/** 启动节点 */
	public void start() {
		try {
			// 1 创建ROOT节点
			ZKManager.INSTANCE.createPathForPERSISTENT(rootNode, "" + System.currentTimeMillis());
			// 2 创建当前服务临时节点,获取临时节点名称
			createChildren();
			// 3 关注所有子节点变化情况，如有变化情况，则重新关注所有子节点
			regWatcherForChildren();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/** 创建当前服务临时节点,获取临时节点名称 */
	private void createChildren() {
		try {
			if (ZKManager.INSTANCE.exists(rootNode + serverNode + serverNodeSequential) == null) {
				String serverNodeSequentialPath = ZKManager.INSTANCE.createPathForEPHEMERAL_SEQUENTIAL(rootNode + serverNode, InetAddress.getLocalHost().getHostAddress());
				serverNodeSequential = serverNodeSequentialPath.substring((rootNode + serverNode).length(), serverNodeSequentialPath.length());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/** 关注所有子节点变化情况，如有变化情况，则重新关注所有子节点 */
	private void regWatcherForChildren() {
		try {
			ZKManager.INSTANCE.getChildren(rootNode, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					createChildren();
					// 选举Leader
					isLeader = isLeader();
					regWatcherForChildren();
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 当前服务节点是Leader
	 * 
	 * @return true:是;false:不是
	 */
	private boolean isLeader() {
		boolean boo = false;
		try {
			List<String> list = ZKManager.INSTANCE.getChildren(rootNode);
			List<Integer> dlist = new ArrayList<Integer>();
			for (String nodeName : list) {
				int nodeNameSequential = Integer.parseInt(nodeName.substring(serverNode.length(), nodeName.length()));
				dlist.add(nodeNameSequential);
			}
			boo = Collections.min(dlist) == Integer.parseInt(serverNodeSequential);
		} catch (ZKException e) {
			log.error(e.getMessage(), e);
		}
		return boo;
	}

	public static void main(String[] args) throws Exception {
		NodeManager manager = new NodeManager();
		manager.start();
		System.out.println("OK!");
		for (int i = 0; i < 5000; i++) {
			System.out.println(serverNodeSequential + ">>>>>" + isLeader);
			Thread.sleep(10000);
		}
		ZKManager.INSTANCE.releaseConnection();
	}

}
