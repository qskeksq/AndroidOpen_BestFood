package com.example.administrator.bestfood.lib;

import android.os.Handler;
import android.util.Log;

import com.example.administrator.bestfood.remote.IRemoteService;
import com.example.administrator.bestfood.remote.ServiceGenerator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017-09-26.
 */

public class KeepLib {

    public final String TAG = KeepLib.class.getSimpleName();
    private volatile static KeepLib instance;

    public static KeepLib getInstance() {
        if (instance == null) {
            synchronized (KeepLib.class) {
                if (instance == null) {
                    instance = new KeepLib();
                }
            }
        }
        return instance;
    }

    /**
     * 즐겨찾기 추가를 서버에 요청한다.
     * @param handler 결과를 응답할 핸들러
     * @param memberSeq 사용자 일련번호
     * @param infoSeq 맛집 정보 일련번호
     */
    public void insertKeep(final Handler handler, int memberSeq, final int infoSeq){
        IRemoteService remoteService = ServiceGenerator.createService(IRemoteService.class);
        Call<ResponseBody> call = remoteService.insertKeep(memberSeq, infoSeq);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    handler.sendEmptyMessage(infoSeq);
                    Log.e("[즐겨찾기]", "insertKeep");
                } else {
                    Log.e("[즐겨찾기]", "insertKeep 이미 있음");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("[즐겨찾기]", "insertKeep 실패");
            }
        });
    }

    /**
     * 즐겨찾기 삭제를 서버에 요청한다.
     * @param handler 결과를 응답할 핸들러
     * @param memberSeq 사용자 일련번호
     * @param infoSeq 맛집 정보 일련번호
     */
    public void deleteKeep(final Handler handler, int memberSeq, final int infoSeq) {
        IRemoteService remoteService = ServiceGenerator.createService(IRemoteService.class);
        Call<String> call = remoteService.deleteKeep(memberSeq, infoSeq);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    handler.sendEmptyMessage(infoSeq);
                } else { // 등록 실패

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


}
