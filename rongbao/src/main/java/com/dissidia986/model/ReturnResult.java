package com.dissidia986.model;


public class ReturnResult {

	private String code;
	private String msg;
	private Object data;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
	public Object getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public ReturnResult(String code , String msg){
		super();
		this.code = code;
		this.msg = msg;
	}
	
	
	public ReturnResult(String code, String msg, Object data) {
		super();
		this.code = code;
		this.msg = msg;
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "{\"code\": \""+this.getCode()+"\"，\"msg\": \""+this.getMsg()+"\"，\"data\":\" userId ="+this.getData()+"\" }";
	}
	
}
