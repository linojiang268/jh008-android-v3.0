package com.gather.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseMapActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.ActEntity;
import com.gather.android.entity.ActListEntity;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.utils.LocationUtils;
import com.gather.android.widget.TitleBar;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by Levi on 2015/8/10.
 */
public class NearbyActMap extends BaseMapActivity {
    private static final int NEARBY_DISTANCE = 50;//搜索范围（千米）
    private static final int RELOAD_CENTER_DISPLACEMENT = 25;//默认中心位移重载距离（千米）

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.mapview)
    MapView mapview;
    @InjectView(R.id.btn_mylocation)
    ImageButton btnMylocation;

    private List<ActEntity> actList;
    private LoadingDialog loadingDialog;
    private MapHandler handler = new MapHandler();

    private boolean isRefreshing = false;
    private boolean isCalculating = false;
    private boolean isInfowindowShowing = false;
    private InfoWindow infoWindow;
    private LatLng centerLatLng;
    private String focusActId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_act_map);
        setSupportMap(mapview);

        titlebar.setHeaderTitle(R.string.nearby_act);
        titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity();
            }
        });

        btnMylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animToMyLocation();
            }
        });

        loadingDialog = LoadingDialog.createDialog(this, true);
        loadingDialog.setMessage(getString(R.string.is_refreshing_nearby_act));

    }

    @Override
    public void onLocation(boolean isFirst) {
        if (isFirst){
            centerLatLng = getMyLatLng();
            animToMyLocation();
            refreshNearbyAct();
        }
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_MOVE:
                hideActInfoWindow();
                break;
            case MotionEvent.ACTION_UP:
                calculatCenterDisplacement();
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        try {
            int pos = Integer.parseInt(marker.getTitle());
            ActEntity entity = actList.get(pos);
            if (!entity.getId().equals(focusActId)){
                showActInfoWindow(entity);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 计算中心位移
     */
    private void calculatCenterDisplacement(){
        if (isCalculating || isRefreshing)return;
        isCalculating = true;
        //避免地图滑动惯性的误差
        handler.sendEmptyMessageDelayed(99, 1000);
    }

    private class  MapHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 99:
                    Point screenCenterPoint = new Point(mapview.getWidth()/2, mapview.getHeight()/2);
                    LatLng screenCenterLatLng = getBaiduMap().getProjection().fromScreenLocation(screenCenterPoint);
                    double disInKm = LocationUtils.getDistanceInKm(screenCenterLatLng, centerLatLng);
                    if (disInKm >= RELOAD_CENTER_DISPLACEMENT){
                        centerLatLng = screenCenterLatLng;
                        refreshNearbyAct();
                    }
                    isCalculating = false;
                    break;
                case 100:
                    getBaiduMap().showInfoWindow(infoWindow);
                    break;
            }
        }
    }

    private void refreshNearbyAct(){
        if (isRefreshing)return;
        loadingDialog.show();
        BaseParams params = new BaseParams(API.GET_NEARBY_ACT);
        params.put("lat", centerLatLng.latitude);
        params.put("lng", centerLatLng.longitude);
        params.put("dist",NEARBY_DISTANCE);
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                loadingDialog.dismiss();
                isRefreshing = false;
                ActListEntity entity = JSON.parseObject(msg, ActListEntity.class);
                actList = entity.getActivities();
                updateActOverlay();
            }

            @Override
            public void fail(int code, String error) {
                toast(error);
                loadingDialog.dismiss();
                isRefreshing = false;
            }
        });
    }

    private void updateActOverlay(){
        clearAllOverlay();
        if (actList != null && actList.size() > 0){
            int n = 0;
            for (ActEntity entity : actList){
                LatLng latLng = entity.getLatLng();
                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_act);
                OverlayOptions overlayOptions = new MarkerOptions()
                        .draggable(false)
                        .icon(descriptor)
                        .zIndex(9)
                        .title(String.valueOf(n))
                        .position(latLng);
                addOverlay(overlayOptions);
                n++;
            }

        }
    }

    private void hideActInfoWindow(){
        if (!isInfowindowShowing)return;
        getBaiduMap().hideInfoWindow();
        isInfowindowShowing = false;
    }

    private void showActInfoWindow(ActEntity entity){
        hideActInfoWindow();
        animToLatLng(entity.getLatLng());
        isInfowindowShowing = true;
        focusActId = entity.getId();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View windowView = inflater.inflate(R.layout.pop_route_map, null);
        TextView tvTitle = (TextView) windowView.findViewById(R.id.tvTitle);
        tvTitle.setText(entity.getTitle());
        windowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NearbyActMap.this, ActDetail.class);
                intent.putExtra(ActDetail.EXTRA_ID, focusActId);
                startActivity(intent);
            }
        });
        //density = 3时76 * 2刚好，所以换算下
        int offsetY = -(int) (76 * getResources().getDisplayMetrics().density / 3 * 2 + 2);
        infoWindow = new InfoWindow(windowView, entity.getLatLng(), offsetY);
        handler.sendEmptyMessageDelayed(100, 400);
    }
}
