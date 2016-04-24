package com.liulishuo.share.wechat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.liulishuo.share.R;
import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.data.ShareConstants;
import com.liulishuo.share.model.PlatformActionListener;
import com.liulishuo.share.util.BaseParams;
import com.liulishuo.share.util.SimpleRequest;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;


/**
 * Created by echo on 5/19/15.
 * <p/>
 * 参考文档:https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317853&lang=zh_CN
 */
public class WechatHandlerActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI mIWXAPI;

    private PlatformActionListener mPlatformActionListener;

    private static final String API_URL = "https://api.weixin.qq.com";

    /**
     * BaseResp的getType函数获得的返回值，1:第三方授权， 2:分享
     */
    private static final int TYPE_LOGIN = 1;

    private static final OkHttpClient mOkHttpClient = new OkHttpClient();

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = WechatHandlerActivity.this;
        mIWXAPI = WechatLoginManager.getIWXAPI();
        if (mIWXAPI != null) {
            mIWXAPI.handleIntent(getIntent(), this);
        }
        finish();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mIWXAPI != null) {
            mIWXAPI.handleIntent(getIntent(), this);
        }
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        mPlatformActionListener = WechatLoginManager.getPlatformActionListener();
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:

                if (resp.getType() == TYPE_LOGIN) {
                    final String code = ((SendAuth.Resp) resp).code;
                    BaseParams params = new BaseParams(API_URL + "/sns/oauth2/access_token");
                    params.put("appid", ShareBlock.getInstance().getWechatAppId());
                    params.put("secret", ShareBlock.getInstance().getWechatSecret());
                    params.put("code", code);
                    params.put("grant_type", "authorization_code");
                    mOkHttpClient.newCall(new SimpleRequest(SimpleRequest.METHOD_POST, params).getRequest()).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            if (mPlatformActionListener != null) {
                                mPlatformActionListener.onError();
                            }
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            try {
                                if (response.isSuccessful()) {
                                    String json = response.body().string();
                                    JSONObject jsonObject = new JSONObject(json);
                                    final String accessToken = jsonObject.getString("access_token");
                                    final String openId = jsonObject.getString("openid");

                                    BaseParams params1 = new BaseParams(API_URL + "/sns/userinfo");
                                    params1.put("access_token", accessToken);
                                    params1.put("openid", openId);
                                    mOkHttpClient.newCall(new SimpleRequest(SimpleRequest.METHOD_POST, params1).getRequest()).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Request request, IOException e) {
                                            if (mPlatformActionListener != null) {
                                                mPlatformActionListener.onError();
                                            }
                                        }

                                        @Override
                                        public void onResponse(Response response) throws IOException {
                                            try {
                                                if (response.isSuccessful()) {
                                                    String json = response.body().string();
                                                    JSONObject jsonObject = new JSONObject(json);
                                                    HashMap<String, Object> userInfoHashMap = new HashMap<String, Object>();
                                                    userInfoHashMap.put(ShareConstants.PARAMS_NICK_NAME, jsonObject.getString("nickname"));
                                                    userInfoHashMap.put(ShareConstants.PARAMS_SEX, jsonObject.getInt("sex"));
                                                    userInfoHashMap.put(ShareConstants.PARAMS_IMAGEURL, jsonObject.getString("headimgurl"));
                                                    userInfoHashMap.put(ShareConstants.PARAMS_USERID, jsonObject.getString("unionid"));
                                                    if (mPlatformActionListener != null) {
                                                        mPlatformActionListener.onComplete(userInfoHashMap);
                                                    }
                                                } else {
                                                    Toast.makeText(mContext, "登录失败，请重试", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                if (mPlatformActionListener != null) {
                                                    mPlatformActionListener.onError();
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(mContext, "登录失败，请重试", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (mPlatformActionListener != null) {
                                    mPlatformActionListener.onError();
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.share_success), Toast.LENGTH_SHORT).show();
                }

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:

                if (resp.getType() == TYPE_LOGIN) {
                    if (mPlatformActionListener != null) {
                        mPlatformActionListener.onCancel();
                    }
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.share_cancel), Toast.LENGTH_SHORT).show();
                }

                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                if (resp.getType() == TYPE_LOGIN) {
                    if (mPlatformActionListener != null) {
                        mPlatformActionListener.onError();
                    }
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.share_failed), Toast.LENGTH_SHORT).show();
                }

                break;
        }
        finish();
    }
}
