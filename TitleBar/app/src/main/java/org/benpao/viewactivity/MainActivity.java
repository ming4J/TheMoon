
package org.benpao.viewactivity;

import org.benpao.widget.TitleActivity;
import org.benpao.netcheck.netcheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.download.Request;
import com.download.Response;
import com.download.What;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

public class MainActivity extends TitleActivity {
    private EditText main_name;
    private EditText main_pass;
    private Button main_login;
    private String ip;
    private SharedPreferences setting;
    private CheckBox checkBox;
    private int port=8000;
    private long firstTime = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ip=this.getString(R.string.ipadd);

        setContentView(R.layout.activity_main);

        setTitle("登录玉盘");

        showBackwardView(R.string.text_back,false);
       showForwardView(R.string.text_forward, true);
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
        main_name=(EditText)findViewById(R.id.main_unm);
        main_pass=(EditText)findViewById(R.id.main_pass);
        main_login=(Button)findViewById(R.id.main_Login);
        checkBox=(CheckBox)findViewById(R.id.main_rem);
        setting=getSharedPreferences("setting",MODE_PRIVATE);
        boolean checkpre=setting.getBoolean("check",false);
        if(checkpre){
            checkBox.setChecked(true);
            String username=setting.getString("username","");
            main_name.setText(username);
        }





    }



    private void setListeners(){
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()){
                    setting.edit()
                            .putBoolean("check",false)
                            .commit();

                }
            }
        });

        main_login.setOnClickListener(new View.OnClickListener() {
            netcheck checknet=new netcheck();
            @Override
            public void onClick(View v) {

                if (checknet.isNetworkConnected(MainActivity.this))

                if (main_name.getText().toString().equals("")||main_pass.getText().toString().equals("")||main_name.getText().toString().length()!=11){
                    Toast.makeText(MainActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                }else{
                    login();
                }else {
                    Toast.makeText(MainActivity.this,"亲~你的网络已断开",Toast.LENGTH_SHORT).show();
                    //main_login.setEnabled(false);

                }


//                Intent intent=new Intent(MainActivity.this,UserActivity.class);
//                intent.putExtra("ip", arr[0]);
//                intent.putExtra("port", Integer.parseInt(arr[1]));
//                intent.putExtra("name", "18511337814");
//                intent.putExtra("passWord", passWord);
//
//
//                startActivity(intent);
//                finish();

            }
        });
    }
    protected void login(){
        //Toast.makeText(MainActivity.this, "执行登录", Toast.LENGTH_SHORT).show();
        //final String [] arr={"192.168.1.101","8000"};


        final String name=main_name.getText().toString();
        final String passWord=main_pass.getText().toString();
        new AsyncTask<Void, Void, Response>(){

            @Override
            protected Response doInBackground(Void... params) {//工作线程执行

                try {
                    Log.i("download","开始连接");
                    Socket s=new Socket(ip,port);
                    Log.i("download","已经连接");
                    Request r=new Request();
//					r.setWhat(Request.WHAT_LIST);
                    r.setWhat(What.LOGIN);//此处使用枚举
                    r.setName(name);
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
                    Log.i("download","names:"+ Arrays.toString(resp.getNames()));
                    Log.i("download","sizes:"+Arrays.toString(resp.getSizes()));

                    return resp;//发到主线程，然后转到onPostExecute
                } catch (Exception e) {
                    // TODO: handle exception
                    Log.e("download","获得列表出错",e);
                    Toast.makeText(MainActivity.this, "亲！您网断了~", Toast.LENGTH_SHORT).show();
                    return null;
                }

            }

            @Override
            protected void onPostExecute(Response result) {//在主线程执行

                if(result==null){
                    Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                String name = result.getName();
                long size = result.getSize();
                if(name.equals("chenggong")){
                    String username=main_name.getText().toString();
                    if (checkBox.isChecked()){
                        setting.edit()
                                .putString("username",username)
                                .putBoolean("check",true)
                                .commit();

                    }

                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(MainActivity.this,UserActivity.class);
                    intent.putExtra("ip", ip);
                    intent.putExtra("port", port);
                    intent.putExtra("name", main_name.getText().toString());
                    intent.putExtra("passWord", passWord);


                    startActivity(intent);
                    finish();

                }else{
                    Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    main_pass.setText("");

                }

            }
        }.execute();
    }




}

