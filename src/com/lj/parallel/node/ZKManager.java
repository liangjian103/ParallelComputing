package com.lj.parallel.node;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lj.parallel.exceptions.ExceptionType;
import com.lj.parallel.exceptions.ZKException;

public enum ZKManager {

	// 枚举方式单例模式
	INSTANCE;

	private static final Logger log = LoggerFactory.getLogger(ZKManager.class);

	private ZooKeeper zk = null;

	/***
	 * 建立Zookeeper连接
	 * 
	 * @param connectString
	 * @param timeout
	 * @throws ZKException
	 */
	public void zkConnection(String connectString, int timeout) throws ZKException {
		try {
			zk = new ZooKeeper(connectString, timeout, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					System.out.println("连接时创建的。" + event.toString());
				}
			});
		} catch (Exception e) {
			throw new ZKException(ExceptionType.zkConnection, "ZKManager.zkConnection() ERROR!!", e);
		}
	}

	/***
	 * 创建永久节点
	 * 
	 * @param path
	 * @param data
	 * @throws ZKException
	 */
	public void createPathForPERSISTENT(String path, String data) throws ZKException {
		try {
			if (this.zk.exists(path, null) == null) {
				this.zk.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		} catch (Exception e) {
			throw new ZKException(ExceptionType.zkCreatePath, "ZKManager.createPathForPERSISTENT() ERROR!!", e);
		}
	}

	/***
	 * 创建临时序列节点
	 * 
	 * @param path
	 * @param data
	 * @return 返回序列节点名称
	 * @throws ZKException
	 */
	public String createPathForEPHEMERAL_SEQUENTIAL(String path, String data) throws ZKException {
		try {
			return this.zk.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		} catch (Exception e) {
			throw new ZKException(ExceptionType.zkCreatePath, "ZKManager.createPathForEPHEMERAL_SEQUENTIAL() ERROR!!", e);
		}
	}

	/***
	 * 获取字节点列表
	 * 
	 * @param path
	 * @throws ZKException
	 */
	public Stat exists(String path) throws ZKException {
		try {
			return this.zk.exists(path, null);
		} catch (Exception e) {
			throw new ZKException(ExceptionType.zkConnection, "ZKManager.exists(" + path + ") ERROR!!", e);
		}
	}
	
	/***
	 * 获取字节点列表
	 * 
	 * @param path
	 * @throws ZKException
	 */
	public List<String> getChildren(String path) throws ZKException {
		try {
			return this.zk.getChildren(path, null);
		} catch (Exception e) {
			throw new ZKException(ExceptionType.zkConnection, "ZKManager.getChildren(" + path + ") ERROR!!", e);
		}
	}
	
	/***
	 * 获取字节点列表，并关注子节点变化
	 * 
	 * @param path
	 * @throws ZKException
	 */
	public List<String> getChildren(String path,Watcher watcher) throws ZKException {
		try {
			return this.zk.getChildren(path, watcher);
		} catch (Exception e) {
			throw new ZKException(ExceptionType.zkConnection, "ZKManager.getChildren(" + path + ") ERROR!!", e);
		}
	}
	
	/***
	 * 释放连接
	 * @throws ZKException
	 */
	public void releaseConnection() throws ZKException {
		try {
			this.zk.close();
		} catch (Exception e) {
			throw new ZKException(ExceptionType.zkConnection, "ZKManager.releaseConnection() ERROR!!", e);
		}
	}
}
