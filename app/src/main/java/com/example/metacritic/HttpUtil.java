package com.example.metacritic;

import android.os.Build;
import android.webkit.WebSettings;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by CJ on 2017/6/11.
 */

public class HttpUtil {
    private static String mobileAgent= "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Mobile Safari/537.36";
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).header("User-Agent",mobileAgent).build();
        client.newCall(request).enqueue(callback);
    }

}
