package com.gather.android.baseclass;

import android.graphics.Point;
import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.gather.android.GatherApplication;
import com.gather.android.event.EventCenter;
import com.gather.android.utils.LocationUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.Subscribe;

/**
 * Created by Levi on 2015/8/7.
 */
public abstract class BaseMapActivity extends BaseActivity implements BaiduMap.OnMarkerClickListener, BaiduMap.OnMapTouchListener{
    protected static final float MAX_ZOOM_LEVEL = 20f;
    protected static final float MIN_ZOOM_LEVEL = 10f;

    private MapView mSupportMapView;
    private BaiduMap mBaiduMap;
    private LatLng mMyLatLng;
    private boolean isFirstLoc = true;
    private List<OverlayOptions> mDefOverlayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(GatherApplication.getInstance());
        EventCenter.getInstance().register(this);
        LocationUtils.getInstance().start(false);
    }

    @Override
    protected void onResume() {
        if (mSupportMapView != null){
            mSupportMapView.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mSupportMapView != null){
            mSupportMapView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mBaiduMap != null){
            mBaiduMap.setMyLocationEnabled(false);
        }
        if (mSupportMapView != null){
            mSupportMapView.onDestroy();
            mSupportMapView = null;
        }
        super.onDestroy();
        LocationUtils.getInstance().stop();
        EventCenter.getInstance().unregister(this);
    }

    public void setSupportMap(MapView mapView){
        if (mapView == null){
            throw new NullPointerException("MapView can't be null");
        }
        mSupportMapView = mapView;
        mBaiduMap = mapView.getMap();

        mSupportMapView.showZoomControls(false);
        mSupportMapView.showScaleControl(false);

        mBaiduMap.setTrafficEnabled(false);
        mBaiduMap.setMaxAndMinZoomLevel(MAX_ZOOM_LEVEL, MIN_ZOOM_LEVEL);

        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null));


        mBaiduMap.setOnMarkerClickListener(this);
        mBaiduMap.setOnMapTouchListener(this);


    }

    public BaiduMap getBaiduMap(){
        return mBaiduMap;
    }

    public LatLng getMyLatLng(){
        return mMyLatLng;
    }

    public void animToMyLocation(){
        animToLatLng(mMyLatLng);
    }

    public void addDefOverlay(OverlayOptions overlay){
        if (overlay != null){
            mDefOverlayList.add(overlay);
            mBaiduMap.addOverlay(overlay);
        }
    }

    public void clearDefOverlay(){
        mDefOverlayList.clear();
    }

    public void clearOverlayExceptDef(){
        mBaiduMap.clear();
        for (OverlayOptions overlay : mDefOverlayList){
            mBaiduMap.addOverlay(overlay);
        }
    }

    public void clearAllOverlay(){
        mBaiduMap.clear();
    }

    public void addOverlay(OverlayOptions overlay){
        if (overlay != null){
            mBaiduMap.addOverlay(overlay);
        }
    }

    public LatLng getLatLngOfScreenCenter(){
        Point screenCenterPoint = new Point(mSupportMapView.getWidth()/2, mSupportMapView.getHeight()/2);
        return mBaiduMap.getProjection().fromScreenLocation(screenCenterPoint);
    }

    public void setZoomLevi(float level){
        float realLevel = level;
        if (level < MIN_ZOOM_LEVEL){
            realLevel = MIN_ZOOM_LEVEL;
        }
        else if (level > MAX_ZOOM_LEVEL){
            realLevel = MAX_ZOOM_LEVEL;
        }
        MapStatusUpdate zoom = MapStatusUpdateFactory.zoomTo(realLevel);
        mBaiduMap.animateMapStatus(zoom);
    }

    /**
     * 以某点为中心
     * @param latLng
     */
    public void animToLatLng(LatLng latLng){
        if (latLng != null){
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.animateMapStatus(u);
        }
    }

    @Subscribe
    public void onEvent(BDLocation location){
        // map view 销毁后不在处理新接收的位置
        if (location == null || mSupportMapView == null) {
            return;
        }
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .direction(location.getDirection())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        mMyLatLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        if (isFirstLoc) {
            isFirstLoc = false;
            onLocation(true);
        }
        else {
            onLocation(false);
        }
    }

    public abstract void onLocation(boolean isFirst);
}
