package org.benpao.viewactivity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.download.Request;
import com.download.Response;
import com.download.What;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.Arrays;

public class UserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView nav_username;
    private FloatingActionsMenuHidable fab;
    private FloatingActionButton search;
    private FloatingActionButton uplaod;
    private FloatingActionButton newdir;
    private FloatingActionButton backbtn;
    private FloatingActionButton movebtn;
    private NavigationView navigationView;
    private String download_usname;
    private String download_getfilename;
    private boolean isfirst;
    private ListView lv;
    private String ip;
    private int port=8000;
    private ProgressDialog pd;
    private String[] names ;
    private long[] sizes ;
    private String [] times;
    private String [] types;
    private String allpath;
    private String oldpath;
    private String oldfile;
    private Socket us;
    private Socket ds;
    private long firstTime = 0;
    public static int upflag=0;
    public static int dowflag=0;
    private String renamecheck;
    private int menuflag;
    private  String[] namenew;
    private  String[] timenew;
    private  long[] sizenew;
    private  String[] typenew;
    private  String exitcheck;



    public static void upstop(){
        upflag=1;
    }
    public static void dowstop(){
        dowflag=1;
    }
    //  private boolean moveflag;

    String abc="";
    String name;
    private int listposition;
    String passWord;
    private NotificationManager notifiM;
    private NotificationManager notifiMs;
    private Notification note;


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && isfirst) {
            movebtn.setEnabled(false);
            nav_username = (TextView) navigationView.findViewById(R.id.nav_username);

            Intent intent = getIntent();
            nav_username.setText("ID:"+intent.getStringExtra("name").toString());
            isfirst = false;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Intent intent=getIntent();
        allpath="/"+intent.getStringExtra("name");
        exitcheck=allpath;
        isfirst = true;
        ip=this.getString(R.string.ipadd);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        menuflag=0;
      //  moveflag=false;
        fab = (FloatingActionsMenuHidable) findViewById(R.id.detailed_multiple_actions);
        fab.show(true);
        movebtn=(FloatingActionButton) findViewById(R.id.user_moveto);
        //movebtn.setVisibility(View.GONE);







//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        upload();//调用上传
        search();//调用搜索
        New();//调用新建文件夹
        setData();
        setViews();
        registerForContextMenu(lv);
        showFileList();
        back();
        moveto();


    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
              //  Toast.makeText(UserActivity.this,"1111111",Toast.LENGTH_SHORT).show();
                //if (allpath.equals(exitcheck)){
                  //  Toast.makeText(UserActivity.this,"222222",Toast.LENGTH_SHORT).show();
                    long secondTime = System.currentTimeMillis();
                    if (secondTime - firstTime > 2000) {
                       // Toast.makeText(UserActivity.this,"33333333",Toast.LENGTH_SHORT).show();//如果两次按键时间间隔大于2秒，则不退出
                        Toast.makeText(this, "再按一次退出玉盘", Toast.LENGTH_SHORT).show();
                        firstTime = secondTime;//更新firstTime
                        return true;
                    } else {
                       // Toast.makeText(UserActivity.this,"444444",Toast.LENGTH_SHORT).show();//两次按键小于2秒时，退出应用
                        System.exit(0);
                    }
               // }else {
                   // int index=allpath.lastIndexOf("/");
                   // if (index>0){
                    //    allpath=allpath.substring(0,index);
                    //    menuflag=0;
                    //    showFileList();
                   // }


                //}


                break;
        }
        return super.onKeyUp(keyCode, event);
    }




    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater=new MenuInflater(this);
        long size1=sizes[listposition];

        if (menuflag==0){
            if(size1>=0){
                inflater.inflate(R.menu.listcontextmenu,menu);
            }else{
                inflater.inflate(R.menu.listcontextmenu1,menu);
            }
        }else {
            long size2=sizenew[listposition];
            if(size2>=0){
                inflater.inflate(R.menu.listcontextmenu,menu);
            }else{
                inflater.inflate(R.menu.listcontextmenu1,menu);
            }
        }


        menu.setHeaderIcon(R.drawable.ic_gongneng);
        menu.setHeaderTitle("功能");
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.list_menu_download:
                movebtn.setEnabled(false);
                new AsyncTask<Void, Void, Boolean>(){

                    @Override
                    protected Boolean doInBackground(Void... params) {//工作线程执行
                        //moveflag=false;
                        String name;
                        if(menuflag==0){
                             name=names[listposition];

                        }else {
                            name=namenew[listposition];
                        }

                        //得到文件名
                        //开始下载文件
                        try {
                            Log.i("download","连接");
                             ds=new Socket(ip,port);
                            Log.i("download","已经连接");
                            Request req=new Request();
//					req.setWhat(Request.WHAT_DOWNLOAD);
                            req.setWhat(What.DOWNLOAD);//此处使用枚举
                            req.setDownloadfilepath(allpath+"/"+name);
                            req.setFilename(name);
                           // req.setFilepath();
                            Log.i("download","发送Request");
                            ObjectOutputStream out=new ObjectOutputStream(ds.getOutputStream());
                            out.writeObject(req);
                            out.flush();
                            abc+="a1";
                            Log.i("download","Request已经发送");
                            Log.i("download","接收Response");
                            ObjectInputStream in=new ObjectInputStream(ds.getInputStream());
                            Response resp=(Response) in.readObject();
                            Log.i("download","Response已经接收");
                            name=resp.getName();
                            long size=resp.getSize();
                            abc+="a2";
                           // long lm=resp.getLastModify();
                            Log.i("download","文件名"+name);
                            Log.i("download","字节量"+size);
                           // Log.i("download","修改时间"+lm);


                            Log.i("download","开始接收文件内容");

                            File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                            //String dirnew=dir.toString().replace("/storage/emulated/0","/sdcard");

                            Log.e("##############：",dir.toString());
                            Log.e("3231231",name);
                            File file=new File("/sdcard/Download/",name);
                            abc+="a3";
                            RandomAccessFile raf=new RandomAccessFile(file,"rw");
                            raf.setLength(size);//会立即分配空间,如果空间不足，会出异常

                            InputStream sin=ds.getInputStream();
                            byte[] buf=new byte[8192];
                            int n;
                            abc+="a4";
                            long count=0;//已经下载的字节量
                            while((n=sin.read(buf))!=-1&&dowflag==0){
                                raf.write(buf,0,n);
                                count+=n;
                                int x=(int) (100*count/size);
                                String percent=x+"";
                                downloadview(x,name,percent);
                                //如果文件很大，不必要每次都发布进度
                                if(count>=size){
                                    break;
                                }

                            }
                            raf.close();
                            abc+="a5";
                            //如果文件大小为0，则会出现进度停在0，不会更新到100的情况，所以，增加下面的语句

                            Log.i("download","文件接收完成");
                            Log.i("download","断开");
                            ds.close();

                            return true;
                        } catch (Exception e) {

                            Log.e("download","文件下载失败",e);
                            abc+="a6"+e.toString();
                            return false;
                        }
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (dowflag==0){
                            if(!result){
                                Toast.makeText(UserActivity.this, "下载失败", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(UserActivity.this, "下载完成", Toast.LENGTH_SHORT).show();

                            }
                        }else{

                            try{
                                ds.close();
                            }catch (Exception e){

                            }
                            notifiM.cancel(1);
                            dowflag=0;
                            if (menuflag==0){
                                File file=new File("/sdcard/Download/",names[listposition]);
                                file.delete();
                            }else {
                                File file=new File("/sdcard/Download/",namenew[listposition]);
                                file.delete();
                            }

                            Toast.makeText(UserActivity.this, "下载已终止", Toast.LENGTH_SHORT).show();


                        }

                    }

                }.execute();
                break;
            case R.id.list_menu_delete:
                Intent intent = getIntent();
              //  download_usname=intent.getStringExtra("name");
               // moveflag=false;
                movebtn.setEnabled(false);
                new AsyncTask<Void, Void, Response>(){


                    @Override
                    protected Response doInBackground(Void... params) {//工作线程执行
                            //String ip=UserActivity.this.getString(R.string.ipadd);
                        String download_getfilename;
                        if (menuflag==0){
                             download_getfilename=names[listposition];
                        }else {
                            download_getfilename=namenew[listposition];
                        }

                        try {
                            Log.i("download","开始连接");
                            Socket s=new Socket(ip,port);
                            Log.i("download","已经连接");
                            Request r=new Request();
//					r.setWhat(Request.WHAT_LIST);
                            r.setWhat(What.DELETE);//此处使用枚举
                            r.setFilename(allpath+"/"+download_getfilename);

                            Log.i("download","发送Request");
                            ObjectOutputStream out=new ObjectOutputStream(s.getOutputStream());
                            out.writeObject(r);
                            out.flush();
                            Log.i("download","Request已经发送");
                            Log.i("download","接收Response");
                            ObjectInputStream in=new ObjectInputStream(s.getInputStream());
                            Response resp=(Response) in.readObject();
                            Log.i("download","Response已经接收");
                            Log.i("download","names:"+ Arrays.toString(resp.getNames()));
                            Log.i("download","sizes:"+Arrays.toString(resp.getSizes()));

                            return resp;//发到主线程，然后转到onPostExecute
                        } catch (Exception e) {
                            // TODO: handle exception
                            Log.e("download","获得列表出错",e);

                            return null;
                        }

                    }

                    @Override
                    protected void onPostExecute(Response result) {//在主线程执行

                        if(result==null){
                            Toast.makeText(UserActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        String name = result.getName();

                        if(name.equals("chenggong")){
                            Log.e("1111111111",name);
                            Toast.makeText(UserActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            menuflag=0;
                            showFileList();


                        }else{
                            Toast.makeText(UserActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                            menuflag=0;
                            showFileList();

                        }

                    }
                }.execute();
                break;
            case R.id.list_menu_rename:
                movebtn.setEnabled(false);
                        rename();
                       break;
            case R.id.list_menu_move:
               // movebtn.setVisibility(View.VISIBLE);
                move();
                break;


        }

        return super.onContextItemSelected(item);
    }

    private void moveto() {
        movebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    movebtn.setEnabled(false);
                    new AsyncTask<Void, Void, Response>(){
                        // String download_getfilename=names[listposition];

                        @Override
                        protected Response doInBackground(Void... params) {//工作线程执行
                            //String ip=UserActivity.this.getString(R.string.ipadd);


                            try {
                                Log.i("download","开始连接");
                                Socket s=new Socket(ip,port);
                                Log.i("download","已经连接");
                                Request r=new Request();
//					r.setWhat(Request.WHAT_LIST);
                                r.setWhat(What.MOVE);//此处使用枚举
                                r.setMovepath(oldpath);
                                r.setMovenewpath(allpath+"/"+oldfile);

                                Log.i("download","发送Request");
                                ObjectOutputStream out=new ObjectOutputStream(s.getOutputStream());
                                out.writeObject(r);
                                out.flush();
                                Log.i("download","Request已经发送");
                                Log.i("download","接收Response");
                                ObjectInputStream in=new ObjectInputStream(s.getInputStream());
                                Response resp=(Response) in.readObject();
                                Log.i("download","Response已经接收");
                                Log.i("download","names:"+ Arrays.toString(resp.getNames()));
                                Log.i("download","sizes:"+Arrays.toString(resp.getSizes()));

                                return resp;//发到主线程，然后转到onPostExecute
                            } catch (Exception e) {
                                // TODO: handle exception
                                Log.e("download","获得列表出错",e);

                                return null;
                            }

                        }

                        @Override
                        protected void onPostExecute(Response result) {//在主线程执行

                            if(result==null){
                                Toast.makeText(UserActivity.this, "移动失败", Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }

                            String name = result.getName();

                            if(name.equals("chenggong")){
                                Log.e("1111111111",name);
                                Toast.makeText(UserActivity.this, "移动成功", Toast.LENGTH_SHORT).show();
                                menuflag=0;
                                showFileList();


                            }else{
                                Toast.makeText(UserActivity.this, "移动失败", Toast.LENGTH_SHORT).show();
                                menuflag=0;
                                showFileList();

                            }

                        }
                    }.execute();


                   // movebtn.setEnabled(false);
                    //Toast.makeText(UserActivity.this,"请选择文件移动！",Toast.LENGTH_SHORT).show();



            }
        });


    }

    private void move() {
        String selectfilename;
        String filepath;
                if (menuflag==0){
                    selectfilename=names[listposition];
                    filepath=allpath+"/"+selectfilename;
                    oldpath=filepath;
                    oldfile=names[listposition];
                    //   Toast.makeText(UserActivity.this,oldpath+"-------"+oldfile,Toast.LENGTH_LONG).show();
                    movebtn.setEnabled(true);
                }else {
                    selectfilename=namenew[listposition];
                    filepath=allpath+"/"+selectfilename;
                    oldpath=filepath;
                    oldfile=namenew[listposition];
                    //   Toast.makeText(UserActivity.this,oldpath+"-------"+oldfile,Toast.LENGTH_LONG).show();
                    movebtn.setEnabled(true);
                }



    }


    //上传按钮监听
    private void upload(){
        uplaod=(FloatingActionButton) findViewById(R.id.user_upload);
        uplaod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);//调用手机文件管理器
                startActivityForResult(intent,1);
              //  moveflag=false;
                movebtn.setEnabled(false);

            }

        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO Auto-generated method stub
        if (resultCode == Activity.RESULT_OK) {
            // Get the Uri of the selected file

            Uri uri = data.getData();
           // final String url=uri.getEncodedPath();
//            String [] proj={MediaStore.Images.Media.DATA};
//            Cursor actualimagecursor=managedQuery(uri,proj,null,null,null);
//            int actual_image_column_index=actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            actualimagecursor.moveToFirst();
//            String img_path=actualimagecursor.getString(actual_image_column_index);

            String url2=FileChooseractivity.getPath(UserActivity.this,uri);
           // Toast.makeText(UserActivity.this,url2,Toast.LENGTH_SHORT).show();
        //Toast.makeText(UserActivity.this,"URL2:"+url2,Toast.LENGTH_LONG).show();

           // String name=getFileName(url);

            final String nameall=getFileNametye(url2);
            final File file=new File(url2);
            //Toast.makeText(UserActivity.this, url, Toast.LENGTH_SHORT).show();//打印文件路径
           // Toast.makeText(UserActivity.this, name, Toast.LENGTH_SHORT).show();//打印仅文件名
          //  Toast.makeText(UserActivity.this, nameall, Toast.LENGTH_SHORT).show();//打印含扩展名的文件名
            //Toast.makeText(UserActivity.this,FileSizeUtil.getAutoFileOrFilesSize(url) , Toast.LENGTH_SHORT).show();//打印文件大小
           // final String[] arr=new String[]{"192.168.1.101","8000"};
            new AsyncTask<Void, Void, Response>(){

                @Override
                protected Response doInBackground(Void... params) {//工作线程执行

                    try {

                        File f=new File(file.getAbsolutePath());

                        if(f.isDirectory()){
                            //Toast.makeText(UploadActivity.this, "请选择文件上传", Toast.LENGTH_SHORT).show();
                            return null;
                        }


                        Log.i("download","开始连接");
                         us=new Socket(ip,port);
                        Log.i("download","已经连接");
                        Request r=new Request();
//					r.setWhat(Request.WHAT_LIST);
                        r.setWhat(What.UPLOAD);//此处使用枚举
                        String path=allpath+"/"+nameall;
                        r.setUploadfilename(path);
                      // r.setFilename(nameall);
                        Log.e("2131231",path);
                        Log.e("2131231",nameall);
                       // Log.i("font:",nameall);


                        long size=f.length();
                        r.setSize(size);
                        Log.i("download","发送Request");
                        ObjectOutputStream out1=new ObjectOutputStream(us.getOutputStream());
                        out1.writeObject(r);
                        out1.flush();




                        Log.i("download","Request已经发送");
                        Log.i("download","接收Response");

                        Response resp=new Response();
                        resp.setName("chenggong");

                        FileInputStream fis=new FileInputStream(f);
                        byte[] buf=new byte[8192];
                        int n;
                        long count=0;
                        OutputStream out=us.getOutputStream();

                        while((n=fis.read(buf))!=-1&& upflag==0){
                            out.write(buf,0,n);
                            count+=n;
                            int x=(int) (100*count/size);
                            String percent=x+"";
                            uploadview(x,nameall,percent);
                        }
                        out.flush();
                        fis.close();

                        return resp;//发到主线程，然后转到onPostExecute
                    } catch (Exception e) {
                        // TODO: handle exception
                        Log.e("download","获得列表出错",e);

                        return null;
                    }

                }

                @Override
                protected void onPostExecute(Response result) {//在主线程执行

                    if(result==null){
                        Toast.makeText(UserActivity.this, "文件上传失败,请选择文件进行上传", Toast.LENGTH_SHORT).show();

                        //finish();
                        return;
                    }


                    String name = result.getName();
                    long size = result.getSize();
                    if (upflag==0){
                        if(name.equals("chenggong")){
                            Toast.makeText(UserActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                            menuflag=0;
                            showFileList();
                        }else{
                            Toast.makeText(UserActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                            menuflag=0;
                            showFileList();
                        }
                    }else{
                        try{
                            us.close();
                        }catch (Exception e){

                        }

                        Toast.makeText(UserActivity.this, "上传已终止", Toast.LENGTH_SHORT).show();
                        notifiM.cancel(2);
                        upflag=0;


                    }



                }
            }.execute();

//
        }


    }
    private void uploadview(int prog,String filename,String filepercent){
        int count=2;
        String ns=Context.NOTIFICATION_SERVICE;
         notifiM=(NotificationManager) getSystemService(ns);


        int icon=R.drawable.ic_launcher;
        String ticker="正在上传...";
        Long when=System.currentTimeMillis();
        Notification notifi=new Notification(icon, ticker, when);

        //添加声音

        // notifi.defaults |=Notification.DEFAULT_SOUND;

        //添加振动

        //  notifi.defaults |= Notification.DEFAULT_VIBRATE;

        //添加LED灯提醒

        notifi.defaults |= Notification.DEFAULT_LIGHTS;

        Context context=getApplicationContext();
        String title="title"+count;
        String text="text"+count;
        //Intent notifiIntentUri=new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com"));
        // Intent notifiIntent = new Intent(context, UserActivity.class); //点击该通知后要跳转的Activity
        //notifiIntent.setData(Uri.parse("http://www.baidu.com"));
        // notifiIntent.setDataAndType(Uri.parse("http://www.baidu.com"), count+"");
        //PendingIntent pintent=PendingIntent.getActivity(context, 0, notifiIntent, 0);

//				notifi.setLatestEventInfo(context, title, text, pintent);
        //notifi.contentIntent=pintent;
        RemoteViews  cvs=new RemoteViews(getApplicationContext().getPackageName(), R.layout.activity_uploadview);
        // prog+=10;
        Intent notifiIntentnew=new Intent(context,noticesupport.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,notifiIntentnew,0);

        cvs.setProgressBar(R.id.notice_up_pro, 100, prog, false);
        cvs.setTextViewText(R.id.notice_up_title,filename);
        cvs.setTextViewText(R.id.notice_up_prse,filepercent+"%");
        cvs.setOnClickPendingIntent(R.id.notice_up_btn,pendingIntent);



        //cvs.setImageViewResource(1,R.drawable.notice_download);

        //Icon ic=new Icon(R.drawable.notice_download);
        // cvs.setImageViewIcon(R);
        if(prog>=100){

            notifiM.cancel(count);
        }else{
            notifi.contentView=cvs;
            notifiM.notify(count, notifi);

        }

    }
    private void downloadview(int prog,String filename,String filepercent){

        int count=1;
        String ns=Context.NOTIFICATION_SERVICE;
         notifiM=(NotificationManager) getSystemService(ns);

        int icon=R.drawable.ic_launcher;
        String ticker="正在下载...";
        Long when=System.currentTimeMillis();
        Notification notifi=new Notification(icon, ticker, when);

        //添加声音

       // notifi.defaults |=Notification.DEFAULT_SOUND;

        //添加振动

      //  notifi.defaults |= Notification.DEFAULT_VIBRATE;

        //添加LED灯提醒

        notifi.defaults |= Notification.DEFAULT_LIGHTS;

        Context context=getApplicationContext();
        String title="title"+count;
        String text="text"+count;
        //Intent notifiIntentUri=new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com"));
       // Intent notifiIntent = new Intent(context, UserActivity.class); //点击该通知后要跳转的Activity
        //notifiIntent.setData(Uri.parse("http://www.baidu.com"));
       // notifiIntent.setDataAndType(Uri.parse("http://www.baidu.com"), count+"");
        //PendingIntent pintent=PendingIntent.getActivity(context, 0, notifiIntent, 0);

//				notifi.setLatestEventInfo(context, title, text, pintent);
        //notifi.contentIntent=pintent;
        RemoteViews  cvs=new RemoteViews(getApplicationContext().getPackageName(), R.layout.activity_notice);
       // prog+=10;
        Intent noifiIntent=new Intent(context,downloadsupport.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,noifiIntent,0);
        cvs.setProgressBar(R.id.notice_dow_pro, 100, prog, false);
        cvs.setTextViewText(R.id.notice_dow_title,filename);
        cvs.setTextViewText(R.id.notice_dow_prse,filepercent+"%");
        cvs.setOnClickPendingIntent(R.id.notice_dow_btn,pendingIntent);
        //cvs.setImageViewResource(1,R.drawable.notice_download);

        //Icon ic=new Icon(R.drawable.notice_download);
       // cvs.setImageViewIcon(R);
        if(prog>=100){
            notifiM.cancel(count);
        }else{
            notifi.contentView=cvs;
            notifiM.notify(count, notifi);

        }

    }
    //搜索按钮的监听
    private void search(){
        search=(FloatingActionButton)findViewById(R.id.user_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchDialog();
             //   moveflag=false;
                movebtn.setEnabled(false);
            }
        });

    }
    //弹出搜索框
    private void searchDialog() {
        movebtn.setEnabled(false);
        final EditText inputServer = new EditText(UserActivity.this);
        inputServer.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        builder.setTitle(getString(R.string.user_search_title)).setIcon(
                R.drawable.ic_float_search).setView(inputServer).setNegativeButton(
                getString(R.string.user_search_no), null);
        builder.setPositiveButton(getString(R.string.user_search_yes),

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        menuflag=1;
                        String inputName = inputServer.getText().toString();
                        UserActivity.this.setTitle(R.string.user_search_title);
                        if(inputName.equals("")){
                            Toast.makeText(UserActivity.this,"搜索内容不能为空！",Toast.LENGTH_SHORT).show();
                        }else {
                            String [] docname=new String[names.length];
                            String [] doctime=new String[names.length];
                            long[]docsize=new long[names.length];
                            String [] doctype=new String[names.length];
                            int count=0;
                            int search;
                            Log.e("doc:","222222");
                            for (int i=0;i<names.length;i++){
                                search=names[i].indexOf(inputName);
                                if(search>-1){
                                    docname[count]=names[i];
                                    docsize[count]=sizes[i];
                                    doctime[count]=times[i];
                                    doctype[count]=types[i];
                                    count++;

                                }
                            }
                            if(count==0){
                                Toast.makeText(UserActivity.this,"没有搜索到文件",Toast.LENGTH_SHORT).show();
                            }
                            Log.e("doc:","333333");
                             namenew=new String[count];
                             timenew=new String[count];
                             sizenew=new long[count];
                             typenew=new String[count];
                            Log.e("doc:","4444444");
                            for (int j=0;j<count;j++){

                                    namenew[j] = docname[j];
                                    timenew[j] = doctime[j];
                                    sizenew[j] = docsize[j];
                                    typenew[j] = doctype[j];


                            }
                            Log.e("doc:","5555555");

                            FileListAdapter adapter=new FileListAdapter(
                                    UserActivity.this,
                                    R.layout.activity_filelistinfo,
                                    namenew,
                                    sizenew,
                                    timenew,
                                    typenew,
                                    ip,
                                    port);
                            Log.e("doc:","66666666");
                            lv.setAdapter(adapter);

                        }

                    }
                });
        builder.show();
    }
    private void rename(){
        movebtn.setEnabled(false);
        final EditText inputServer = new EditText(UserActivity.this);
        int check=names[listposition].lastIndexOf(".");

        if (menuflag==0){
            if (check>0){
                inputServer.setText(names[listposition].substring(0,names[listposition].lastIndexOf(".")));
            }else {
                inputServer.setText(names[listposition]);
            }
        }else {
            int checknew=namenew[listposition].lastIndexOf(".");
            if (checknew>0){
                inputServer.setText(namenew[listposition].substring(0,namenew[listposition].lastIndexOf(".")));
            }else {
                inputServer.setText(namenew[listposition]);
            }

        }


        inputServer.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        builder.setTitle(getString(R.string.user_rename)).setIcon(
                R.drawable.ic_user_rename).setView(inputServer).setNegativeButton(
                getString(R.string.user_search_no), null);
        builder.setPositiveButton(getString(R.string.user_search_yes),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        if (menuflag==0){
                            int check=names[listposition].lastIndexOf(".");

                            if(check>0){
                                renamecheck=names[listposition].substring(0,names[listposition].lastIndexOf("."));
                            }else{
                                renamecheck=names[listposition];
                            }
                        }else {
                            int newcheck=namenew[listposition].lastIndexOf(".");

                            if(newcheck>0){
                                renamecheck=namenew[listposition].substring(0,namenew[listposition].lastIndexOf("."));
                            }else{
                                renamecheck=namenew[listposition];
                            }
                        }


                       // final String filename=inputServer.getText().toString().substring(0,inputServer.getText().toString().lastIndexOf("."));
                        //final String filetye=names[listposition].substring(names[listposition].indexOf("."));
                        //Toast.makeText(UserActivity.this,sname,Toast.LENGTH_SHORT).show();
                       // Toast.makeText(UserActivity.this,filename,Toast.LENGTH_SHORT).show();
                     //  Toast.makeText(UserActivity.this,filetye,Toast.LENGTH_SHORT).show();
                        if (!inputServer.equals(renamecheck)) {


                        final String inputName = inputServer.getText().toString();

                        // Intent intent = getIntent();
                        //  download_usname=intent.getStringExtra("name");
                        // moveflag=false;
                        new AsyncTask<Void, Void, Response>() {



                            @Override
                            protected Response doInBackground(Void... params) {//工作线程执行
                                //String ip=UserActivity.this.getString(R.string.ipadd);
                                String name;
                                if (menuflag==0){
                                    name=names[listposition];
                                }else {
                                    name=namenew[listposition];
                                }


                                try {
                                    Log.i("download", "开始连接");
                                    Socket s = new Socket(ip, port);
                                    Log.i("download", "已经连接");
                                    Request r = new Request();
//					r.setWhat(Request.WHAT_LIST);
                                    r.setWhat(What.RENAME);//此处使用枚举
                                    r.setRename(allpath + "/" + inputName);
                                    r.setRenamepth(allpath + "/" + name);

                                    Log.i("download", "发送Request");
                                    ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                                    out.writeObject(r);
                                    out.flush();
                                    Log.i("download", "Request已经发送");
                                    Log.i("download", "接收Response");
                                    ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                                    Response resp = (Response) in.readObject();
                                    Log.i("download", "Response已经接收");
                                    Log.i("download", "names:" + Arrays.toString(resp.getNames()));
                                    Log.i("download", "sizes:" + Arrays.toString(resp.getSizes()));

                                    return resp;//发到主线程，然后转到onPostExecute
                                } catch (Exception e) {
                                    // TODO: handle exception
                                    Log.e("download", "获得列表出错", e);

                                    return null;
                                }

                            }

                            @Override
                            protected void onPostExecute(Response result) {//在主线程执行

                                if (result == null) {
                                    Toast.makeText(UserActivity.this, "重命名失败", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }

                                String name = result.getName();

                                if (name.equals("chenggong")) {
                                    Log.e("1111111111", name);
                                    Toast.makeText(UserActivity.this, "重命名成功", Toast.LENGTH_SHORT).show();
                                    menuflag=0;
                                    showFileList();

                                } else {
                                    Toast.makeText(UserActivity.this, "重命名失败", Toast.LENGTH_SHORT).show();
                                    menuflag=0;
                                    showFileList();
                                }

                            }
                        }.execute();
                    }else {

                        }

                    }
                });
        builder.show();

    }
    private void back(){
        backbtn=(FloatingActionButton)findViewById(R.id.user_back);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int index=allpath.lastIndexOf("/");
                if (index>0){
                    allpath=allpath.substring(0,index);
                    menuflag=0;
                    showFileList();
                }


                //Log.e("PAth",allpath);


            }
        });
    }

