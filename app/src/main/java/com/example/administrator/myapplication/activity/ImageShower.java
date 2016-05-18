package com.example.administrator.myapplication.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.myapplication.BaseActivity;
import com.example.administrator.myapplication.R;

import java.io.File;

/**
 * Created by Administrator on 2016/1/3.
 */
public class ImageShower extends BaseActivity {
private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_showerd);
        mImageView= (ImageView) findViewById(R.id.image_large);
        String path= Environment.getExternalStorageDirectory()+"/w65/icon_bitmap/"+ "myicon.jpg";
        if (path!=null){
            mImageView.setImageBitmap(getDiskBitmap(path));
        }else {
            mImageView.setImageResource(R.mipmap.icon);
            Toast.makeText(getApplicationContext(), "zanshi没有照片", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        finish();
        return true;
    }
    /**
     * 从本地获取图片
     * @param pathString 文件路径
     * @return 图片
     */
    public static Bitmap getDiskBitmap(String pathString)
    {
        Bitmap bitmap = null;
        try
        {
            File file = new File(pathString);
            if(file.exists())
            {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e)
        {
            // TODO: handle exception
        }
        return bitmap;
    }
}
