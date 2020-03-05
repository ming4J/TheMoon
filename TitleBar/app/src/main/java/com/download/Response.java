package com.download;

/**
 * Created by shaom on 2016/10/6.
 */
import java.io.Serializable;

public class Response implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //列表响应数据
    private String[] names;//所有文件按名
    private long[] sizes;//所有文件的大小Kb
    private String [] fileTypes;
    private String [] filetimes;
    //文件下载响应数据
    private String name;//当额文件名
    private long size;//单个文件大小byte
    private long lastModify;//最后修改时间 毫秒




    public Response() {
        super();
        // TODO Auto-generated constructor stub
    }


    public Response(String[] names, long[] sizes, String name, long size,
                    long lastModify) {
        super();
        this.names = names;
        this.sizes = sizes;
        this.name = name;
        this.size = size;
        this.lastModify = lastModify;
    }
    public void setFiletimes(String[] filetimes) {
        this.filetimes = filetimes;
    }
    public String[] getFiletimes() {
        return filetimes;
    }
    public void setFileTypes(String[] fileTypes) {
        this.fileTypes = fileTypes;
    }
    public String[] getFileTypes() {
        return fileTypes;
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
    public long getLastModify() {
        return lastModify;
    }
    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

}
