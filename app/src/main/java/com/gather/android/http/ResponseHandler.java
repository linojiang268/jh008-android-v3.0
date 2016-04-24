package com.gather.android.http;

import android.content.Intent;

import com.gather.android.baseclass.BaseActivity;
import com.gather.android.data.UserPref;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Levi on 2015/9/23.
 */
public abstract class ResponseHandler extends HandlerCallBack {

    @Override
    public void onFailure(Request request, IOException e) {
        setErrorMsg(HttpCode.NET_ERROR, "请求失败，请检查网络并重试");
        sendReulstMessage();
    }

    @Override
    public void upload(int progress) {

    }

    @Override
    public void onResponse(Response response) throws IOException {
        if (response != null && response.isSuccessful()){
            try {
                String responseData = response.body().string();
                JSONObject object = new JSONObject(responseData);
                int code = object.getInt("code");
                switch (code){
                    case 0://请求成功
                        setData(responseData);
                        break;
                    case 10101://cookie失效，需要重新登录
                        Intent intent1 = new Intent();
                        intent1.putExtra("TYPE", 1);
                        intent1.setAction(BaseActivity.BROADCAST_FLAG);
                        setBroadcast(intent1, object.getString("message"));
                        break;
                    case 10102://需要完善资料
                        if (object.has("push_alias")) {
                            UserPref.getInstance().setAlias(object.getString("push_alias"));
                        }

                        Intent intent2 = new Intent();
                        intent2.putExtra("TYPE", 2);
                        intent2.setAction(BaseActivity.BROADCAST_FLAG);
                        setBroadcast(intent2, object.getString("message"));
                        break;
                    case 10103://在其他地方登录
                        Intent intent3 = new Intent();
                        intent3.putExtra("TYPE", 3);
                        intent3.setAction(BaseActivity.BROADCAST_FLAG);
                        setBroadcast(intent3, object.getString("message"));
                        break;
                    default:
                        setErrorMsg(code, object.getString("message"));
                        break;
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
                setErrorMsg(HttpCode.JSON_ERROR, "数据格式错误");
            }
            catch (Exception e){
                setErrorMsg(HttpCode.RESPONSE_ERROR, "返回数据错误");
            }
        }
        else {
            setErrorMsg(HttpCode.RESPONSE_FAIL_ERROR, "请求失败，请重试");
        }
        sendReulstMessage();
    }
}
