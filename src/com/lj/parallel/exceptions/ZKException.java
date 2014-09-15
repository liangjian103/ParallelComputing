package com.lj.parallel.exceptions;

/***
 * Zookeeper自定义异常处理
 * 
 * @author James Date:2014-9-9 11:23:16
 * 
 */
public class ZKException extends Exception {

	private ExceptionType exceptionType;

	public ZKException() {
		super();
	}

	public ZKException(ExceptionType exceptionType, Throwable cause) {
		super(exceptionType.getContext(), cause);
		this.exceptionType = exceptionType;
	}

	public ZKException(ExceptionType exceptionType, String message, Throwable cause) {
		super(message, cause);
		this.exceptionType = exceptionType;
	}

	public ExceptionType getExceptionType() {
		return exceptionType;
	}

}
