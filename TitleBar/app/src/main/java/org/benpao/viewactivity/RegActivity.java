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
 * Created by shaom on 2016/9/20.
 */

public class RegActivity extends TitleActivity {
    private String[] servers;
    private EditText reg_uen;
    private EditText reg_pass;
    private  EditText reg_repass;
    private  EditText reg_idnm;
    private  Button reg_reg;
    private String checkpass;
    private  String checkrepass;
    private String ip;
    private  int port=8000;
    private long firstTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ip=this.getString(R.string.ipadd);
        setContentView(R.layout.activity_reg);
        setTitle(R.string.forward_title);
        showBackwardView(R.string.text_back,true);
        showForwardView(R.string.text_forward,false);
        //setData();
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
//    private void setData(){
//        String[] arr=getResources().getStringArray(R.array.servers);
//        this.servers=arr;
//
//    }
    private void setViews(){
        reg_uen=(EditText)findViewById(R.id.reg_edt_urn);
        reg_pass=(EditText)findViewById(R.id.reg_edt_pass);
        reg_repass=(EditText)findViewById(R.id.reg_edt_repass);
        reg_idnm=(EditText)findViewById(R.id.reg_edt_idnm);
        reg_reg=(Button)findViewById(R.id.reg_btn_reg);

    }
    private void setListeners(){
        reg_reg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkpass=reg_pass.getText().toString();
                checkrepass=reg_repass.getText().toString();
                netcheck net=new netcheck();
               // Toast.makeText(RegActivity.this,reg_idnm.getText().toString().length()+"",Toast.LENGTH_SHORT).show();
                if (net.isNetworkConnected(RegActivity.this)){
                    if (checkpass.equals(checkrepass)){
                        if(reg_uen.getText().toString().equals("")||reg_idnm.getText().toString().equals("")||reg_pass.getText().toString().equals("")||reg_repass.getText().toString().equals("")||reg_idnm.getText().toString().length()!=6||!reg_repass.getText().toString().equals(reg_pass.getText().toString())||reg_uen.getText().toString().length()!=11){
                            Toast.makeText(RegActivity.this,"注册信息填写错误",Toast.LENGTH_SHORT).show();
                        }else {
                            regist();
                        }

                        // Toast.makeText(RegActivity.this,"成功执行注册！",Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(RegActivity.this,"重复输入密码有误，请重新输入！",Toast.LENGTH_LONG).show();

                    }

                }else{
                    Toast.makeText(RegActivity.this,"亲~你的网络已断开",Toast.LENGTH_SHORT).show();
                }


            }
        });
        reg_idnm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                checkpass=reg_pass.getText().toString();
                checkrepass=reg_repass.getText().toString();
                if (checkpass.equals(checkrepass)){
                    Toast.makeText(RegActivity.this,"重复输入密码正确，请继续！",Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(RegActivity.this,"重复输入密码有误，请重新输入！",Toast.LENGTH_LONG).show();

                }
            }
        });
    }
    protected void regist(){
         //final String[] arr=new String[]{ip,port};
        //System.out.println(arr[0]+"------"+arr[1]);
        final String name=reg_uen.getText().toString();
        final String passWord=reg_pass.getText().toString();
        final String idNum=reg_idnm.getText().toString();
        new AsyncTask<Void, Void, Response>() {
            @Override
            protected Response doInBackground(Void... params) {
                try {
                    Log.i("download","开始连接");
                    Socket s=new Socket(ip,port);
                    Log.i("download","已经连接");
                    Request r=new Request();
//					r.setWhat(Request.WHAT_LIST);
                    r.setWhat(What.REGIST);//此处使用枚举
                    r.setName(name);
                    r.setPassWord(passWord);
                    r.setIdNum(idNum);
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
                    Log.e("download","zhuce出错",e);
                    Toast.makeText(RegActivity.this, "亲！你网断啦~", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Response result) {
                if(result==null){
                    Toast.makeText(RegActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                String name = result.getName();
                long size = result.getSize();
                if(name.equals("chenggong")){
                    Toast.makeText(RegActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RegActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(RegActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }.execute();

    }

}
