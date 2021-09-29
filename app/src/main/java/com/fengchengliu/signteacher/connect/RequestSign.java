package com.fengchengliu.signteacher.connect;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RequestSign {

    public String connect(String url)  {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        Response response= null;
        String message  = null;
        try {
            response = call.execute();
            message = response.body().string();
        }catch (IOException e){
            e.printStackTrace();
        }
        return message;
    }
}

//class test{
//    public static void main(String[] args) {
//
//
//
//    }
//}