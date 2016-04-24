package com.gather.android.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.gather.android.API;
import com.gather.android.Constant;
import com.gather.android.GatherApplication;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.data.CityPref;
import com.gather.android.data.UserPref;
import com.gather.android.dialog.ActScoreDialog;
import com.gather.android.dialog.DialogCreater;
import com.gather.android.entity.ActScoreListEntity;
import com.gather.android.entity.HomeNoMyActEvent;
import com.gather.android.event.EventCenter;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.ui.fragment.ActFragment;
import com.gather.android.ui.fragment.HomeFragment;
import com.gather.android.ui.fragment.OrgListFragment;
import com.gather.android.utils.Checker;
import com.gather.android.utils.LocationUtils;
import com.gather.android.utils.PushUtils;
import com.jihe.dialog.listener.OnBtnClickL;
import com.orhanobut.logger.Logger;
import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.viewpager.SViewPager;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.InjectView;
import de.greenrobot.event.Subscribe;
import io.yunba.android.manager.YunBaManager;

/**
 * Created by Christain on 2015/5/26.
 */
public class IndexHome extends BaseActivity {

    @InjectView(R.id.tab_viewPager)
    SViewPager viewPager;
    @InjectView(R.id.tab_indicator)
    Indicator indicator;

    private IndicatorViewPager indicatorViewPager;
    private ImageView ivTip;
    private int index;
    private Dialog mCitydialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_home);

        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        indicatorViewPager.setAdapter(new IndexHomeAdapter(getSupportFragmentManager()));
        // 禁止viewpager的滑动事件
        viewPager.setCanScroll(false);
        // 设置viewpager保留界面不重新加载的页面数量
        viewPager.setOffscreenPageLimit(3);
        viewPager.setOnPageChangeListener(new HomePagerChangeListener());

        index = 0;
        if (savedInstanceState != null) {
            index = savedInstanceState.getInt("index");
            indicator.setCurrentItem(index);
            viewPager.setCurrentItem(index);
        }


        EventCenter.getInstance().register(this);
        if (YunBaManager.isStopped(getApplicationContext())) {
            YunBaManager.resume(getApplicationContext());
        }
        handler.sendEmptyMessageDelayed(4, 2000);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("index", index);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return false;
    }

    private class IndexHomeAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        private String[] tabNames = {"首页", "活动", "社团"};
        private int[] tabIcons = {R.drawable.tab_host_home_icon_style, R.drawable.tab_host_act_icon_style, R.drawable.tab_host_org_icon_style};
        private LayoutInflater inflater;

        public IndexHomeAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            inflater = LayoutInflater.from(getApplicationContext());
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.tab_host_item, container, false);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.tv_icon);
            textView.setText(tabNames[position]);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_icon);
            imageView.setImageResource(tabIcons[position]);
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            LazyFragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new HomeFragment();
                    break;
                case 1:
                    fragment = new ActFragment();
                    break;
                case 2:
                    fragment = new OrgListFragment();
                    break;
            }
            return fragment;
        }
    }

    private class HomePagerChangeListener implements SViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            index = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Subscribe
    public void onEvent(HomeNoMyActEvent event) {
        indicator.setCurrentItem(1);
        viewPager.setCurrentItem(1);
    }

    /**
     * 检查是否需要评分
     */
    private void checkActScroe() {
        BaseParams params = new BaseParams(API.GET_ACT_SCORE);
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                ActScoreListEntity entity = JSON.parseObject(msg, ActScoreListEntity.class);
                if (entity != null && entity.getActivities() != null && entity.getActivities().size() > 0) {
                    ActScoreDialog dialog = new ActScoreDialog(IndexHome.this, R.style.dialog_untran, entity.getActivities(), 0);
                    dialog.show();
                }
            }

            @Override
            public void fail(int code, String error) {
                toast(error);
            }
        });
    }

    private final Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    getSubscribeTopic();
                    break;
                case 3:
                    //通知服务器别名绑定成功
                    BaseParams params = new BaseParams(API.NOTE_SERVICE_ALIAS_SUCCESS);
                    OkHttpUtil.get(params, new ResponseHandler() {
                        @Override
                        public void success(String msg) {
                            if (Constant.SHOW_LOG) {
                                Logger.d("通知服务器绑定成功");
                            }
                        }

                        @Override
                        public void fail(int code, String error) {
                            if (Constant.SHOW_LOG) {
                                Logger.d("通知服务器绑定失败");
                            }
                        }
                    });
                    break;
                case 4:
                    unSubscribeTopics();
                    break;
            }
            return false;
        }
    };

    private final Handler handler = new Handler(callback);

    /**
     * 先清除Push订阅的Topics
     */
    private void unSubscribeTopics() {
        YunBaManager.getTopicList(getApplicationContext(), new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                JSONObject result = iMqttToken.getResult();
                try {
                    JSONArray array = result.getJSONArray("topics");
                    if (array != null) {
                        ArrayList<String> list = new ArrayList<String>();
                        for (int i = 0; i < array.length(); i++) {
                            list.add(array.get(i).toString());
                        }
                        if (list.size() > 0) {
                            String[] topics = list.toArray(new String[list.size()]);
                            YunBaManager.unsubscribe(getApplicationContext(), topics, new IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken iMqttToken) {
                                    String topic = PushUtils.join(iMqttToken.getTopics(), ",");
                                    if (Constant.SHOW_LOG) {
                                        Logger.d("UnSubscribe succeed : " + topic);
                                    }
                                    SubsciribeTopiscs();
                                }

                                @Override
                                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                                    SubsciribeTopiscs();
                                }
                            });
                        } else {
                            SubsciribeTopiscs();
                        }
                    } else {
                        SubsciribeTopiscs();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Logger.d("gettopiclist failed : " + throwable.getMessage());
                unSubscribeTopics();
            }
        });
    }

    /**
     * 重新订阅Topics
     */
    private void SubsciribeTopiscs() {
        YunBaManager.subscribe(getApplicationContext(), new String[]{"topic_static_all", "topic_static_android", "topic_static_city_" + CityPref.getInstance().getCityId()}, GatherApplication.getInstance().pushListener);
        YunBaManager.setAlias(getApplicationContext(), UserPref.getInstance().getAlias(), new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Logger.d("Alias succeed : " + asyncActionToken.getAlias());
                handler.sendEmptyMessage(3);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Logger.d("Alias failed : " + exception.getMessage());
            }
        });
        handler.sendEmptyMessage(0);
    }

    /**
     * 获取需要订阅的频道
     */
    private void getSubscribeTopic() {
        BaseParams params = new BaseParams(API.SUBSCRIBE_TOPIC);
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                try {
                    JSONObject object = new JSONObject(msg);
                    JSONArray array = object.getJSONArray("topic_list");
                    ArrayList<String> list = new ArrayList<String>();
                    for (int i = 0; i < array.length(); i++) {
                        list.add(array.get(i).toString());
                    }
                    if (list.size() > 0) {
                        String[] topics = list.toArray(new String[list.size()]);
                        YunBaManager.subscribe(getApplicationContext(), topics, GatherApplication.getInstance().pushListener);
                        if (Constant.SHOW_LOG) {
                            YunBaManager.getTopicList(getApplicationContext(), new IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken iMqttToken) {
                                    JSONObject result = iMqttToken.getResult();
                                    try {
                                        JSONArray topics = result.getJSONArray("topics");
                                        Logger.d(topics.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                                    Logger.d("gettopiclist failed : " + throwable.getMessage());
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(int code, String error) {
                if (Constant.SHOW_LOG) {
                    toast(error);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventCenter.getInstance().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationUtils.getInstance().stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocation();
        checkActScroe();
//        getMessage();
    }

//    /**
//     * 获取新消息(主要用于红点的提示)
//     */
//    private void getMessage() {
//        BaseParams params = new BaseParams(API.GET_MESSAGE_LIST);
//        params.put("type", 2);
//        String lastTime = TimePref.getInstance().getOrgActMsgTime();
//        if (TextUtils.isEmpty(lastTime)) {
//            lastTime = TimeUtil.getFormatedTime("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis() - 7 * 24 * 3600 * 1000);
//        }
//        params.put("last_requested_time", lastTime);
//        OkHttpUtil.get(params, new ResponseHandler() {
//            @Override
//            public void success(String result) {
//                MessageListEntity entity = JSON.parseObject(result, MessageListEntity.class);
//                if (entity != null && !entity.getLast_requested_time().equals("") && entity.getMessages() != null) {
//                    TimePref.getInstance().setOrgActMsgTime(entity.getLast_requested_time());
//                    for (int i = 0; i < entity.getMessages().size(); i++) {
//                        MessageEntity model = entity.getMessages().get(i);
//                        OrgActMsg msg = new OrgActMsg();
//                        msg.setId(model.getId());
//                        msg.setUserId(UserPref.getInstance().getUserInfo().getUid());
//                        msg.setTeamName(model.getTeam_name());
//                        msg.setContent(model.getContent());
//                        msg.setType(model.getType());
//                        msg.setAttribute(model.getAttributes());
//                        msg.setCreatedAt(model.getCreated_at());
//                        msg.setReaded(false);
//                        GatherApplication.getInstance().getDaoSession().getOrgActMsgDao().insertOrReplace(msg);
//                    }
//                    if (entity.getMessages().size() > 0) {
//                        handler.sendEmptyMessage(1);
//                    } else {
//                        getSystemMessage();
//                    }
//                }
//            }
//
//            @Override
//            public void fail(int code, String error) {
//
//            }
//        });
//    }
//
//    /**
//     * 获取系统消息
//     */
//    private void getSystemMessage() {
//        BaseParams params = new BaseParams(API.GET_MESSAGE_LIST);
//        params.put("type", 1);
//        String lastTime = TimePref.getInstance().getSystemMsgTime();
//        if (TextUtils.isEmpty(lastTime)) {
//            lastTime = TimeUtil.getFormatedTime("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis() - 7 * 24 * 3600 * 1000);
//        }
//        params.put("last_requested_time", lastTime);
//        OkHttpUtil.get(params, new ResponseHandler() {
//            @Override
//            public void success(String result) {
//                MessageListEntity entity = JSON.parseObject(result, MessageListEntity.class);
//                if (entity != null && !entity.getLast_requested_time().equals("") && entity.getMessages() != null) {
//                    TimePref.getInstance().setSystemMsgTime(entity.getLast_requested_time());
//                    for (int i = 0; i < entity.getMessages().size(); i++) {
//                        MessageEntity model = entity.getMessages().get(i);
//                        SystemMsg msg = new SystemMsg();
//                        msg.setUserId(UserPref.getInstance().getUserInfo().getUid());
//                        msg.setId(model.getId());
//                        msg.setContent(model.getContent());
//                        msg.setType(model.getType());
//                        msg.setAttribute(model.getAttributes());
//                        msg.setCreatedAt(model.getCreated_at());
//                        msg.setReaded(false);
//                        GatherApplication.getInstance().getDaoSession().getSystemMsgDao().insertOrReplace(msg);
//                    }
//                    if (entity.getMessages().size() > 0) {
//                        handler.sendEmptyMessage(1);
//                    }
//                }
//            }
//
//            @Override
//            public void fail(int code, String error) {
//
//            }
//        });
//    }


    /**
     * 检查定位信息
     */
    private void checkLocation() {
        if (CityPref.getInstance().isAutoLocation()) {
            LocationUtils.getInstance().getDetailLocation(listener);
        }
    }

    private LocationUtils.OnLocationListener listener = new LocationUtils.OnLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            LocationUtils.getInstance().stop();
            String city = location.getCity();
            if (!Checker.isEmpty(city)) {
                CityPref cityPref = CityPref.getInstance();
                HashMap<String, Integer> cityMap = cityPref.getCityMap();
                if (cityMap != null) {
                    String birefCity = city.length() > 2 ? city.substring(0, city.length() - 1) : city;
                    //已开放的城市包含定位城市
                    if (cityMap.containsKey(birefCity)) {
                        int lastId = cityPref.getCityId();
                        int curId = cityMap.get(birefCity);
                        if (lastId != curId) {
                            if (lastId != -1) {
                                //删除城市订阅
                                YunBaManager.unsubscribe(getApplicationContext(), "topic_static_city_" + lastId, GatherApplication.getInstance().pushListener);
                            }
                            //加城市订阅
                            YunBaManager.subscribe(getApplicationContext(), "topic_static_city_" + curId, GatherApplication.getInstance().pushListener);
                        }
                        cityPref.setSelectedCity(birefCity, curId);

                    } else {
                        showCityUnsuseTipsDialog();
                    }
                }
            }
        }
    };

    /**
     * 当用户选择的是自动切换城市时，如果自动定位到的城市未开放，则弹出窗口提醒用户去选择
     */
    private void showCityUnsuseTipsDialog() {
        if (mCitydialog == null) {
            mCitydialog = DialogCreater.createTipsDialog(this, "温馨提示", getString(R.string.auto_location_city_is_unuse), "选择城市", false, new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    mCitydialog.dismiss();
                    selectCity();
                }
            });
        }
        mCitydialog.show();
    }

    private void selectCity() {
        Intent intent = new Intent(this, SelectCity.class);
        startActivity(intent);
    }

    /**
     * 检查定位，如果未选择城市先跳选择成，如果已选城市则跳首页
     * 在indexhome里检查跳转选择城市会出现一些莫名其妙的问题
     */
    public static void checkAndStartIndexActivity(Activity activity){
        Intent intent = null;
        if (CityPref.getInstance().hasSelectedCity()) {
            intent = new Intent(activity, IndexHome.class);
        }
        else {
            intent = new Intent(activity, SelectCity.class);
            intent.putExtra(SelectCity.EXTRA_BACK_TO_INDEX, true);
        }
        activity.startActivity(intent);
    }
}
