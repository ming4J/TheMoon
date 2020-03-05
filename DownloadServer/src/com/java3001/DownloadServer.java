package com.java3001;

import java.awt.Font;
//import java.io.BufferedReader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
//import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

//import javax.sound.midi.Receiver;















import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.download.Request;
import com.download.Response;




public class DownloadServer extends JFrame {
	private String filepostion;
	private File dir ;//服务器端的一个目录，里面提供了可以供客户端下载的文件
	private File dir1 ;//服务器端的一个目录，里面提供了可以供客户端下载的文件
	private File dirdel;
	private int port;
	private ServerSocket ss;
	private String[] names;
	private long[] sizes;
	private String[] times;
	private long size;
	private String[] fileTypes;
	private String name;
	private String passWord;
	private String idNum;
	private String repassword;
	private JTextField aas;
	private String filename;
	private JButton btn1;
	private String selectPath;
	private DownloadServer frmobj;
	private File file;
	private int upflag=0;
	private boolean flag;
	
	
	
	public DownloadServer(int port){
		this.port=port;
		frmobj=this;
		aas=new JTextField("jald");
		this.add(aas);
		btn1=new JButton("Exit");
		btn1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frmobj.dispose();
				
			}
		});
	}
	
	
	
	public void startServer(){
		

		try {
			
			dir = new File(filepostion);
			if(!dir.exists()){
				dir.mkdir();
			}
			while(!dir.exists()){

				;
			}
			// 0 连接SQLite的JDBC
			
			String sql="jdbc:sqlite://"+filepostion+"/yhxx.db";
			Class.forName("org.sqlite.JDBC");
			
			// 1 建立一个数据库名yhxx.db的连接，如果不存在就在当前目录下创建之
			Connection conn = DriverManager.getConnection(sql);
			Statement stat = conn.createStatement();
			
			stat.executeUpdate("create table if not exists users(username varchar(20), password varchar(20),idnum varchar(20))");// 创建一个表
			conn.close(); // 结束数据库的连接

			} catch (Exception e) {
			e.printStackTrace();
			aas.setText(e.toString());
			return;
			}
		
		
		//服务线程
		new Thread(){
			@Override
			public void run(){
			try {
//				int i=0;
				ss=new ServerSocket(port);
				System.out.println("文件服务已经启动");
				aas.setText("文件服务已经启动");
				while(true){
					
					Socket s = ss.accept();//服务线程
//					System.out.println("获得链接"+i++);
					WorkThread t=new WorkThread(s);//工作线程对通道s执行通信
//					list.add(t);
					t.start();
					
				}
				
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("服务已经停止");
				e.printStackTrace();
			}
		}}.start();
	}
	
	
	class WorkThread extends Thread{
		Socket s;
		private InputStream in;
		private OutputStream out;

		public WorkThread(Socket s){
			this.s=s;
			try {
				in=s.getInputStream();
				out=s.getOutputStream();
			} catch (Exception e) {
				System.out.println(e);
			}
			
		}
		
	
		@Override
		public void run(){

			try {
				
				try {
					//1.获得请求对象
					Request req=receiveRequest();
					//2.根据请求发送相应对象
					sendResponse(req);
				} catch (Exception e) {
					System.out.println("通信失败");
					e.printStackTrace();
				}
				//3.断开连接
				s.close();
				


			} catch (Exception e) {
				System.out.println("通信失败");
				e.printStackTrace();
				
			}
			
		}



		private Request receiveRequest() throws Exception {

			ObjectInputStream ois=new ObjectInputStream(in);
			
			return (Request) ois.readObject();

		}


		private void sendResponse(Request req) throws Exception{

			switch (req.getWhat()) {
//			case Request.WHAT_LIST:
			case REGIST:
				name=req.getName();
				passWord=req.getPassWord();
				idNum=req.getIdNum();
				
				//System.out.print("注册"+name+"---"+passWord);
				sendRegist();
				break;
			case LOGIN:
				name=req.getName();
				passWord=req.getPassWord();
				System.out.print("登录"+name+"---"+passWord);
				sendLogin();
				break;
			case CHECK:
				name=req.getName();
				idNum=req.getIdNum();
				sendCheck();
				System.out.println("CHECK"+name+"---"+idNum);
				break;
			case REPASSWORD:
				name=req.getName();
				repassword=req.getRePassword();
				sendRePassword();
				System.out.println("REPASSWORD"+name+"---"+repassword);
				break;
			case LIST://此处使用枚举，不要前缀引用
				name=req.getName();
				passWord=req.getPassWord();
				String path=req.getFilepath();
				System.out.print(name+"---"+passWord);
				sendList(path);//发列表
				
				break;

			case UPLOAD://此处使用枚举，不要前缀引用
				String uploadpath=filepostion+req.getUploadfilename();
				Long size=req.getSize();
				
				
				System.out.print(size+"---"+uploadpath+"---------");
				sendUpload(size,uploadpath);//发列表
				
				break;

//			case Request.WHAT_DOWNLOAD:
			case DOWNLOAD://此处使用枚举，不要前缀引用
				String downloadname=filepostion+req.getDownloadfilepath();
				String downloadfilename=req.getFilename();
				sendFile(downloadname,downloadfilename);//发文件
				break;
			case DELETE:
				
				String filepath=filepostion+req.getFilename();
				System.out.print("======"+filepath);
				delete(filepath);
				break;
			case CREATEDIR:
				String path1=req.getFilepath();
				System.out.println("-----------"+path1);
				String filename=req.getFilename();
				System.out.println("-----------"+filename);
				createdir(path1,filename);
			case RENAME:
				String renamepath=req.getRenamepth();
				String rename=req.getRename();
				rename(renamepath,rename);
			case MOVE:
				String movepath=req.getMovepath();
				String movenewpath=req.getMovenewpath();
				movefile(movepath,movenewpath);
			}
			
		}

		private void movefile(String movepath, String movenewpath) {
			// TODO Auto-generated method stub
			Response resp=new Response();
			String oldpath=filepostion+movepath;
			String pathnew=filepostion+movenewpath;
			File file=new File(oldpath);
			File file2=new File(pathnew);
			if(file.renameTo(file2)){
				file.delete();
				resp.setName("chenggong");
			}else{
				resp.setName("shipai");
			}
			ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(out);
				oos.writeObject(resp);
				oos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}


		private void rename(String renamepath, String rename) {
			// TODO Auto-generated method stub
			Response resp=new Response();
			String allpath=filepostion+renamepath;
			int index=allpath.lastIndexOf(".");
			String filetype="";
			if(index!=-1){
				filetype=allpath.substring(index);
			}else{
				
			}
			System.out.println("---------"+filetype);
			String allpathnew=filepostion+rename;
			File file=new File(allpath);
			File filenew=new File(allpathnew+filetype);
			System.out.println("------"+allpath+"======="+allpathnew);
			
			if(file.renameTo(filenew)){
				resp.setName("chenggong");
				
			}else{
				resp.setName("shibai");
			}
			ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(out);
				oos.writeObject(resp);
				oos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}


		private void createdir(String path, String filename) {
			// TODO Auto-generated method stub
			String allpath=filepostion+path+filename;
			File file=new File(allpath);
			if(!file.exists()){
				file.mkdir();
			}
			Response resp=new Response();
			resp.setName("chenggong");
			ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(out);
				oos.writeObject(resp);
				oos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		

		private static final String matches = "[A-Za-z]:\\\\[^:?\"><*]*";  
		/** 
		 * 删除目录（文件夹）以及目录下的文件 
		 * @param   sPath 被删除目录的文件路径 
		 * @return  目录删除成功返回true，否则返回false 
		 */  
		public boolean deleteDirectory(String sPath) {  
		    //如果sPath不以文件分隔符结尾，自动添加文件分隔符  
		    if (!sPath.endsWith(File.separator)) {  
		        sPath = sPath + File.separator;  
		    }  
		    File dirFile = new File(sPath);  
		    //如果dir对应的文件不存在，或者不是一个目录，则退出  
		    if (!dirFile.exists() || !dirFile.isDirectory()) {  
		        return false;  
		    }  
		    flag = true;  
		    //删除文件夹下的所有文件(包括子目录)  
		    File[] files = dirFile.listFiles();  
		    for (int i = 0; i < files.length; i++) {  
		        //删除子文件  
		        if (files[i].isFile()) {  
		            flag = deleteFile(files[i].getAbsolutePath());  
		            if (!flag) break;  
		        } //删除子目录  
		        else {  
		            flag = deleteDirectory(files[i].getAbsolutePath());  
		            if (!flag) break;  
		        }  
		    }  
		    if (!flag) return false;  
		    //删除当前目录  
		    if (dirFile.delete()) {  
		        return true;  
		    } else {  
		        return false;  
		    }  
		}  
		/** 
		 * 删除单个文件 
		 * @param   sPath    被删除文件的文件名 
		 * @return 单个文件删除成功返回true，否则返回false 
		 */  
		public boolean deleteFile(String sPath) {  
		    flag = false;  
		    file = new File(sPath);  
		    // 路径为文件且不为空则进行删除  
		    if (file.isFile() && file.exists()) {  
		        file.delete();  
		        flag = true;  
		    }  
		    return flag;  
		}  
		public boolean DeleteFolder(String sPath) {  
		    flag = false;  
		    file = new File(sPath);  
		    // 判断目录或文件是否存在  
		    if (!file.exists()) {  // 不存在返回 false  
		        return flag;  
		    } else {  
		        // 判断是否为文件  
		        if (file.isFile()) {  // 为文件时调用删除文件方法  
		            return deleteFile(sPath);  
		        } else {  // 为目录时调用删除目录方法  
		            return deleteDirectory(sPath);  
		        }  
		    }  
		}  
		private void sendRegist() throws Exception{
			Response resp=new Response();
//			//要根据用户名和密码，判断是否符合注册条件，进行注册，如果成功，返回chenggong，否则返回shibai
//			if(name.equals("zs"))
//			{
//				resp.setName("chenggong");
//				resp.setSize(9);
//
//			}else{
//				resp.setName("shibai");
//				resp.setSize(6);
//
//			}
			
			aas.setText("zhuce");
			try {
				// 0 连接SQLite的JDBC
				String sql="jdbc:sqlite://"+filepostion+"/yhxx.db";
				Class.forName("org.sqlite.JDBC");
				
				// 1 建立一个数据库名yhxx.db的连接，如果不存在就在当前目录下创建之
				Connection conn = DriverManager.getConnection(sql);
				Statement stat = conn.createStatement();
				
				ResultSet rs = stat.executeQuery("select * from users where username='"+name+"'"); // 查询数据
				if (rs.next()) { 
				System.out.print("username = " + rs.getString("username") + ", "); // 列属性一
				System.out.println("password = " + rs.getString("password")); // 列属性二
				resp.setName("shibai");
				resp.setSize(6);

				}else{
					System.out.println("insert into users values('"+name+"','"+passWord+"','"+idNum+"')");
					stat.executeUpdate("insert into users values('"+name+"','"+passWord+"','"+idNum+"')"); // 插入数据
					dir = new File(filepostion+"/"+name);
					
					if(!dir.exists()){
						dir.mkdir();
					}
					dir1 = new File(filepostion+"/"+name);
					if(!dir1.exists()){
						dir1.mkdir();
					}
					resp.setName("chenggong");
					resp.setSize(9);

				}
				rs.close();
				conn.close(); // 结束数据库的连接
				} catch (Exception e) {
				e.printStackTrace();
				resp.setName("shibai");
				resp.setSize(6);

				}


			
			
			
			
			

			ObjectOutputStream oos=new ObjectOutputStream(out);
			oos.writeObject(resp);
			oos.flush();
		}
		//
		private void sendCheck() throws Exception{
			Response resp=new Response();
			try{
				String sql="jdbc:sqlite://"+filepostion+"/yhxx.db";
				Class.forName("org.sqlite.JDBC");
				Connection conn = DriverManager.getConnection(sql);
				Statement stat = conn.createStatement();
				ResultSet rs = stat.executeQuery("select * from users where username='"+name+"'");
				if(rs.next()){
					System.out.println(rs.getString(3)+"=========");
					if(rs.getString(3).equals(idNum)){
						resp.setName("find");
						resp.setSize(9);
						System.out.print("username = " + rs.getString("username") + ", "); // 列属性一
						System.out.println("idNum = " + rs.getString("idNum"));
					}
					else{
						resp.setName("nofind");
						resp.setSize(6);
					
					
				}
				}
				rs.close();
				conn.close();
			}catch(Exception e){
				e.printStackTrace();
				resp.setName("nofind");
				resp.setSize(6);
			}
			
			ObjectOutputStream oos=new ObjectOutputStream(out);
			oos.writeObject(resp);
			oos.flush();
		}
		
		private void sendRePassword() throws Exception{
			System.out.println("Start");
			Response resp=new Response();
			try{
				String sql="jdbc:sqlite://"+filepostion+"/yhxx.db";
				Class.forName("org.sqlite.JDBC");
				Connection conn = DriverManager.getConnection(sql);
				Statement stat = conn.createStatement();
//				ResultSet rs = stat.executeQuery("select * from users where username='"+name+"'");
				
					
					stat.executeUpdate("UPDATE users SET password ='"+repassword+"' WHERE username='"+name+"' ");
					resp.setName("ok");
					
				
				
					resp.setSize(9);
				
//				rs.close();
				conn.close();
			}catch(Exception e){
				e.printStackTrace();
				resp.setName("no");
				resp.setSize(6);
			}
			ObjectOutputStream oos=new ObjectOutputStream(out);
			oos.writeObject(resp);
			oos.flush();
		}
		private void delete(String path)throws Exception{
			
			Response resp=new Response();
			//String path=filepostion+"/"+name+"/"+filename;
			
			System.out.println("-=-=-=-=-="+path);
		//	File dir=new File(path);
			
		//	if(dir.delete()){
			if(DeleteFolder(path)){
				resp.setName("chenggong");
			}else{
				resp.setName("shipai");
			}
			ObjectOutputStream oos=new ObjectOutputStream(out);
			oos.writeObject(resp);
			oos.flush();
				
		}
		
		
		private void sendLogin() throws Exception{
			Response resp=new Response();
			//要根据用户名和密码，判断是否符合登录条件，如果成功，返回chenggong，否则返回shibai

			aas.setText("denglu");

			try {
				// 0 连接SQLite的JDBC
				String sql="jdbc:sqlite://"+filepostion+"/yhxx.db";
				Class.forName("org.sqlite.JDBC");
				
				// 1 建立一个数据库名yhxx.db的连接，如果不存在就在当前目录下创建之
				Connection conn = DriverManager.getConnection(sql);
				Statement stat = conn.createStatement();
				
				ResultSet rs = stat.executeQuery("select * from users"); // 查询数据
				if (rs.next()) { 
				System.out.print("username = " + rs.getString("username") + ", "); // 列属性一
				System.out.println("password = " + rs.getString("password")); // 列属性二

				}
				rs.close();
				conn.close(); // 结束数据库的连接
				} catch (Exception e) {
				e.printStackTrace();

				}

			
			try {
				// 0 连接SQLite的JDBC
				String sql="jdbc:sqlite://"+filepostion+"/yhxx.db";
				Class.forName("org.sqlite.JDBC");
				
				// 1 建立一个数据库名yhxx.db的连接，如果不存在就在当前目录下创建之
				Connection conn = DriverManager.getConnection(sql);
				Statement stat = conn.createStatement();
				
				ResultSet rs = stat.executeQuery("select * from users where username='"+name+"' and password='"+passWord+"'"); // 查询数据
				if (rs.next()) { 
				System.out.print("username = " + rs.getString("username") + ", "); // 列属性一
				System.out.println("password = " + rs.getString("password")); // 列属性二
				dir = new File(filepostion+"/"+name);
				if(!dir.exists()){
					dir.mkdir();
				}
				dir1 = new File(filepostion+"/"+name);
				if(!dir1.exists()){
					dir1.mkdir();
				}
				resp.setName("chenggong");
				resp.setSize(9);

				}else{
					resp.setName("shibai");
					resp.setSize(6);

				}
				rs.close();
				conn.close(); // 结束数据库的连接
				} catch (Exception e) {
				e.printStackTrace();
				resp.setName("shibai");
				resp.setSize(6);

				}
			
			

			
			ObjectOutputStream oos=new ObjectOutputStream(out);
			oos.writeObject(resp);
			oos.flush();
		}
		
		private void sendList(String path) throws Exception{
			//先准备好文件列表，只列出文件，不支持子目录
			String allpath=filepostion+path;
			File file=new File(allpath);
			//System.out.println(dir.getAbsolutePath());
			File[] list1=file.listFiles(new FileFilter() {//加上过滤器
				@Override
				public boolean accept(File f) {
					
					//return f.isFile();//只返回文件
					return true;
				}
			});
			List<File> fileList = new ArrayList<File>();
			for(int i=0;i<list1.length;i++){
				if(list1[i].isDirectory()){
					fileList.add(list1[i]);
				}
				
			}
			Collections.sort(fileList, new Comparator<File>() {
			    @Override
			    public int compare(File o1, File o2) {
			        if (o1.isDirectory() && o2.isFile())
			            return -1;
			        if (o1.isFile() && o2.isDirectory())
			            return 1;
			        return o2.getName().compareTo(o1.getName());
			    }
			});
			List<File> fileList1= new ArrayList<File>();
			for(int i=0;i<list1.length;i++){
				if(list1[i].isFile()){
					fileList1.add(list1[i]);
				}
				
			}
			Collections.sort(fileList1, new Comparator<File>() {
			    @Override
			    public int compare(File o1, File o2) {
			        if (o1.isDirectory() && o2.isFile())
			            return -1;
			        if (o1.isFile() && o2.isDirectory())
			            return 1;
			        return o2.getName().compareTo(o1.getName());
			    }
			});
			fileList.addAll(fileList1);
			int size1=fileList.size();
			File[] list=fileList.toArray(new File[size1]);
//			File[] filedir=dir.listFiles(new FileFilter() {
//				
//				@Override
//				public boolean accept(File fdir) {
//					// TODO Auto-generated method stub
//					return fdir.isDirectory();
//					
//				}
//			});
			
			if(list==null || list.length==0){
				System.out.println("没有文件，不能提供服务");
				
			}
			
			
			names=new String[list.length];
			sizes=new long[list.length];
			times=new String[list.length];
			fileTypes=new String[list.length];
			
			for(int i=0;i<list.length;i++){//得到文件名列表以及文件大小列表
				
				File f=list[i];
				
				
				names[i]=f.getName();
				
				sizes[i]=f.length();
				if(f.isDirectory()){
					sizes[i]=-1;
				}
				String ctime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(f.lastModified()));
				times[i]=ctime;
				 String fileName=f.getName();
				 System.out.println("=========文件名："+fileName);
			      String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
				fileTypes[i]=prefix;
			}
			Response resp=new Response();
			//要根据用户重新得到names和sizes，作为文件列表发送
			resp.setNames(names);
			resp.setSizes(sizes);
			resp.setFiletimes(times);
			resp.setFileTypes(fileTypes);
			
			ObjectOutputStream oos=new ObjectOutputStream(out);
			oos.writeObject(resp);
			oos.flush();
		}

		public void sendUpload(long size,String path ) throws Exception{
//			Response resp=new Response();
				System.out.println(path);
				
			long sizef=size;
			
			File file=new File(path);
			FileOutputStream raf=
					new FileOutputStream(file,true);

			
			InputStream sin=s.getInputStream();
			byte[] buf=new byte[8192];
			int n;
			long count=0;//已经下载的字节量
					try{
						while((n=sin.read(buf))!=-1){
							raf.write(buf,0,n);
							count+=n;
							
							if(count>=sizef){
								break;
							}
							System.out.println(count+"--"+sizef);
							}
						
					}catch (Exception e) {
						System.out.println("捕获到");
						upflag=1;
						
					}
				
		
			
				
			
					raf.close();
				//System.out.println("count");
			if(upflag==1||count<sizef){
				file.delete();
				System.out.println("------------------"+file.delete());
			}
			
			
			
		}
		
		private void sendFile(String path,String name) throws Exception{
			System.out.println(path+name);
			File f=new File(path);
			long size=f.length();
			long lm=f.lastModified();
			Response resp=new Response();
			resp.setName(name);
			resp.setSize(size);
			resp.setLastModify(lm);
			System.out.println("=============");
			//发文件信息
			ObjectOutputStream oos=new ObjectOutputStream(out);
			oos.writeObject(resp);
			oos.flush();
			System.out.println("---------------------");
			//发文件字节数据
			FileInputStream fis=new FileInputStream(f);
			byte[] buf=new byte[8192];
			int n;
			try{
				while((n=fis.read(buf))!=-1){
					out.write(buf,0,n);
					System.out.println(""+n);
				}
				out.flush();
				fis.close();
				
			}catch (Exception e) {
				System.out.println("成功");
			}
			

		}
		
	}
	public void GUI() {
		// TODO Auto-generated method stub
		JLabel jl=new JLabel("请选择创建云盘的物理地址：");
		jl.setBounds(20, 40, 200, 30);
		jl.setFont(new Font("宋体", Font.PLAIN, 14));
		final JTextField jf=new JTextField();
		jf.setBounds(20, 80, 160, 20);
		jf.setEditable(false);
		JButton jbtn=new JButton("选择");
		jbtn.setBounds(185, 80, 60, 20);
		final JButton start=new JButton("开始服务");
		start.setBounds(105, 185, 90, 40);
		final JLabel tips=new JLabel("开始服务········");
		tips.setBounds(20, 120, 200, 20);
		tips.setVisible(false);
		final String flag=jf.getText();
		jbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showOpenDialog(chooser);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
				  selectPath =chooser.getSelectedFile().getPath() ;
				  jf.setText(selectPath);
				  chooser.hide();
				}
				
			}
		});
		
		this.add(tips);
		this.add(start);
		this.add(jbtn);
		this.add(jl);
		this.add(jf);
		this.setLayout(null);
		this.setBounds(900, 400, 300, 300);
		this.setTitle("玉盘服务端");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
		
		
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(jf.getText().equals("")){
					int b=JOptionPane.showConfirmDialog( DownloadServer.this, "设置储存警告！", "警告",JOptionPane.WARNING_MESSAGE,JOptionPane.WARNING_MESSAGE);
					if(b==0){
						return;
					}else{
						dispose();
					}
				}else{
					String pathChange=selectPath.replace("\\", "/");
					//System.out.println(pathChange);
					filepostion=pathChange;
					tips.setVisible(true);
					startServer();
					
				}
					
								
			}
		});
	}

	
	public static void main(String[] args) {
		
		DownloadServer s=new DownloadServer(8000);
		s.GUI();
		
		
	}


	
}