//新建文件夹按钮的监听事件
    private void New(){
        newdir=(FloatingActionButton)findViewById(R.id.user_newdir);
        newdir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFileDialog();
            }
        });
    }
    //新建文件夹的过程
    private void newFileDialog() {
        movebtn.setEnabled(false);
        final EditText inputServer = new EditText(UserActivity.this);
        inputServer.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
        builder.setTitle(getString(R.string.user_newdilog)).setIcon(
                R.drawable.ic_float_new).setView(inputServer).setNegativeButton(
                getString(R.string.user_search_no), null);
        builder.setPositiveButton(getString(R.string.user_search_yes),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        final String inputName = inputServer.getText().toString();
                      //  moveflag=false;
                        movebtn.setEnabled(false);
                        if(inputName.equals("")){
                            Toast.makeText(UserActivity.this,"新建文件夹命名不能为空",Toast.LENGTH_SHORT).show();

                        }else {
                            new AsyncTask<Void, Void, Response>(){

                                @Override
                                protected Response doInBackground(Void... params) {//工作线程执行
                                    //String ip=UserActivity.this.getString(R.string.ipadd);
                                    try {
                                        Log.i("download","开始连接");
                                        Socket s=new Socket(ip,port);
                                        Log.i("download","已经连接");
                                        Request r=new Request();
//					r.setWhat(Request.WHAT_LIST);
                                        r.setWhat(What.CREATEDIR);//此处使用枚举
                                        r.setFilename("/"+inputName);
                                        r.setFilepath(allpath);
                                        Log.i("download","发送Request");
                                        ObjectOutputStream out=new ObjectOutputStream(s.getOutputStream());
                                        out.writeObject(r);
                                        out.flush();
                                        Log.i("download","Request已经发送");
                                        Log.i("download","接收Response");
                                        ObjectInputStream in=new ObjectInputStream(s.getInputStream());
                                        Response resp=(Response) in.readObject();
                                        Log.i("download","Response已经接收");
                                        Log.i("download","names:"+ Arrays.toString(resp.getNames()));
                                        Log.i("download","sizes:"+Arrays.toString(resp.getSizes()));

                                        return resp;//发到主线程，然后转到onPostExecute
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                        Log.e("download","获得列表出错",e);

                                        return null;
                                    }

                                }

                                @Override
                                protected void onPostExecute(Response result) {//在主线程执行

                                    if(result==null){
                                        Toast.makeText(UserActivity.this, "新建失败", Toast.LENGTH_SHORT).show();
                                        finish();
                                        return;
                                    }

                                    String name = result.getName();

                                    if(name.equals("chenggong")){
                                        Log.e("1111111111",name);
                                        Toast.makeText(UserActivity.this, "新建成功", Toast.LENGTH_SHORT).show();
                                        menuflag=0;
                                        showFileList();

                                    }else{
                                        Toast.makeText(UserActivity.this, "新建失败", Toast.LENGTH_SHORT).show();
                                        menuflag=0;
                                        showFileList();
                                    }

                                }
                            }.execute();

                        }


                    }
                });
        builder.show();
    }
    //拆分文件名
    public String getFileName(String pathandname){
        int start=pathandname.lastIndexOf("/");
        int end=pathandname.lastIndexOf(".");
        if (start!=-1 && end!=-1) {
            return pathandname.substring(start+1, end);
        }
        else {
            return null;
        }
    }
    //拆分代文件扩展名的文件名
    public String getFileNametye(String nametye){

            String tye=nametye.substring(nametye.lastIndexOf("/") + 1, nametye.length());
            return tye;


    }


