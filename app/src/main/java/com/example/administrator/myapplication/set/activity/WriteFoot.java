package com.example.administrator.myapplication.set.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.administrator.myapplication.BaseActivity;
import com.example.administrator.myapplication.utils.PreferencesUtils;
import com.example.administrator.myapplication.R;
import com.example.administrator.myapplication.database.MyDbManager;
import com.example.administrator.myapplication.set.fragment.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/1/10.
 */
public class WriteFoot extends BaseActivity implements View.OnClickListener{
    public static String SET_USERNAME = "username";
    private CircleImageView mCircleNameIcon;
    private EditText mEditContent;
    private Button mButtonBack,mButtonSet;
    private MyDbManager myDbManager;
    private Bitmap bitIcon;
    private String iconPath;

    private String name,time,content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_foot);
        initWidgets();
        iconPath =getApplicationContext().getExternalFilesDir("icon").getAbsolutePath()+"/ailafei/icon/myicon.jpg";
        bitIcon=getDiskBitmap(iconPath);
        if (bitIcon!=null){
            mCircleNameIcon.setImageBitmap(bitIcon);
        }else {
            mCircleNameIcon.setImageResource(R.mipmap.icon);
        }
        myDbManager=MyDbManager.newInstances(this);
    }

    private void initWidgets() {
        mCircleNameIcon= (CircleImageView) findViewById(R.id.name_icon);
        mEditContent = (EditText) findViewById(R.id.edit_content);
        mButtonBack= (Button) findViewById(R.id.button_name_back);
        mButtonBack.setOnClickListener(this);
        mButtonSet= (Button) findViewById(R.id.button_name_set);
        mButtonSet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        name=sharedPreferences.getString("mename","二锅头");
        content= mEditContent.getText().toString();
//        SimpleDateFormat formatter = new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss");
//        Date curDate = new   Date(System.currentTimeMillis());//获取当前时间
//        time   =   formatter.format(curDate);
        switch (v.getId()){
            case R.id.button_name_back:
                finish();
                break;
            case R.id.button_name_set:
                PreferencesUtils.getInstance().writeSharedString(this,Set.NAME,content);
                Intent intent = new Intent();
                intent.putExtra(SET_USERNAME, content);
                setResult(RESULT_OK,intent);
                finish();
                break;
            default:
                break;
        }
    }
}
