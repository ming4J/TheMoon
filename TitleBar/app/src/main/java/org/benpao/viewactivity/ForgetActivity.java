package org.benpao.viewactivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.download.Request;
import com.download.Response;
import com.download.What;

import org.benpao.widget.TitleActivity;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import org.benpao.netcheck.netcheck;

/**
 * Created by shaom on 2016/9/22.
 */

public class ForgetActivity extends TitleActivity {
    private EditText forget_uname;
    private EditText forget_idnum;
    private Button forget_idmun_cheeck;
    private EditText forget_repass;
    private EditText Forget_repassre;
    private Button forget_forget;
    private String ip;
    private int port=8000;
    private long firstTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ip=getString(R.string.ipadd);
        setContentView(R.layout.activity_forgetpass);
        setTitle(R.string.forgetpass_title);
        showBackwardView(R.string.text_back,true);
        showForwardView(R.string.text_forward,false);
        setViews();
        setListeners();
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {                                         //如果两次按键时间间隔大于2秒，则不退出
                    Toast.makeText(this, "再按一次退出玉盘", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//更新firstTime
                    return true;
                } else {                                                    //两次按键小于2秒时，退出应用
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
    private void setViews(){
        forget_uname=(EditText)findViewById(R.id.forget_uname);
        forget_idnum=(EditText)findViewById(R.id.forget_idnum);
        forget_idmun_cheeck=(Button)findViewById(R.id.forget_check);
        forget_repass=(EditText)findViewById(R.id.forget_repass);
        Forget_repassre=(EditText)findViewById(R.id.forget_repassre);
        forget_forget=(Button)findViewById(R.id.forget_repassbtn);
        forget_repass.setVisibility(View.GONE);
        Forget_repassre.setVisibility(View.GONE);
        forget_forget.setVisibility(View.GONE);

    }
    private void setListeners(){
        forget_idmun_cheeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netcheck net=new netcheck();
                if (net.isNetworkConnected(ForgetActivity.this)){
                    if(forget_uname.getText().toString().equals("")||forget_idnum.getText().toString().equals("")||forget_idnum.getText().toString().length()!=6){
                        Toast.makeText(ForgetActivity.this,"验证信息填写错误！",Toast.LENGTH_SHORT).show();
                    }else {
                        check();
                    }
                }else {
                    Toast.makeText(ForgetActivity.this,"亲~你的网络已断开",Toast.LENGTH_SHORT).show();
                }


            }
        });
        forget_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netcheck net=new netcheck();

                if (net.isNetworkConnected(ForgetActivity.this)){
                    if(forget_repass.getText().toString().equals("")||Forget_repassre.getText().toString().equals("")||!forget_repass.getText().toString().equals(Forget_repassre.getText().toString())){
                        Toast.makeText(ForgetActivity.this,"重置密码填写有误！",Toast.LENGTH_SHORT).show();
                    }else{
                        rePassword();
                    }

                }else {
                    Toast.makeText(ForgetActivity.this,"亲~你的网络已断开",Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
    protected void check(){
       // Toast.makeText(ForgetActivity.this, "执行登录", Toast.LENGTH_SHORT).show();
       // final String [] arr={"192.168.1.101","8000"};
        final String name=forget_uname.getText().toString();
        final String idnum=forget_idnum.getText().toString();
        new AsyncTask<Void, Void, Response>(){

            @Override
            protected Response doInBackground(Void... params) {//工作线程执行

                try {
                    Log.i("download","开始连接");
                    Socket s=new Socket(ip,port);
                    Log.i("download","已经连接");
                    Request r=new Request();
//					r.setWhat(Request.WHAT_LIST);
                    r.setWhat(What.CHECK);//此处使用枚举
                    r.setName(name);
                    r.setIdNum(idnum);
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
                    Toast.makeText(ForgetActivity.this, "亲！你网断了~", Toast.LENGTH_SHORT).show();
                    return null;
                }

            }

            @Override
            protected void onPostExecute(Response result) {//在主线程执行

                if(result==null){
                    Toast.makeText(ForgetActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                String name = result.getName();
                long size = result.getSize();
                if(name.equals("find")){
                    forget_repass.setVisibility(View.VISIBLE);
                    Forget_repassre.setVisibility(View.VISIBLE);
                    forget_forget.setVisibility(View.VISIBLE);
                    //forget_uname.setVisibility(View.INVISIBLE);
                    //forget_idnum.setVisibility(View.INVISIBLE);
                    //forget_idmun_cheeck.setVisibility(View.INVISIBLE);

                    Toast.makeText(ForgetActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
//                    Intent intent=new Intent(ForgetActivity.this,UserActivity.class);
//                    intent.putExtra("ip", arr[0]);
//                    intent.putExtra("port", Integer.parseInt(arr[1]));
//                    intent.putExtra("name", name);
//                    intent.putExtra("idnum", idnum);
//
//                    startActivity(intent);


                }else{
                    Toast.makeText(ForgetActivity.this, "验证失败", Toast.LENGTH_SHORT).show();

                }

            }
        }.execute();
    }
    protected void rePassword() {
        //Toast.makeText(ForgetActivity.this, "执行repass", Toast.LENGTH_SHORT).show();
        //final String [] arr={"192.168.1.101","8000"};
        String checkrepass = forget_repass.getText().toString();
        String checkrepassre = Forget_repassre.getText().toString();
        if (!checkrepass.equals(checkrepassre)) {
            Toast.makeText(ForgetActivity.this, "两次密码输入不一致，请重新输入", Toast.LENGTH_LONG).show();
        }else{

        final String name = forget_uname.getText().toString();
        final String repassWord = Forget_repassre.getText().toString();
        new AsyncTask<Void, Void, Response>() {

            @Override
            protected Response doInBackground(Void... params) {//工作线程执行

                try {
                    Log.i("download", "开始连接");
                    Socket s = new Socket(ip, port);
                    Log.i("download", "已经连接");
                    Request r = new Request();
//					r.setWhat(Request.WHAT_LIST);
                    r.setWhat(What.REPASSWORD);//此处使用枚举
                    r.setName(name);
                    r.setRePassword(repassWord);
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
                    Toast.makeText(ForgetActivity.this, "亲！你网断了~", Toast.LENGTH_SHORT).show();

                    return null;
                }

            }

            @Override
            protected void onPostExecute(Response result) {//在主线程执行

                if (result == null) {
                    Toast.makeText(ForgetActivity.this, "重置密码失败", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                String name = result.getName();
                long size = result.getSize();
                if (name.equals("ok")) {


                    Toast.makeText(ForgetActivity.this, "重置密码成功", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(ForgetActivity.this,MainActivity.class);




                    startActivity(intent);
                    finish();
                    return;


                } else {
                    Toast.makeText(ForgetActivity.this, "重置密码失败", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        }.execute();
    }
    }


}

