package com.gather.android.utils;



import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.gather.android.Constant;
import com.gather.android.GatherApplication;
import com.gather.android.event.EventCenter;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

public class LocationUtils implements  BDLocationListener{
	private static final LocationUtils instance = new LocationUtils();

    public static LocationUtils getInstance(){
        return instance;
    }

    public static double getDistanceInMeter(LatLng start, LatLng end){
        if (start == null || end == null) return 0;
        //该方法返回的结果是米
        double meter = DistanceUtil.getDistance(start, end);
        return meter;
    }

    public static double getDistanceInKm(LatLng start, LatLng end){
        double meter = getDistanceInMeter(start, end);
        return meter / 1000d;
    }

    private final LocationClient mLocationClient = new LocationClient(GatherApplication.getInstance());
    private BDLocation myLocation = null;
    private WeakReference<OnLocationListener> listenerWp;
	private LocationUtils() {

	}

    public  LocationClient getLocationClient() {
        return mLocationClient;
    }

    public  BDLocation getMyLocation() {
        return myLocation;
    }

    private LocationClientOption getOption(boolean needAddress){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置高精度定位模式
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms
        if (needAddress){
            option.setAddrType("all");
            option.setIsNeedAddress(true);// 是否需要地址信息
        }
        return option;
    }

    public void getDetailLocation(OnLocationListener l){
        if (l == null)return;
        listenerWp = new WeakReference<OnLocationListener>(l);
        start(true);
    }


    public void start(boolean needAddress){
        stop();
        mLocationClient.registerLocationListener(this);

        LocationClientOption option = getOption(needAddress);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    public void start(LocationClientOption option){
        stop();
        if (option != null){
            mLocationClient.registerLocationListener(this);
            mLocationClient.setLocOption(option);
            mLocationClient.start();
        }
    }

    public void stop(){
        if (mLocationClient.isStarted()){
            mLocationClient.stop();
            mLocationClient.unRegisterLocationListener(this);
        }
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location != null){
            StringBuilder sb = new StringBuilder(256);
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            if (location.hasRadius()){
                sb.append("\nradius : ");
                sb.append(location.getRadius());
            }
            if (location.hasAddr()) {
                sb.append("\nprovince : ");
                sb.append(location.getProvince());
                sb.append("\ncity : ");
                sb.append(location.getCity());
                sb.append("\ncity code : ");
                sb.append(location.getCityCode());
                sb.append("\nstreet : ");
                sb.append(location.getStreet());
                sb.append("\ndistrict : ");
                sb.append(location.getDistrict());
                sb.append("\naddress : ");
                sb.append(location.getAddrStr());
                if (listenerWp != null && listenerWp.get() != null){
                    listenerWp.get().onReceiveLocation(location);
                }
            }
            else {
                EventCenter.getInstance().post(location);
            }
            if (Constant.SHOW_LOG) {
                Logger.d("location:" + sb.toString());
            }
            myLocation = location;
        }
    }

    public interface OnLocationListener {
        public void onReceiveLocation(BDLocation location);
    }

}
