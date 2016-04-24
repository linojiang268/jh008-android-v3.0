package com.gather.android.http;

import com.gather.android.GatherApplication;
import com.gather.android.baseclass.BaseParams;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

/**
 * Created by Christain on 4/9/15.
 */
public class OkHttpUtil {

    private static final OkHttpClient mOkHttpClient = new OkHttpClient();

    static {
        mOkHttpClient.setConnectTimeout(5, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(30, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
//        mOkHttpClient.setRetryOnConnectionFailure(true);
        mOkHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(GatherApplication.getInstance()), CookiePolicy.ACCEPT_ALL));
    }

    public static OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }


    public static Response syncGet(BaseParams params) {
        SimpleRequest simpleRequest = new SimpleRequest(SimpleRequest.METHOD_GET, params);
        Response response = null;
        try {
            response = mOkHttpClient.newCall(simpleRequest.getRequest()).execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void post(BaseParams params) throws IOException {
        SimpleRequest simpleRequest = new SimpleRequest(SimpleRequest.METHOD_POST, params);
        mOkHttpClient.newCall(simpleRequest.getRequest()).execute();
    }

    public static void get(BaseParams params) throws IOException {
        SimpleRequest simpleRequest = new SimpleRequest(SimpleRequest.METHOD_GET, params);
        mOkHttpClient.newCall(simpleRequest.getRequest()).execute();
    }

    public static void get(BaseParams params, HandlerCallBack handler) {
        SimpleRequest simpleRequest = new SimpleRequest(SimpleRequest.METHOD_GET, params);
        mOkHttpClient.newCall(simpleRequest.getRequest()).enqueue(handler);
    }

    public static void post(BaseParams params, HandlerCallBack handler) {
        SimpleRequest simpleRequest = new SimpleRequest(SimpleRequest.METHOD_POST, params);
        mOkHttpClient.newCall(simpleRequest.getRequest()).enqueue(handler);
    }

    public static void post(BaseParams params,  ResponseProfressHandler handler) {
        SimpleRequest simpleRequest = new SimpleRequest(SimpleRequest.METHOD_POST, params, handler);
        mOkHttpClient.newCall(simpleRequest.getRequest()).enqueue(handler);
    }


}
