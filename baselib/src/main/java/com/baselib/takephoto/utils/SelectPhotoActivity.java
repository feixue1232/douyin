package com.baselib.takephoto.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;

import java.io.File;

/**
 * 选择头像中间页面
 * 传入值为 Type
 * CAMERA 调用相机
 * PHOTO 调用相册
 */
public class SelectPhotoActivity extends TakePhotoActivity {

    /**
     * 选择了相机
     */
    public static final String CAMERA = "CAMERA";
    /**
     * 选择了相册
     */
    public static final String PHOTO = "PHOTO";
    /**
     * 请求码
     */
    public static final int REQUEST_CODE = 1021;
    /**
     * 返回码
     */
    public static final int RESULT_CODE = 6666;

    /**
     * 返回路径
     */
    public static final String PATH = "path";

    /**
     * 图片
     */
    public static final String ITEM = "item";

    /**
     * 类型相册或相机
     */
    public static final String TYPE = "type";

    private static Class<?> mCls;

    File file;

    public static void start(Activity activity, Class<?> cls, String type) {
        start(activity, cls, type, "item");
    }

    public static void start(Activity activity, Class<?> cls, String Type, String item) {
        Intent intent = new Intent(activity, SelectPhotoActivity.class);
        intent.putExtra(TYPE, Type);
        intent.putExtra(ITEM, item);
        mCls = cls;
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectPhoto(getTakePhoto());
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
        finish();
    }


    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        finish();
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        if (result != null) {
            //这里是设置返回页面
            Intent intent = new Intent(SelectPhotoActivity.this, mCls);
            intent.putExtra(PATH, result.getImage().getCompressPath());
            intent.putExtra(ITEM, getIntent().getStringExtra(ITEM));
            setResult(RESULT_CODE, intent);
        }
        finish();
    }

    private void selectPhoto(TakePhoto takePhoto) {
        file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);

        //压缩参数
        CompressConfig config = new CompressConfig.Builder()
                .setMaxSize(50 * 1024)//尺寸
                .setMaxPixel(800)//最大像素
                .enableReserveRaw(false)//是否保留原文件
                .create();
        takePhoto.onEnableCompress(config, false);//压缩是对话框

        //是否使用自带相册
        TakePhotoOptions.Builder builder1 = new TakePhotoOptions.Builder();
        builder1.setWithOwnGallery(false);//是否使用TakePhoto自带的相册进行图片选择，默认不使用，但选择多张图片会使用
        takePhoto.setTakePhotoOptions(builder1.create());

        //剪切设置
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setOutputX(400).setOutputY(400);//剪切大小设置
        builder.setWithOwnCrop(true);

        String type = getIntent().getStringExtra(TYPE);
        if (CAMERA.equals(type)) {
            takePhoto.onPickFromCaptureWithCrop(imageUri, builder.create());
        } else if (PHOTO.equals(type)) {
            takePhoto.onPickFromGalleryWithCrop(imageUri, builder.create());
        } else {
            Toast.makeText(this, "缺少Type请用意图传入...", Toast.LENGTH_SHORT).show();
        }

    }

//    /**
//     * 回调选择头像地址
//     */
//        if (resultCode == SelectPhotoActivity.RESULT_CODE) {
//        switch (requestCode) {
//            case SelectPhotoActivity.REQUEST_CODE:   // 调用相机拍照
//                File file = new File(data.getStringExtra(SelectPhotoActivity.PATH));
//                if (file.exists() && file.length() == 0) {
//                    return;
//                }
//                break;
//        }
//    }
}