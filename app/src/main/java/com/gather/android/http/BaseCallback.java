package com.gather.android.http;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public abstract class BaseCallback implements Callback {

    @Override
    public void onFailure(Request request, IOException e) {
        onBaseFailed(null, e);
    }

    @Override
    public void onResponse(Response response) throws IOException {
        onBaseSuccess(response);
    }

    public abstract void onBaseSuccess(Response response);

    public abstract void onBaseFailed(Response response, Exception e);

}