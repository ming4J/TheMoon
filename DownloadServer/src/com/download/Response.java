package com.download;

import java.io.Serializable;

public class Response implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//�б���Ӧ����
	private String[] names;//�����ļ�����
	private long[] sizes;//�����ļ��Ĵ�СKb
	private String [] fileTypes;
	private String [] filetimes;
	//�ļ�������Ӧ����
	private String name;//�����ļ���
	private String usname;
	private long size;//�����ļ���Сbyte
	private long  lastModify;//����޸�ʱ�� ����

	
	
	
	public Response() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public Response(String[] names, long[] sizes, String name, long size,
			long  lastModify) {
		super();
		this.names = names;
		this.sizes = sizes;
		this.name = name;
		this.size = size;
		this.lastModify = lastModify;
	}
public String getUsname() {
	return usname;
}
public void setUsname(String usname) {
	this.usname = usname;
}
public void setFiletimes(String [] filetimes) {
	this.filetimes = filetimes;
}
public String [] getFiletimes() {
	return filetimes;
}
	public String[] getNames() {
		return names;
	}
	public void setNames(String[] names) {
		this.names = names;
	}
	public long[] getSizes() {
		return sizes;
	}
	public void setSizes(long[] sizes) {
		this.sizes = sizes;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
public void setFileTypes(String[] fileTypes) {
	this.fileTypes = fileTypes;
}
public String[] getFileTypes() {
	return fileTypes;
}
public void setLastModify(long lastModify) {
	this.lastModify = lastModify;
}
public long getLastModify() {
	return lastModify;
}
	
	
	
}
