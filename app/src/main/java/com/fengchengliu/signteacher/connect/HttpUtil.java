package com.fengchengliu.signteacher.connect;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {




    public static void sendRequestWithOkhttp(String address,okhttp3.Callback callback)
    {
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    public static  String  getData(String url){
        ;
        HttpUtil.sendRequestWithOkhttp(url,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    final String message   = response.body().string();

                }catch (IOException e){
                    e.printStackTrace();
                }
                dealData(response);

            }
        });

        return null;
    }

    private static void dealData(Response response) {

    }
}
