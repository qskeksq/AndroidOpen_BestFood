package com.example.administrator.bestfood.lib;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.administrator.bestfood.R;

/**
 * Created by Administrator on 2017-09-26.
 */

public class DialogLib {
    public final String TAG = DialogLib.class.getSimpleName();
    private volatile static DialogLib instance;

    public static DialogLib getInstance() {
        if (instance == null) {
            synchronized (DialogLib.class) {
                if (instance == null) {
                    instance = new DialogLib();
                }
            }
        }
        return instance;
    }

    /**
     * 즐겨찾기 추가 다이얼로그 화면을 보여준다.
     * @param context 컨텍스트 객체
     * @param handler 핸들러 객체
     * @param memberSeq 사용자 일련번호
     * @param infoSeq 맛집 정보 일련번호
     */
    public void showKeepInsertDialog(Context context, final Handler handler,
                                     final int memberSeq, final int infoSeq) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.keep_insert)
                .setMessage(R.string.keep_insert_message)
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("[즐겨찾기]", "showKeepInsertDialog");
                        KeepLib.getInstance().insertKeep(handler, memberSeq, infoSeq);
                    }
                })
                .show();
    }

    /**
     * 즐겨찾기 삭제 다이얼로그 화면을 보여준다.
     * @param context 컨텍스트 객체
     * @param handler 핸들러 객체
     * @param memberSeq 사용자 일련번호
     * @param infoSeq 맛집 정보 일련번호
     */
    public void showKeepDeleteDialog(Context context, final Handler handler,
                                     final int memberSeq, final int infoSeq) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.keep_delete)
                .setMessage(R.string.keep_delete_message)
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        KeepLib.getInstance().deleteKeep(handler, memberSeq, infoSeq);
                    }
                })
                .show();
    }
}
