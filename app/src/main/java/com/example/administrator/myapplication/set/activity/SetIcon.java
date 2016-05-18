package com.example.administrator.myapplication.set.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.administrator.myapplication.BaseActivity;
import com.example.administrator.myapplication.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/1/5.
 */
public class SetIcon extends BaseActivity implements View.OnClickListener{
    private static final int PICTURE_FROM_CAMERA=101;
    private static final int PICTURE_FROM_GALLERY=102;
    private Button mButtonCamera,mButtonPhoto,mButtonCancel;
    private File file;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_icon);
        mButtonCamera= (Button) findViewById(R.id.button_camera);
        mButtonCamera.setOnClickListener(this);
        mButtonPhoto= (Button) findViewById(R.id.button_photo);
        mButtonPhoto.setOnClickListener(this);
        mButtonCancel= (Button) findViewById(R.id.button_cancel);
        mButtonCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_camera:
                startCamera();
                break;
            case R.id.button_photo:
                Intent intent = new Intent();
                //设置启动相册的Action
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //设置类型
                intent.setType("image/*");
                //启动相册，这里使用有返回结果的启动
                startActivityForResult(intent, PICTURE_FROM_GALLERY);
                break;
            case R.id.button_cancel:
                finish();
                break;
            default:
                break;
        }
    }
    /**
     * 调用系统相机拍照
     */
    private void startCamera() {
        Intent intent = new Intent();
        //启动相机的Action
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //文件的保存位置
        file = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //设置图片拍摄后保存的位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        //启动相机，这里使用有返回结果的启动
        startActivityForResult(intent, PICTURE_FROM_CAMERA);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICTURE_FROM_CAMERA:
                    Bitmap bp = getCameraBitmap();
                    try {
                        saveFile(bp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finish();
                    break;
                case PICTURE_FROM_GALLERY:
                    Bitmap bit = getPhotoBitmap(data);
                    try {
                        saveFile(bit);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finish();
                    break;
            }
        }
    }
    public Bitmap getPhotoBitmap(Intent data) {
        Bitmap bitmap=null;
        try {
            uri=data.getData();
            bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            bitmap= ThumbnailUtils.extractThumbnail(bitmap, 300, 300);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 得到相机拍照后的照片的bitmap
     * @return
     */
    public Bitmap getCameraBitmap() {
        Bitmap bitmap=null;
        try {
            bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.fromFile(file)));
            bitmap= ThumbnailUtils.extractThumbnail(bitmap, 300, 300);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        };
        return bitmap;
    }
    /**
     * 保存文件
     * @param bm
     * @throws IOException
     */
    public File saveFile(Bitmap bm) throws IOException {

        String path =getApplicationContext().getExternalFilesDir("icon").getAbsolutePath()+"/ailafei/icon";
        Log.d("zhaopianweizhi123",path);
       // String path = Environment.getExternalStorageDirectory().toString()+"/w65/icon_bitmap/";
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        File myIconFile= new File(path +"/myicon.jpg");
        Log.d("zhaopianweizhi123",myIconFile.getAbsolutePath());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myIconFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return myIconFile;
    }
}