//上传过程




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }







    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    //导航菜单动作
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        setTitle(item.getTitle());
        int id = item.getItemId();

        if (id == R.id.nav_all) {

            //moveflag=false;
            fab.show(true);
            menuflag=0;
            showFileList();

        } else if (id == R.id.nav_document) {
          //  moveflag=false;
           // movebtn.setEnabled(false);
            menuflag=1;
            fab.show(false);
            Log.e("doc:","111111");
            String [] docname=new String[names.length];
            String [] doctime=new String[names.length];
            long[]docsize=new long[names.length];
            String [] doctype=new String[names.length];
            int count=0;
            Log.e("doc:","222222");
            for (int i=0;i<names.length;i++){
                if(types[i].equals("docx")||types[i].equals("doc")||types[i].equals("txt")||types[i].equals("TXT")||types[i].equals("pdf")||types[i].equals("pptx")||types[i].equals("xlsx")){
                    docname[count]=names[i];
                    docsize[count]=sizes[i];
                    doctime[count]=times[i];
                    doctype[count]=types[i];
                    count++;

                }
            }
            Log.e("doc:","333333");
             namenew=new String[count];
             timenew=new String[count];
             sizenew=new long[count];
             typenew=new String[count];
            Log.e("doc:","4444444");
            for (int j=0;j<count;j++){
                namenew[j]=docname[j];
                timenew[j]=doctime[j];
                sizenew[j]=docsize[j];
                typenew[j]=doctype[j];

            }
            Log.e("doc:","5555555");

            FileListAdapter adapter=new FileListAdapter(
                    UserActivity.this,
                    R.layout.activity_filelistinfo,
                    namenew,
                    sizenew,
                    timenew,
                    typenew,
                    ip,
                    port);
            Log.e("doc:","66666666");
            lv.setAdapter(adapter);

            fab.isShown();

        } else if (id == R.id.nav_gallery) {
           // movebtn.setEnabled(false);
          //  moveflag=false;
            menuflag=1;
            fab.show(false);
            Log.e("doc:","111111");
            String [] docname=new String[names.length];
            String [] doctime=new String[names.length];
            long[]docsize=new long[names.length];
            String [] doctype=new String[names.length];
            int count=0;
            Log.e("doc:","222222");
            for (int i=0;i<names.length;i++){
                if(types[i].equals("jpg")||types[i].equals("png")||types[i].equals("JPG")||types[i].equals("PNG")||types[i].equals("gif")){
                    docname[count]=names[i];
                    docsize[count]=sizes[i];
                    doctime[count]=times[i];
                    doctype[count]=types[i];
                    count++;

                }
            }
            Log.e("doc:","333333");
             namenew=new String[count];
             timenew=new String[count];
             sizenew=new long[count];
             typenew=new String[count];
            Log.e("doc:","4444444");
            for (int j=0;j<count;j++){
                namenew[j]=docname[j];
                timenew[j]=doctime[j];
                sizenew[j]=docsize[j];
                typenew[j]=doctype[j];

            }
            Log.e("doc:","5555555");

            FileListAdapter adapter=new FileListAdapter(
                    UserActivity.this,
                    R.layout.activity_filelistinfo,
                    namenew,
                    sizenew,
                    timenew,
                    typenew,
                    ip,
                    port);
            Log.e("doc:","66666666");
            lv.setAdapter(adapter);



        } else if (id == R.id.nav_music) {
           // movebtn.setEnabled(false);
         //   moveflag=false;
            fab.show(false);
            Log.e("doc:","111111");
            String [] docname=new String[names.length];
            String [] doctime=new String[names.length];
            long[]docsize=new long[names.length];
            String [] doctype=new String[names.length];
            int count=0;
            Log.e("doc:","222222");
            for (int i=0;i<names.length;i++){
                if(types[i].equals("mp3")||types[i].equals("ape")||types[i].equals("flac")||types[i].equals("wma")||types[i].equals("aac")){
                    docname[count]=names[i];
                    docsize[count]=sizes[i];
                    doctime[count]=times[i];
                    doctype[count]=types[i];
                    count++;

                }
            }
            Log.e("doc:","333333");
             namenew=new String[count];
             timenew=new String[count];
             sizenew=new long[count];
             typenew=new String[count];
            Log.e("doc:","4444444");
            for (int j=0;j<count;j++){
                namenew[j]=docname[j];
                timenew[j]=doctime[j];
                sizenew[j]=docsize[j];
                typenew[j]=doctype[j];

            }
            Log.e("doc:","5555555");

            FileListAdapter adapter=new FileListAdapter(
                    UserActivity.this,
                    R.layout.activity_filelistinfo,
                    namenew,
                    sizenew,
                    timenew,
                    typenew,
                    ip,
                    port);
            Log.e("doc:","66666666");
            lv.setAdapter(adapter);


        } else if (id == R.id.nav_video) {
           // movebtn.setEnabled(false);
           // moveflag=false;
            menuflag=1;
            fab.show(false);
            Log.e("doc:","111111");
            String [] docname=new String[names.length];
            String [] doctime=new String[names.length];
            long[]docsize=new long[names.length];
            String [] doctype=new String[names.length];
            int count=0;
            Log.e("doc:","222222");
            for (int i=0;i<names.length;i++){
                if(types[i].equals("mp4")||types[i].equals("3GP")||types[i].equals("avi")||types[i].equals("wmv")||types[i].equals("mov")){
                    docname[count]=names[i];
                    docsize[count]=sizes[i];
                    doctime[count]=times[i];
                    doctype[count]=types[i];
                    count++;

                }
            }
            Log.e("doc:","333333");
             namenew=new String[count];
             timenew=new String[count];
             sizenew=new long[count];
             typenew=new String[count];
            Log.e("doc:","4444444");
            for (int j=0;j<count;j++){
                namenew[j]=docname[j];
                timenew[j]=doctime[j];
                sizenew[j]=docsize[j];
                typenew[j]=doctype[j];

            }
            Log.e("doc:","5555555");

            FileListAdapter adapter=new FileListAdapter(
                    UserActivity.this,
                    R.layout.activity_filelistinfo,
                    namenew,
                    sizenew,
                    timenew,
                    typenew,
                    ip,
                    port);
            Log.e("doc:","66666666");
            lv.setAdapter(adapter);


        } else if (id == R.id.nav_zip) {
           // movebtn.setEnabled(false);
           // moveflag=false;
            menuflag=1;
            fab.show(false);
            Log.e("doc:","111111");
            String [] docname=new String[names.length];
            String [] doctime=new String[names.length];
            long[]docsize=new long[names.length];
            String [] doctype=new String[names.length];
            int count=0;
            Log.e("doc:","222222");
            for (int i=0;i<names.length;i++){
                if(types[i].equals("zip")||types[i].equals("7z")||types[i].equals("rar")||types[i].equals("iso")||types[i].equals("img")){
                    docname[count]=names[i];
                    docsize[count]=sizes[i];
                    doctime[count]=times[i];
                    doctype[count]=types[i];
                    count++;

                }
            }
            Log.e("doc:","333333");
             namenew=new String[count];
             timenew=new String[count];
             sizenew=new long[count];
             typenew=new String[count];
            Log.e("doc:","4444444");
            for (int j=0;j<count;j++){
                namenew[j]=docname[j];
                timenew[j]=doctime[j];
                sizenew[j]=docsize[j];
                typenew[j]=doctype[j];

            }
            Log.e("doc:","5555555");

            FileListAdapter adapter=new FileListAdapter(
                    UserActivity.this,
                    R.layout.activity_filelistinfo,
                    namenew,
                    sizenew,
                    timenew,
                    typenew,
                    ip,
                    port);
            Log.e("doc:","66666666");
            lv.setAdapter(adapter);


        } else if (id == R.id.nav_other) {
           // movebtn.setEnabled(false);
           // moveflag=false;
            menuflag=1;
            fab.show(false);
            Log.e("doc:","111111");
            String [] docname=new String[names.length];
            String [] doctime=new String[names.length];
            long[]docsize=new long[names.length];
            String [] doctype=new String[names.length];
            int count=0;
            Log.e("doc:","222222");
            for (int i=0;i<names.length;i++){
                if(types[i].equals("db")||types[i].equals("")||types[i].equals("html")||types[i].equals("css")||types[i].equals("java")){
                    docname[count]=names[i];
                    docsize[count]=sizes[i];
                    doctime[count]=times[i];
                    doctype[count]=types[i];
                    count++;

                }
            }
            Log.e("doc:","333333");
             namenew=new String[count];
             timenew=new String[count];
             sizenew=new long[count];
             typenew=new String[count];
            Log.e("doc:","4444444");
            for (int j=0;j<count;j++){
                namenew[j]=docname[j];
                timenew[j]=doctime[j];
                sizenew[j]=docsize[j];
                typenew[j]=doctype[j];

            }
            Log.e("doc:","5555555");

            FileListAdapter adapter=new FileListAdapter(
                    UserActivity.this,
                    R.layout.activity_filelistinfo,
                    namenew,
                    sizenew,
                    timenew,
                    typenew,
                    ip,
                    port);
            Log.e("doc:","66666666");
            lv.setAdapter(adapter);



        }  else if (id == R.id.nav_exit) {
            //movebtn.setEnabled(false);
         //   moveflag=false;

            fab.show(false);
            Intent intent=new Intent(UserActivity.this,MainActivity.class);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    private void showFileList() {
        // TODO Auto-generated method stub
       // movebtn.setEnabled(false);
        new AsyncTask<Void, Void, Response>(){

            @Override
            protected Response doInBackground(Void... params) {//工作线程执行

                try {
                    Log.i("download","开始连接");
                    Socket s=new Socket(ip,port);
                    Log.i("download","已经连接");
                    Request r=new Request();
//					r.setWhat(Request.WHAT_LIST);
                    r.setWhat(What.LIST);//此处使用枚举
                    r.setName(name);

                    r.setFilepath(allpath);
                    r.setPassWord(passWord);
                    Log.i("download","发送Request");
                    ObjectOutputStream out=new ObjectOutputStream(s.getOutputStream());
                    out.writeObject(r);
                    out.flush();
                    Log.i("download","Request已经发送");
                    Log.i("download","接收Response");
                    ObjectInputStream in=new ObjectInputStream(s.getInputStream());
                    Response resp=(Response) in.readObject();
                    Log.i("download","Response已经接收");
                    Log.i("download","names:"+Arrays.toString(resp.getNames()));
                    Log.i("download","sizes:"+Arrays.toString(resp.getSizes()));
                    Log.i("download","sizes:"+Arrays.toString(resp.getFiletimes()));
                    Log.i("download","sizes:"+Arrays.toString(resp.getFileTypes()));
                    return resp;//发到主线程，然后转到onPostExecute
                } catch (Exception e) {
                    // TODO: handle exception
                    Log.e("download","获得列表出错",e);

                    return null;
                }

            }

            @Override
            protected void onPostExecute(Response result) {//在主线程执行
          try {
              pd.dismiss();
              pd=null;
          }catch (Exception e){

          }

                if(result==null){
                    Toast.makeText(UserActivity.this, "文件加载失败", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                 names = result.getNames();
                 sizes = result.getSizes();
                 times=result.getFiletimes();
                 types=result.getFileTypes();
                //在lv显示文件名和文件大小
                FileListAdapter adapter=new FileListAdapter(
                        UserActivity.this,
                        R.layout.activity_filelistinfo,
                        names,
                        sizes,
                        times,
                        types,
                        ip,
                        port);
                lv.setAdapter(adapter);

            }
        }.execute();

    }

    private void setData() {
        // TODO Auto-generated method stub
        Intent intent=getIntent();
        ip=intent.getStringExtra("ip");
        port=intent.getIntExtra("port",8000);
        name=intent.getStringExtra("name");
        passWord=intent.getStringExtra("passWord");

        Log.i("download", "ip:"+ip+",port:"+port);

    }
    private void setViews() {
        // TODO Auto-generated method stub
        lv=(ListView)findViewById(R.id.filelist);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                listposition=position;
                return false;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("hahahah",sizes[position]+"");
                if(sizes[position]<0){
                        allpath+="/"+names[position];

                    showFileList();
                }
            }
        });
        pd=new ProgressDialog(this);
        pd.setMessage("正在加载文件列表");
        pd.setCancelable(false);
        pd.show();
    }





























}
