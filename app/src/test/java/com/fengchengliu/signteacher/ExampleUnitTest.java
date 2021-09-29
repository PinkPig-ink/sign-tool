package com.fengchengliu.signteacher;

import com.fengchengliu.signteacher.connect.HttpUtil;
import com.fengchengliu.signteacher.connect.RequestSign;

import org.junit.Test;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testConnect(){
        RequestSign requestSign = new RequestSign();

        System.out.println(requestSign.connect("http://localhost:8081/user/getUser/1679108504/wangshuo123"));

    }
    String message ="12312";
    @Test
    public void testConnectE(){

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("false");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("message1:");
                message = response.body().string();
//                response);

            }
        };
//        HttpUtil.getData("http://localhost:8081/user/getUser");
        HttpUtil.sendRequestWithOkhttp("http://localhost:8081/user/getUser",callback);
        System.out.println(message);
    }
}