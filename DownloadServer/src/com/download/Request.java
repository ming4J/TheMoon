package com.download;

import java.io.Serializable;

public class Request implements Serializable {

	private static final long serialVersionUID = 1L;

	//����ʹ��������������ö�ٴ���
	
//	//��ʾ�����б��whatֵ
//	public static final int WHAT_LIST=1;
//
//	//��ʾ���������ļ���whatֵ
//	public static final int WHAT_DOWNLOAD=2;
	
//	//�б�����
//	private int what;

	//�б�����
	private What what;//what��������Ϊö�����ͣ������������Ʋ���ʹ���������������

	//�ļ���������
	private String usname;
	private String name;
    private String filename;
	private String passWord;
	private String idNum;
	private String rePassword;
	private long size;
	private String uploadfilename;
	private String filepath;
	 private String downloadfilepath;
	 private String renamepth;
	 private String rename;
	 private String movepath;
	 private String movenewpath;
	 public String getMovepath() {
		return movepath;
	}
	 public void setMovepath(String movepath) {
		this.movepath = movepath;
	}
	 public String getMovenewpath() {
		return movenewpath;
	}
	 public void setMovenewpath(String movenewpath) {
		this.movenewpath = movenewpath;
	}
	 public void setRename(String rename) {
		this.rename = rename;
	}
	 public String getRename() {
		return rename;
	}
	 public String getRenamepth() {
		return renamepth;
	}
	 public void setRenamepth(String renamepth) {
		this.renamepth = renamepth;
	}

	    public void setDownloadfilepath(String downloadfilepath) {
	        this.downloadfilepath = downloadfilepath;
	    }

	    public String getDownloadfilepath() {
	        return downloadfilepath;
	    }
	public String getUploadfilename() {
		return uploadfilename;
	}
	public void setUploadfilename(String uploadfilename) {
		this.uploadfilename = uploadfilename;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public String getUsname() {
		return usname;
	}
	public void setUsname(String usname) {
		this.usname = usname;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	
	public long getSize() {
		return size;
	}
	



	public void setSize(long size) {
		this.size = size;
	}

public String getRePassword() {
	return rePassword;
}
public void setRePassword(String rePassword) {
	this.rePassword = rePassword;
}
	public String getPassWord() {
		return passWord;
	}



	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	
	public String getIdNum() {
		return idNum;
	}
	public void setIdNum(String idNum) {
		this.idNum = idNum;
	}
	
	
	


	
	public Request() {
		super();
	}



	//ʹ��ö������
//	public Request(int what, String name) {
	public Request(What what, String name) {
		super();
		this.what = what;
		this.name = name;
	}



	//ʹ��ö������
//	public int getWhat() {
	public What getWhat() {
		return what;
	}



	//ʹ��ö������
//	public void setWhat(int what) {
	public void setWhat(What what) {
		this.what = what;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
}
