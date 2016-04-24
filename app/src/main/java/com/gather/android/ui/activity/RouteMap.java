package com.gather.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gather.android.API;
import com.gather.android.R;
import com.gather.android.baseclass.BaseMapActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.MemberEntity;
import com.gather.android.entity.MemberListEntity;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.widget.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Levi on 2015/8/7.
 */
public class RouteMap extends BaseMapActivity {
    public static final String EXTRA_ROUTEMAP = "extra_route";
    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_MEMBER = "extra_member";

    private static final int MESSAGE_REFRESH_MEMBERS = 22;
    private static final int MESSAGE_ZOOMLEVEL = 33;
    private static final int MESSAGE_COUNT_ZOOM = 44;
    private static final int MESSAGE_SHOW_INFOWINDOW = 55;

    private static final int DELAY_REFRESH_MEMBERS = 30000;

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.mapview)
    MapView mapview;
    @InjectView(R.id.btnMylocation)
    ImageButton btnMylocation;
    @InjectView(R.id.cbMembers)
    CheckBox cbMembers;

    List<MemberEntity> membersList;
    MemberEntity focusedEntity;
    View infoWindowView;

    String actId;
    ArrayList<double[]> routeMap;
    boolean isActMember = false;//是否加入活动
    boolean isRefreshing = false;
    boolean isInfoWindowShowing = false;
    MapHandler handler = new MapHandler();
    LoadingDialog loadingDialog;

    float zoomlevel = -1;//缩放等级
    LatLng startLatLng;//起点坐标
    LatLng northWestLl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_map);
        ButterKnife.inject(this);
        setSupportMap(mapview);

        Intent intent = getIntent();
        routeMap = (ArrayList<double[]>) intent.getSerializableExtra(EXTRA_ROUTEMAP);
        actId = intent.getStringExtra(EXTRA_ID);
        if (intent.hasExtra(EXTRA_MEMBER)) {
            isActMember = true;
        }

        titlebar.setHeaderTitle(R.string.route_chart);
        titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnMylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animToMyLocation();
            }
        });
        if (isActMember){
            cbMembers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setMembersVisible(isChecked);
                }
            });
        }
        else {
            cbMembers.setVisibility(View.GONE);
        }


        setRouteToMap();
    }

    @Override
    protected void onDestroy() {
        handler.removeMessages(MESSAGE_REFRESH_MEMBERS);
        handler.removeMessages(MESSAGE_SHOW_INFOWINDOW);
        handler.removeMessages(MESSAGE_ZOOMLEVEL);
        handler.removeMessages(MESSAGE_COUNT_ZOOM);
        super.onDestroy();
    }

    private void setRouteToMap(){
        loadingDialog = LoadingDialog.createDialog(this, true);
        loadingDialog.setMessage(getString(R.string.is_build_venue));
        loadingDialog.show();
        new BuildRouteMapTask().execute();
    }

    /**
     * 设置周围成员是否可见
     * @param visible
     */
    private void setMembersVisible(boolean visible){
        if (visible){
            if (membersList != null && membersList.size() > 0){
                showMembersOverlay();
                delayToRefreshMembers();
            }
            else {
                refreshNearMembersOfAct();
            }
        }
        else {
            handler.removeMessages(MESSAGE_REFRESH_MEMBERS);
            clearOverlayExceptDef();
            isInfoWindowShowing = false;
            focusedEntity = null;
        }
    }


    private class MapHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_REFRESH_MEMBERS:
                    if (cbMembers.isChecked()) {
                        refreshNearMembersOfAct();
                    }
                    break;
                case MESSAGE_ZOOMLEVEL:
                    if (zoomlevel == -1){
                        zoomlevel = MAX_ZOOM_LEVEL;
                    }
                    else {
                        zoomlevel--;
                    }
                    setZoomLevi(zoomlevel);
                    handler.sendEmptyMessageDelayed(MESSAGE_COUNT_ZOOM, 300);
                    break;
                case MESSAGE_COUNT_ZOOM:
                    boolean goOn = true;
                    if (getBaiduMap() != null && getBaiduMap().getProjection() != null){
                        Point point = getBaiduMap().getProjection().toScreenLocation(northWestLl);
                        //路线区域的西北角在当前缩放等级的屏幕坐标在屏幕中，即x>0,y>0则当前屏幕已完整显示路线图
                        if (point.x > 0 && point.y > 0){
                            goOn = false;
                        }
                        else if (zoomlevel == MIN_ZOOM_LEVEL){
                            //路线跨度太大已经超出范围，以起点位置为中心位置
                            animToLatLng(startLatLng);
                            goOn = false;
                        }
                    }
                    if (goOn){
                        handler.sendEmptyMessageDelayed(MESSAGE_ZOOMLEVEL, 10);
                    }
                    else {
                        mapview.setVisibility(View.VISIBLE);
                        loadingDialog.dismiss();
                    }
                    break;
                case MESSAGE_SHOW_INFOWINDOW:
                    if (isInfoWindowShowing){
                        //density = 3时115 * 2刚好，所以换算下
                        int offsetY = -(int) (76 * getResources().getDisplayMetrics().density / 3 * 2 + 2);
                        InfoWindow infoWindow = new InfoWindow(infoWindowView, focusedEntity.getLatLng(), offsetY);
                        getBaiduMap().showInfoWindow(infoWindow);
                    }
                    break;
            }
        }
    }

    private void refreshNearMembersOfAct(){
        if (isRefreshing)return;
        cbMembers.setEnabled(false);
        LatLng latLng = getMyLatLng();
        isRefreshing = true;
        BaseParams params = new BaseParams(API.GET_NEAR_ACT_MEMBERS);
        params.put("activity_id", actId);
        params.put("lat", latLng.latitude);
        params.put("lng", latLng.longitude);
        OkHttpUtil.get(params, new ResponseHandler() {
            @Override
            public void success(String msg) {
                cbMembers.setEnabled(true);
                isRefreshing = false;
                try {
                    MemberListEntity entity = JSON.parseObject(msg, MemberListEntity.class);
                    membersList = entity.getMembers();
                    showMembersOverlay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                delayToRefreshMembers();
            }

            @Override
            public void fail(int code, String error) {
                cbMembers.setEnabled(true);
                isRefreshing = false;

//                String[] USER_ICONS = new String[]{"http://img.store.sogou.com/net/a/05/link?appid=100520033&url=http%3A%2F%2Fwww.qqw21.com%2Farticle%2FUploadPic%2F2012-10%2F201210229441110298.jpg",
//                        "http://p2.gexing.com/touxiang/20120802/0922/5019d66eef7ed_200x200_3.jpg",
//                        "http://img3.imgtn.bdimg.com/it/u=2117727038,2641018931&fm=21&gp=0.jpg",
//                        "http://www.qq1234.org/uploads/allimg/150606/8_150606104229_1.jpg",
//                        "http://www.qjis.com/uploads/allimg/130401/110439A91-5.jpg",
//                        "http://p2.gexing.com/G1/M00/F2/71/rBACFFHqdVHSg4G6AAAeunmMrXE212_200x200_3.jpg"};
//                if (membersList == null) {
//                    membersList = new ArrayList<MemberEntity>();
//                    LatLng myLatLng = getMyLatLng();
//                    for (int i = 0; i < USER_ICONS.length; i++) {
//                        LatLng latLng = new LatLng(myLatLng.latitude + new Random().nextDouble() * 0.05d, myLatLng.longitude + new Random().nextDouble() * 0.05d);
//                        MemberEntity entity = new MemberEntity();
//                        entity.setAvatar_url(USER_ICONS[i]);
//                        entity.setName("Member" + i);
//                        entity.setLat(latLng.latitude);
//                        entity.setLng(latLng.longitude);
//                        entity.setRole(1);
//                        entity.setUser_id(i + "");
//                        membersList.add(entity);
//                    }
//                }
//                showMembersOverlay();

                delayToRefreshMembers();
            }
        });
    }

    /**
     *显示member覆盖物
     */
    private void showMembersOverlay(){
        if (!cbMembers.isChecked() || isInfoWindowShowing)return;
        clearOverlayExceptDef();
        if (membersList != null && membersList.size() > 0){
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_act);
            for (int i = 0; i < membersList.size(); i++) {
                OverlayOptions overlayOptions = new MarkerOptions()
                        .draggable(false)
                        .icon(descriptor)
                        .zIndex(9)
                        .title(String.valueOf(i))
                        .position(membersList.get(i).getLatLng());
                addOverlay(overlayOptions);
            }
        }
    }

    /**
     * 显示member pop
     * @param entity
     */
    private void showMemberInfoWindow(MemberEntity entity){
        hideMemberInfoWindow();
        isInfoWindowShowing = true;
        animToLatLng(entity.getLatLng());
        focusedEntity = entity;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        infoWindowView = inflater.inflate(R.layout.pop_route_map, null);
        TextView tvTitle = (TextView) infoWindowView.findViewById(R.id.tvTitle);
        SimpleDraweeView ivUserIcon = (SimpleDraweeView) infoWindowView.findViewById(R.id.ivUserIcon);
        ivUserIcon.setImageURI(Uri.parse(entity.getAvatar_url()));
        tvTitle.setText(entity.getName());
        infoWindowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RouteMap.this, UserCenter.class);
                intent.putExtra("USER_ID", focusedEntity.getUser_id());
                startActivity(intent);
            }
        });
        handler.sendEmptyMessageDelayed(MESSAGE_SHOW_INFOWINDOW, 400);
    }

    private void hideMemberInfoWindow(){
        if (!isInfoWindowShowing)return;
        getBaiduMap().hideInfoWindow();
        isInfoWindowShowing = false;
        focusedEntity = null;
    }

    private void delayToRefreshMembers(){
        handler.sendEmptyMessageDelayed(MESSAGE_REFRESH_MEMBERS, DELAY_REFRESH_MEMBERS);
    }

    private class BuildRouteMapTask extends AsyncTask<Void, Void, LatLng> {

        @Override
        protected LatLng doInBackground(Void... voids) {
            //起点overlay
            OverlayOptions startOverlay = null;
            //终点overlay
            OverlayOptions endOverlay = null;
            //区域
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            List<LatLng> pointList = new ArrayList<>();
            for (int i = 0 ; i < routeMap.size(); i++){
                double[] point = routeMap.get(i);
                LatLng latLng = new LatLng(point[0], point[1]);
                pointList.add(latLng);
                builder.include(latLng);
                if (i == 0){
                    startLatLng = latLng;
                    //构建起点overlay
                    startOverlay  = createPointOverlay(R.drawable.ic_route_start, "start", latLng);
                }
                else if (i == routeMap.size() - 1){
                    //构建终点overlay
                    endOverlay = createPointOverlay(R.drawable.ic_route_end, "end", latLng);
                }
            }
            //路线覆盖物
            OverlayOptions overlayOptions = new PolylineOptions().width(8).color(getResources().getColor(R.color.route_color)).points(pointList);
            addDefOverlay(overlayOptions);
            addDefOverlay(startOverlay);
            addDefOverlay(endOverlay);

            //区域绑定
            LatLngBounds bounds = builder.build();
            //区域西北角坐标
            northWestLl = new LatLng(bounds.northeast.latitude, bounds.southwest.longitude);

            return bounds.getCenter();
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            clearOverlayExceptDef();
            animToLatLng(latLng);
            startFormatZoomLevel();
        }
    }

    @Override
    public void onLocation(boolean isFirst) {
        if (isFirst){
            if (isActMember) {
                refreshNearMembersOfAct();
            }
        }
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        hideMemberInfoWindow();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        try {
            int position = Integer.parseInt(marker.getTitle());
            MemberEntity entity = membersList.get(position);
            if (focusedEntity == null || !entity.getUser_id().equals(focusedEntity.getUser_id())) {
                showMemberInfoWindow(entity);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 创建新的起点和终点overlay
     * @param resid
     * @param title
     * @param latLng
     * @return
     */
    private OverlayOptions createPointOverlay(int resid, String title, LatLng latLng){
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(resid);
        OverlayOptions overlay = new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .draggable(false)
                .icon(descriptor)
                .title(title)
                .zIndex(9)
                .position(latLng);
        return overlay;
    }

    private void startFormatZoomLevel(){
        handler.sendEmptyMessage(MESSAGE_ZOOMLEVEL);
    }

}
