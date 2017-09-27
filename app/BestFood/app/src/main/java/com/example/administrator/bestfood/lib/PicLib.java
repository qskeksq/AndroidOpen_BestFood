package com.example.administrator.bestfood.lib;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by Administrator on 2017-09-25.
 */

public class PicLib {

    public static final int PIC_FROM_CAMERA = 0;
    public static final int PIC_FROM_ALBUM = 1;
    public static final int CROP_FROM_CAMERA = 2;
    public static final int CROP_FROM_ALBUM = 3;
    private static PicLib sPicLib;

    private PicLib() {

    }

    public static PicLib getInstance(){
        if(sPicLib == null) {
            sPicLib = new PicLib();
        }
        return sPicLib;
    }

    public void getImageFromCamera(Activity activity, File imageFile){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFile);
        activity.startActivityForResult(intent, PIC_FROM_CAMERA);
    }

    public void getImageFromAlbum(Activity activity){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        activity.startActivityForResult(intent, PIC_FROM_ALBUM);
    }

    public Intent getCropImageIntent(Uri beforeImg, Uri afterImg){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(beforeImg, "image/*");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, afterImg);
        intent.putExtra("outputForm", Bitmap.CompressFormat.PNG);
        return intent;
    }

    public void cropImageFromCamera(Activity activity, File imageFile){
        Uri cropUri = Uri.fromFile(imageFile);
        Intent intent = getCropImageIntent(cropUri, cropUri);
        activity.startActivityForResult(intent, CROP_FROM_CAMERA);
    }

    public void cropImageFromAlbum(Activity activity, File imageFile, Uri beforeUri){
        Uri afterUri = Uri.fromFile(imageFile);
        Intent intent = getCropImageIntent(beforeUri, afterUri);
        activity.startActivityForResult(intent, CROP_FROM_ALBUM);
    }


}
