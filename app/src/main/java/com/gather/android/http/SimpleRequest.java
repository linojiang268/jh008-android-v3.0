package com.gather.android.http;

import android.os.Build;
import android.util.Log;

import com.gather.android.Constant;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.manager.PhoneManager;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class SimpleRequest {
    private Request request;
    private RequestBody mRequestBody;
    public Request.Builder mRequestBuilder = new Request.Builder();

    public static final int METHOD_GET = 0x1;
    public static final int METHOD_POST = 0x2;
    private int method = METHOD_GET;

    public HashMap<String, String> mHeaders = new HashMap<String, String>();
    public TreeMap<String, Object> mParams = new TreeMap<String, Object>();
    public String mUrl;
    public boolean hasFile = false;
    private UploadProgressRequestBody.UploadProgressListener listener;

    public SimpleRequest(int method, BaseParams params) {
        mUrl = params.getUrl();
        if (params.hasParameters()) {
            mParams = params.getParameters();
        }
        this.hasFile = params.hasFile();
        this.method = method;
    }

    public SimpleRequest(int method, BaseParams params, UploadProgressRequestBody.UploadProgressListener listener) {
        this(method, params);
        this.listener = listener;
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

    private void prepareParamsForPost(){
        StringBuffer sb = new StringBuffer();
        if (!hasFile) {
            if (mParams.size() != 0) {
                EncryptBuilder builder = new EncryptBuilder();
                for (Map.Entry<String, Object> map : mParams.entrySet()) {
                    String key = map.getKey();
                    String value = (String) map.getValue();
                    builder.add(key, value);
                }
                mRequestBody = builder.build();
            }
        } else {
            MultipartBuilder builder = new MultipartBuilder();
            builder.type(MultipartBuilder.FORM);
            Iterator<Map.Entry<String, Object>> iterator = mParams.entrySet().iterator();
            while (iterator.hasNext()) {
                java.util.Map.Entry<String, Object> map = iterator.next();
                if (map.getValue() instanceof Pair) {
                    Pair<String, File> filePair = (Pair<String, File>) map.getValue();
//                    builder.addPart(
//                            Headers.of("Content-Disposition", "form-data; name=\"" + map.getKey() + "\"; " + "filename=\"" + filePair.getSecond().getName() + "\"" + "\r\n"),
//                            RequestBody.create(MediaType.parse(filePair.getFirst()), filePair.getSecond()));
                    File file = filePair.getSecond();
                    if (listener != null){
                        builder.addFormDataPart(map.getKey(), file.getName(), new UploadProgressRequestBody(MediaType.parse(filePair.getFirst()), file, listener));
                    }
                    else {
                        builder.addFormDataPart(map.getKey(), file.getName(), RequestBody.create(MediaType.parse(filePair.getFirst()), file));
                    }
                } else if (map.getValue() instanceof String) {
//                    builder.addPart(
//                            Headers.of("Content-Disposition", "form-data; name=\"" + map.getKey() + "\""),
//                            RequestBody.create(null, (String) map.getValue()));
                    builder.addFormDataPart(map.getKey(),(String) map.getValue());
                    try {
                        if (map.getKey().matches("^\\w+\\[\\d+\\]$")) {//数组

                        } else {
                            if (sb.length() > 0) {
                                sb.append('&');
                            }
                            sb.append(URLEncoder.encode(map.getKey(), "UTF-8"))
                                    .append('=')
                                    .append(URLEncoder.encode((String) map.getValue(), "UTF-8"));
                        }
                    } catch (UnsupportedEncodingException e) {
                        throw new AssertionError(e);
                    }
                }
            }
            try {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"sign\""), RequestBody.create(null, URLEncoder.encode(addSign(sb), "UTF-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mRequestBody = builder.build();
        }
    }

    private String addSign(StringBuffer content) {
        String key = "";
        if (Constant.SHOW_LOG) {
            key = "key=F1C86DC81A8CBCE4EEB9D219B68D6E66&";
        } else {
            key = "key=RDPTQSUB1AKR7LO9Y17BTK2YC0PBAJ0L&";
        }
        key += content.toString();
        Log.i("Key", key);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(key.getBytes());
            StringBuffer buf = new StringBuffer();
            byte[] bits = md.digest();
            for (int i = 0; i < bits.length; i++) {
                int a = bits[i];
                if (a < 0)
                    a += 256;
                if (a < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(a));
            }
            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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
        request = mRequestBuilder.addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("User-Agent", "Gather" + PhoneManager.getVersionInfo().versionName + "/" + Build.VERSION.SDK_INT)
                .build();
    }

    public void initGetRequest() {
        prepareHeaders();
        prepareParamsForGet();

        mRequestBuilder.url(mUrl);
        mRequestBuilder.get();
//        request = mRequestBuilder.header("X-Requested-With", "XMLHttpRequest").build();
        request = mRequestBuilder.addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("User-Agent", "Gather" + PhoneManager.getVersionInfo().versionName + "/" + Build.VERSION.SDK_INT)
                .build();
    }

}
