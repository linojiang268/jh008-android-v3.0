package com.gather.android.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.gather.android.R;
import com.gather.android.baseclass.BaseMapActivity;
import com.gather.android.dialog.DialogCreater;
import com.gather.android.entity.VenueModel;
import com.gather.android.widget.TitleBar;
import com.jihe.dialog.entity.DialogMenuItem;
import com.jihe.dialog.listener.OnOperItemClickL;

import java.net.URISyntaxException;
import java.util.ArrayList;

import butterknife.InjectView;

/**
 * 活动地点
 * Created by Levi on 2015/8/7.
 */
public class VenueMap extends BaseMapActivity {
    public static final String EXTRA_MODEL = "extra_model";

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.mapview)
    MapView mapview;
    @InjectView(R.id.btnNavigation)
    ImageButton btnNavigation;
    @InjectView(R.id.btnMyLocation)
    ImageButton btnMyLocation;


    VenueModel model;

    InfoWindow infoWindow;
    boolean isInfoWindowShowing = false;
    Handler handler = new Handler();

    private Dialog mNavDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.venue_map);
        setSupportMap(mapview);

        titlebar.setHeaderTitle(R.string.act_venue);
        titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animToMyLocation();
            }
        });

        btnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationClick();
            }
        });

        model = (VenueModel) getIntent().getSerializableExtra(EXTRA_MODEL);
        setActMarker();
    }

    private void setActMarker(){
        LatLng latLng = model.getLatLng();
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_act);
        OverlayOptions overlayOptions = new MarkerOptions()
                .draggable(false)
                .icon(descriptor)
                .zIndex(9)
                .title(String.valueOf(model.getId()))
                .position(latLng);
        addDefOverlay(overlayOptions);
        animToLatLng(latLng);
    }

    private void navigationClick(){
        if (mNavDialog == null){
            ArrayList<DialogMenuItem> list = new ArrayList<>();
            list.add(new DialogMenuItem("百度地图", R.drawable.icon_baidumap));
            list.add(new DialogMenuItem("高德地图", R.drawable.icon_gaodemap));
            mNavDialog = DialogCreater.createImageListDialog(this, "请选择导航工具", list, new OnOperItemClickL() {
                @Override
                public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mNavDialog.dismiss();
                    Intent intent = null;
                    if (position == 0){
                        intent = getBaiduMapIntent();
                    }
                    else {
                        intent = getGaodeMapIntent();
                    }
                    try {
                        startActivity(intent);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        if (position == 0){
                            toast("您还没有安装百度地图");
                        }
                        else {
                            toast("您还没有安装高德地图");
                        }
                    }
                }
            });
        }
        mNavDialog.show();
    }

    /**
     * 获取百度地图导航跳转intent
     * @return
     */
    private Intent getBaiduMapIntent(){
        LatLng pt1 = getMyLatLng();
        LatLng pt2 = model.getLatLng();
        StringBuilder sb = new StringBuilder();
        sb.append("intent://map/direction?origin=latlng:");
        sb.append(pt1.latitude);
        sb.append(",");
        sb.append(pt1.longitude);
        sb.append("|name:我的位置&destination=latlng:");
        sb.append(pt2.latitude);
        sb.append(",");
        sb.append(pt2.longitude);
        sb.append("|name:");
        sb.append(model.getAddress());
        sb.append("&mode=driving&region=");
        sb.append(model.getCity());
        sb.append("&src=成都零创科技有限公司|集合#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
        try {
            Intent intent = Intent.getIntent(sb.toString());
            return intent;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取高德地图导航跳转intent
     * @return
     */
    private Intent getGaodeMapIntent(){
        LatLng latLng = model.getLatLng();
        StringBuilder sb = new StringBuilder();
        sb.append("androidamap://navi?sourceApplication=集合&poiname=");
        sb.append(model.getAddress());
        sb.append("&lat=");
        sb.append(latLng.latitude);
        sb.append("&lon=");
        sb.append(latLng.longitude);
        sb.append("&dev=1&style=2");
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse(sb.toString()));
        intent.setPackage("com.autonavi.minimap");
        return intent;
    }

//    private boolean isBaiduMapInstalled()
//    {
//        PackageInfo packageInfo;
//        try {
//            packageInfo = getPackageManager().getPackageInfo("com.baidu.BaiduMap", 0);
//        }catch (PackageManager.NameNotFoundException e) {
//            packageInfo = null;
//            e.printStackTrace();
//        }
//        if(packageInfo ==null){
//            //System.out.println("没有安装");
//            return false;
//        }else{
//            //System.out.println("已经安装");
//            return true;
//        }
//    }

    private void showActInfoWindow(){
        animToLatLng(model.getLatLng());
        if (infoWindow == null){
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View windowView = inflater.inflate(R.layout.pop_venue_map, null);
            TextView tvTitle = (TextView) windowView.findViewById(R.id.tvTitle);
            tvTitle.setText(model.getTitle());
            windowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigationClick();
                }
            });
            //density = 3时76 * 2刚好，所以换算下
            int offsetY = -(int) (76 * getResources().getDisplayMetrics().density / 3 * 2 + 2);
            infoWindow = new InfoWindow(windowView, model.getLatLng(), offsetY);
        }
        isInfoWindowShowing = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getBaiduMap().showInfoWindow(infoWindow);
            }
        }, 400);
    }

    private void hideActInfoWindow(){
        if (!isInfoWindowShowing)return;
        isInfoWindowShowing = false;
        getBaiduMap().hideInfoWindow();
    }

    @Override
    public void onLocation(boolean isFirst) {

    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE){
            hideActInfoWindow();
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!isInfoWindowShowing){
            showActInfoWindow();
        }
        else {
            hideActInfoWindow();
        }
        return true;
    }
}
