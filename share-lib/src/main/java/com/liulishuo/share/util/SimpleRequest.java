package com.liulishuo.share.util;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class SimpleRequest {
    private Request request;
    private RequestBody mRequestBody;
    public Request.Builder mRequestBuilder = new Request.Builder();

    public static final int METHOD_GET = 0x1;
    public static final int METHOD_POST = 0x2;
    private int method = METHOD_GET;

    public HashMap<String, String> mHeaders = new HashMap<String, String>();
    public HashMap<String, Object> mParams = new HashMap<String, Object>();
    public String mUrl;

    public SimpleRequest(int method, BaseParams params) {
        mUrl = params.getUrl();
        if (params.hasParameters()) {
            mParams = params.getParameters();
        }
        this.method = method;
    }

    public Request getRequest() {
        switch (method) {
            case METHOD_POST:
                initPostRequest();
                break;
            case METHOD_GET:
                initGetRequest();
                break;
        }
        return request;
    }

    private void prepareHeaders() {
        for (Map.Entry<String, String> map : mHeaders.entrySet()) {
            mRequestBuilder.header(map.getKey(), map.getValue());
        }
    }

    private void prepareParamsForPost() {
        if (mParams.size() != 0) {
            FormEncodingBuilder builder = new FormEncodingBuilder();
            for (Map.Entry<String, Object> map : mParams.entrySet()) {
                builder.add(map.getKey(), (String) map.getValue());
            }
            mRequestBody = builder.build();
        }
    }

    private void prepareParamsForGet() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : mParams.entrySet()) {
            if (entry.getValue() instanceof String) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(String.format("%s=%s", urlEncodeUTF8(entry.getKey()), urlEncodeUTF8((String) entry.getValue())));
            }
        }
        if (sb.toString().length() > 0) {
            if (mUrl.contains("?")) {
                mUrl = mUrl + "&";
            } else {
                mUrl = mUrl + "?";
            }
            mUrl = mUrl + sb.toString();
        }
    }

    private String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public void initPostRequest() {
        prepareHeaders();
        prepareParamsForPost();

        mRequestBuilder.url(mUrl);
        if (mRequestBody != null) {
            mRequestBuilder.post(mRequestBody);
        } else {
            mRequestBuilder.get();
        }
        request = mRequestBuilder.build();
    }

    public void initGetRequest() {
        prepareHeaders();
        prepareParamsForGet();

        mRequestBuilder.url(mUrl);
        mRequestBuilder.get();
        request = mRequestBuilder.build();
    }
}
