package com.loadmore.mark.testloadmore;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Mark.Han on 2017/5/8.
 */
public class HttpClient {
    public static AsyncHttpClient client = new AsyncHttpClient();    //实例化对象

    static {
        client.setTimeout(30000);   //设置链接超时，如果不设置，默认为10s
    }

    public static void get(String urlString, RequestParams params, AsyncHttpResponseHandler res)   //url里面带参数
    {
        if (client == null) {
            client = new AsyncHttpClient();
        }
        Log.i("HttpUtil", "带参数 " + urlString + "?" + params.toString());
        client.get(urlString, params, res);
    }

}
