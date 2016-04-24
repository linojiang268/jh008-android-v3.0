package com.gather.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;

import com.gather.android.Constant;
import com.gather.android.GatherApplication;
import com.gather.android.ui.activity.ActDetail;
import com.gather.android.ui.activity.OrgHome;
import com.gather.android.ui.activity.WebActivity;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.PushUtils;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import io.yunba.android.manager.YunBaManager;

/**
 * Push接收处理
 * Created by Administrator on 2015/8/6.
 */
public class PushMessageReceiver extends BroadcastReceiver {

    private final static String REPORT_MSG_SHOW_NOTIFICARION = "1000";
    private final static String REPORT_MSG_SHOW_NOTIFICARION_FAILED = "1001";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (YunBaManager.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {

            String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
            String msg = intent.getStringExtra(YunBaManager.MQTT_MSG);

            if (Constant.SHOW_LOG) {
                StringBuilder showMsg = new StringBuilder();
                showMsg.append("Received message from server: ").append(YunBaManager.MQTT_TOPIC).append(" = ").append(topic).append(" ").append(YunBaManager.MQTT_MSG).append(" = ").append(msg);
                Logger.d(showMsg.toString());
            }
            try {
                JSONObject object = new JSONObject(msg);
                String content = object.getString("content");
                String type = object.getString("type");
                String title = "";
                if (object.has("title")) {
                    title = object.getString("title");
                }
                JSONObject attributes = null;
                if (object.has("attributes")) {
                    if (object.get("attributes") != null) {
                        attributes = new JSONObject(object.getString("attributes"));
                    }
                }
                // text、url、activity、team、cancel_subscribe、add_subscribe、version_upgrade
                if (type.equals("text")) {
                    if (title.equals("")) {
                        title = "集合消息";
                    }
                    TextMessage(context, topic, title, content, attributes);
                } else if (type.equals("url")) {
                    if (title.equals("")) {
                        title = "集合";
                    }
                    UrlMessage(context, topic, title, content, attributes);
                } else if (type.equals("activity")) {
                    if (title.equals("")) {
                        title = "集合活动";
                    }
                    ActivityMessage(context, topic, title, content, attributes);
                } else if (type.equals ("team")) {
                    if (title.equals("")) {
                        title = "集合社团";
                    }
                    TeamMessage(context, topic, title, content, attributes);
                } else if (type.equals("cancel_subscribe")) {
                    CancelSubscribeMessage(context, topic, title, content, attributes);
                } else if (type.equals("add_subscribe")) {
                    addSubscribeMessage(context, topic, title, content, attributes);
                } else if (type.equals("version_upgrade")) {
                    VersionUpgradeMessage(context, topic, title, content, attributes);
                } else if (type.equals("kick")) {
                    //设备已在其他地方登录
                    Intent login = new Intent();
                    login.putExtra("TYPE", 3);
                    login.setAction(BaseActivity.BROADCAST_FLAG);
                    GatherApplication.getInstance().sendBroadcast(login);
                } else {
                    //未知类型
                    Logger.d("未知类型PUSH");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(YunBaManager.PRESENCE_RECEIVED_ACTION.equals(intent.getAction())) {

            String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
            String payload = intent.getStringExtra(YunBaManager.MQTT_MSG);

            if (Constant.SHOW_LOG) {
                StringBuilder showMsg = new StringBuilder();
                showMsg.append("Received message presence: ").append(YunBaManager.MQTT_TOPIC).append(" = ").append(topic).append(" ").append(YunBaManager.MQTT_MSG).append(" = ").append(payload);
                Logger.d(showMsg.toString());
            }
        }
    }

    /**
     * 文本消息处理
     */
    private void TextMessage(Context context, String topic, String title, String content, JSONObject object) {
        try {
            boolean flag = PushUtils.showNotifation(context, title, content, null);
            if (flag) {
                YunBaManager.report(context, REPORT_MSG_SHOW_NOTIFICARION, topic);
            } else {
                YunBaManager.report(context, REPORT_MSG_SHOW_NOTIFICARION_FAILED, topic);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 网页Url消息处理
     */
    private void UrlMessage(Context context, String topic, String title, String content, JSONObject object) {
        try {
            Intent intent = new Intent(context, WebActivity.class);
            intent.putExtra("URL", object.getString("url"));
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(WebActivity.class);
            stackBuilder.addNextIntent(intent);
            boolean flag = PushUtils.showNotifation(context, title, content, stackBuilder);
            if (flag) {
                YunBaManager.report(context, REPORT_MSG_SHOW_NOTIFICARION, topic);
            } else {
                YunBaManager.report(context, REPORT_MSG_SHOW_NOTIFICARION_FAILED, topic);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 活动消息处理
     */
    private void ActivityMessage(Context context, String topic, String title, String content, JSONObject object) {
        try {
            Intent intent = new Intent(context, ActDetail.class);
            intent.putExtra(ActDetail.EXTRA_ID, object.getString("activity_id"));
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(ActDetail.class);
            stackBuilder.addNextIntent(intent);
            boolean flag = PushUtils.showNotifation(context, title, content, stackBuilder);
            if (flag) {
                YunBaManager.report(context, REPORT_MSG_SHOW_NOTIFICARION, topic);
            } else {
                YunBaManager.report(context, REPORT_MSG_SHOW_NOTIFICARION_FAILED, topic);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 社团消息处理
     */
    private void TeamMessage(Context context, String topic, String title, String content, JSONObject object) {
        try {
            Intent intent = new Intent(context, OrgHome.class);
            intent.putExtra("ID", object.getString("team_id"));
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(OrgHome.class);
            stackBuilder.addNextIntent(intent);
            boolean flag = PushUtils.showNotifation(context, title, content, stackBuilder);
            if (flag) {
                YunBaManager.report(context, REPORT_MSG_SHOW_NOTIFICARION, topic);
            } else {
                YunBaManager.report(context, REPORT_MSG_SHOW_NOTIFICARION_FAILED, topic);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 取消订阅
     */
    private void CancelSubscribeMessage(Context context, String topic, String title, String content, JSONObject object) {

    }

    /**
     * 加新订阅
     */
    private void addSubscribeMessage(Context context, String topic, String title, String content, JSONObject object) {

    }

    /**
     * 版本升级
     */
    private void VersionUpgradeMessage(Context context, String topic, String title, String content, JSONObject object) {
        try {
            int versionCode = object.getInt("version");
            if (versionCode > PhoneManager.getVersionInfo().versionCode) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(object.getString("url")));
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(WebActivity.class);
                stackBuilder.addNextIntent(intent);
                boolean flag = PushUtils.showNotifation(context, "版本更新啦~~", content, stackBuilder);
                if (flag) {
                    YunBaManager.report(context, REPORT_MSG_SHOW_NOTIFICARION, topic);
                } else {
                    YunBaManager.report(context, REPORT_MSG_SHOW_NOTIFICARION_FAILED, topic);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

}
