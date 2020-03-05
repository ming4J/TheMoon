/*******************************************************************************
 *
 * Copyright (c) Weaver Info Tech Co. Ltd
 *
 * TitleActivity
 *
 * app.client.TitleActivity.java
 * TODO: File description or class description.
 *
 * @author: gao_chun
 * @since:  2014-9-03
 * @version: 1.0.0
 *
 * @changeLogs:
 *     1.0.0: First created this class.
 *
 ******************************************************************************/
package org.benpao.widget;

import org.benpao.viewactivity.ForgetActivity;

import org.benpao.viewactivity.MainActivity;
import org.benpao.viewactivity.R;
import org.benpao.viewactivity.RegActivity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;


/**
 * @author gao_chun
 * 自定义标题栏
 */
public class TitleActivity extends Activity implements OnClickListener{

    //private RelativeLayout mLayoutTitleBar;
    private TextView mTitleTextView;
    private Button mBackwardbButton;
    private Button mForwardButton;
    private FrameLayout mContentLayout;
    private TextView mForgetpass;
    private Button mLoginButton;

    public int flag;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();   //加载 activity_title 布局 ，并获取标题及两侧按钮
    }


    private void setupViews() {
        super.setContentView(R.layout.activity_title);
        mTitleTextView = (TextView) findViewById(R.id.text_title);
        mContentLayout = (FrameLayout) findViewById(R.id.layout_content);
        mBackwardbButton = (Button) findViewById(R.id.button_backward);
        mForwardButton = (Button) findViewById(R.id.button_forward);
        mForgetpass=(TextView) findViewById(R.id.forget_main);
        mLoginButton=(Button) findViewById(R.id.main_Login);


    }



    /**
     * 是否显示返回按钮
     * @param backwardResid  文字
     * @param show  true则显示
     */
    protected void showBackwardView(int backwardResid, boolean show) {
        if (mBackwardbButton != null) {
            if (show) {
               // mBackwardbButton.setText(backwardResid);
               mBackwardbButton.setVisibility(View.VISIBLE);
              //  mBackwardbButton.setVisibility(View.GONE);
            } else {
               //mBackwardbButton.setVisibility(View.INVISIBLE);
                mBackwardbButton.setVisibility(View.GONE);
            }
        } // else ignored
    }

    /**
     * 提供是否显示提交按钮
     * @param forwardResId  文字
     * @param show  true则显示
     */
    protected void showForwardView(int forwardResId, boolean show) {
        if (mForwardButton != null) {
            if (show) {
                mForwardButton.setVisibility(View.VISIBLE);
                mForwardButton.setText(forwardResId);
            } else {
              //  mForwardButton.setVisibility(View.INVISIBLE);
                mForwardButton.setVisibility(View.GONE);
            }
        } // else ignored
    }
    //点击注册 按钮触发的事件
    protected void onForward(View forwardView) {

        Intent intent=new Intent(TitleActivity.this, RegActivity.class);
        startActivity(intent);
        finish();



    }
    //忘记密码 文字点击后触发的事件
    protected void onforgetpass(View forgetView){
        Intent intent=new Intent(TitleActivity.this, ForgetActivity.class);
        startActivity(intent);
        finish();

    }
    //点击登陆 后触发的事件


    /**
     * 返回按钮点击后触发
     * @param backwardView
     */
    //TitleBar 返回按钮点击触发的事件
    protected void onBackward(View backwardView) {
        RegActivity regActivity=new RegActivity();
        System.out.println(regActivity.flag);
        if(regActivity.flag==0){
            Intent intent=new Intent(TitleActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
//            try{
//                Runtime runtime = Runtime.getRuntime();
//                runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
//            }catch (Exception e){



//            Instrumentation inst = new Instrumentation();
//            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            flag=flag+1;
        }else{
            System.out.println("OK oK oK oK");

        }
        //finish();
    }




    //设置标题内容
    @Override
    public void setTitle(int titleId) {
        mTitleTextView.setText(titleId);
    }

    //设置标题内容
    @Override
    public void setTitle(CharSequence title) {
        mTitleTextView.setText(title);
    }

    //设置标题文字颜色
    @Override
    public void setTitleColor(int textColor) {
        mTitleTextView.setTextColor(textColor);
    }


    //取出FrameLayout并调用父类removeAllViews()方法
    @Override
    public void setContentView(int layoutResID) {
        mContentLayout.removeAllViews();
        View.inflate(this, layoutResID, mContentLayout);
        onContentChanged();
    }

    @Override
    public void setContentView(View view) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view);
        onContentChanged();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#setContentView(android.view.View, android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void setContentView(View view, LayoutParams params) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view, params);
        onContentChanged();
    }



    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     * 按钮点击调用的方法
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_backward:
                onBackward(v);
                break;
            case R.id.button_forward:
                onForward(v);
                break;
            case R.id.forget_main:
                onforgetpass(v);
                break;

            default:
                break;
        }
    }
}
