package com.gather.android.baseclass;

import android.content.Intent;

import com.gather.android.Constant;
import com.gather.android.GatherApplication;
import com.gather.android.data.UserPref;
import com.gather.android.http.HttpCode;
import com.orhanobut.logger.Logger;
import com.shizhefei.HttpResponseStatus;
import com.shizhefei.mvc.IDataSource;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/7/11.
 */
public class BaseDataSource<DATA> implements IDataSource<DATA> {
    @Override
    public DATA refresh() throws Exception {
        return null;
    }

    @Override
    public DATA loadMore() throws Exception {
        return null;
    }

    @Override
    public boolean hasMore() {
        return false;
    }

    @Override
    public HttpResponseStatus onResp(Object resp) {
        HttpResponseStatus resStatus = new HttpResponseStatus();
        resStatus.setResult("请求失败，请重试");
        resStatus.setSuccess(false);

        Response response = null;
        if (resp != null && resp instanceof Response) {
            response = (Response) resp;
            if (response.isSuccessful()) {
                try {
                    String responseData = response.body().string();
                    if (Constant.SHOW_LOG){
                        Logger.json(responseData);
                    }
                    JSONObject object = new JSONObject(responseData);
                    int code = object.getInt("code");
                    switch (code) {
                        case 0://请求成功
                            resStatus.setSuccess(true);
                            resStatus.setResult(responseData);
                            break;
                        case 10101://cookie失效，需要重新登录
                            resStatus.setResult(object.getString("message"));

                            Intent intent1 = new Intent();
                            intent1.putExtra("TYPE", 1);
                            intent1.setAction(BaseActivity.BROADCAST_FLAG);
                            GatherApplication.getInstance().sendBroadcast(intent1);
                            break;
                        case 10102://需要完善资料
                            resStatus.setResult(object.getString("message"));
                            if (object.has("push_alias")) {
                                UserPref.getInstance().setAlias(object.getString("push_alias"));
                            }

                            Intent intent2 = new Intent();
                            intent2.putExtra("TYPE", 2);
                            intent2.setAction(BaseActivity.BROADCAST_FLAG);
                            break;
                        case 10103://在其他地方登录
                            resStatus.setResult(object.getString("message"));

                            Intent intent3 = new Intent();
                            intent3.putExtra("TYPE", 3);
                            intent3.setAction(BaseActivity.BROADCAST_FLAG);
                            break;
                        default:
                            resStatus.setResult(object.getString("message"));
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    resStatus.setResult("数据格式错误");
                } catch (Exception e) {
                    e.printStackTrace();
                    resStatus.setResult("返回数据错误");
                }
            }
        }
        return resStatus;
    }
}
