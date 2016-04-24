package com.gather.android.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.gather.android.API;
import com.gather.android.GatherApplication;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.baseclass.BaseParams;
import com.gather.android.data.CityPref;
import com.gather.android.dialog.DialogCreater;
import com.gather.android.dialog.LoadingDialog;
import com.gather.android.entity.CityEntity;
import com.gather.android.event.ChangeCityEvent;
import com.gather.android.event.EventCenter;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.http.ResponseHandler;
import com.gather.android.utils.Checker;
import com.gather.android.utils.LocationUtils;
import com.gather.android.widget.TitleBar;
import com.jihe.dialog.listener.OnBtnClickL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;
import io.yunba.android.manager.YunBaManager;

/**
 * Created by Levi on 2015/7/24.
 */
public class SelectCity extends BaseActivity {
    public static final String EXTRA_BACK_TO_INDEX = "bt_index";

    @InjectView(R.id.titlebar)
    TitleBar titlebar;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.tvCity)
    TextView tvCity;
    @InjectView(R.id.ivSelected)
    ImageView ivSelected;
    @InjectView(R.id.llCity)
    LinearLayout llCity;

    private LoadingDialog loadingDialog;

    //是否自动定位（切换）城市
    private boolean isAutoLocation = false;
    //正在自动定位
    private boolean isGetLocation = false;

    private final List<CityEntity> cityList = new ArrayList<>();
    private final HashMap<String, Integer> cityMap = new HashMap<>();
    private CityAdapter adapter;
    //自动定位城市
    private CityEntity autoCity = null;
    private CityEntity selecredCity = null;

    private Dialog mTipsDialog = null;
    private boolean isBackToIndex = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city);
        isBackToIndex = getIntent().getBooleanExtra(EXTRA_BACK_TO_INDEX, false);

        if (CityPref.getInstance().hasSelectedCity()) {
            isAutoLocation = CityPref.getInstance().isAutoLocation();
            selecredCity = new CityEntity();
            selecredCity.setId(CityPref.getInstance().getCityId());
            selecredCity.setName(CityPref.getInstance().getCityName());
        }

        initView();
        initCityList();
        autoLocationCity();
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocationUtils.getInstance().stop();
        //保存选择的城市
        if (selecredCity != null){
            CityPref cityPref = CityPref.getInstance();
            int lastId = cityPref.getCityId();
            int curId = selecredCity.getId();

            cityPref.setAutoLocationEnable(isAutoLocation);
            cityPref.setSelectedCity(selecredCity.getName(), selecredCity.getId());
            if (lastId != curId){
                if (lastId != -1){
                    //删除城市订阅
                    YunBaManager.unsubscribe(getApplicationContext(), "topic_static_city_" + lastId, GatherApplication.getInstance().pushListener);
                }
                //加城市订阅
                YunBaManager.subscribe(getApplicationContext(), "topic_static_city_" + curId, GatherApplication.getInstance().pushListener);
                EventCenter.getInstance().post(new ChangeCityEvent());
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (selecredCity == null){
            toast(R.string.select_city_please);
        }
        else {
            if (isBackToIndex){
                Intent intent = new Intent(this, IndexHome.class);
                startActivity(intent);
                finish();
            }
            else {
                super.onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, R.anim.push_down_out);
            }
        }
    }

    private void initView() {
        titlebar.setHeaderTitle(R.string.select_city);
        titlebar.getBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadingDialog = LoadingDialog.createDialog(this, true);
        loadingDialog.setMessage(getString(R.string.get_city_list));

        if (isAutoLocation){
            ivSelected.setVisibility(View.VISIBLE);
        }
        else {
            ivSelected.setVisibility(View.INVISIBLE);
        }

        llCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isGetLocation) {
                    if (autoCity == null) {
                        autoLocationCity();
                    } else if (autoCity.getId() == -1) {
                        toast(R.string.auto_location_city_is_unuse);
                    } else {
                        isAutoLocation = true;
                        selecredCity = autoCity;
                        onBackPressed();
                    }
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new CityAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 初始化城市列表
     */
    private void initCityList() {
        List<CityEntity> list = CityPref.getInstance().getCityList();
        if (list == null) {
            getCityListFromNet();
        } else {
            refreshAdapter(list);
        }
    }

    /**
     * 自动定位城市
     */
    private void autoLocationCity() {
        isGetLocation = true;
        tvCity.setText(R.string.is_getting_location);
        LocationUtils.getInstance().getDetailLocation(onLocationListener);
    }

    private LocationUtils.OnLocationListener onLocationListener = new LocationUtils.OnLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            isGetLocation = false;
            LocationUtils.getInstance().stop();
            String city = location.getCity();
            if (!Checker.isEmpty(city)) {
                autoCity = new CityEntity();
                String birefCity = city.length() > 2 ? city.substring(0, city.length() - 1) : city;
                //已开放的城市包含定位城市
                if (cityMap.containsKey(birefCity)) {
                    int id = cityMap.get(birefCity);
                    autoCity.setName(birefCity);
                    autoCity.setId(id);
                    tvCity.setText(String.format(getString(R.string.auto_location_city), birefCity));
                    if (isAutoLocation){
                        selecredCity = autoCity;
                    }
                } else {
                    autoCity.setName(city);
                    autoCity.setId(-1);
                    tvCity.setText(String.format(getString(R.string.auto_location_city), city));
                    if (isAutoLocation){
                        showCityUnsuseTipsDialog();
                    }
                }

            } else {
                autoCity = null;
                tvCity.setText(R.string.get_location_fail);
            }
        }
    };

    /**
     * 当用户选择的是自动切换城市时，如果自动定位到的城市未开放，则弹出窗口提醒用户去选择
     */
    private void showCityUnsuseTipsDialog(){
        if (mTipsDialog == null){
            mTipsDialog = DialogCreater.createTipsDialog(this, "温馨提示", getString(R.string.auto_location_city_is_unuse), "确定", false, new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    mTipsDialog.dismiss();
                    selecredCity = null;
                    isAutoLocation = false;
                    ivSelected.setVisibility(View.INVISIBLE);
                }
            });
        }
        mTipsDialog.show();
    }

    /**
     * 从网络获取城市列表
     */
    private void getCityListFromNet() {
        loadingDialog.show();
        OkHttpUtil.get(new BaseParams(API.GET_CITY_LIST), new ResponseHandler() {

            @Override
            public void success(String msg) {
                loadingDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(msg);
                    String citysStr = object.getString("cities");
                    CityPref.getInstance().updateCityList(citysStr);
                    List<CityEntity> list = JSON.parseArray(citysStr, CityEntity.class);
                    refreshAdapter(list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(int code, String error) {
                loadingDialog.dismiss();
                toast(error);
                finish();
            }
        });
    }

    /**
     * 刷新列表
     * @param list
     */
    private void refreshAdapter(List<CityEntity> list) {
        cityList.clear();
        cityMap.clear();
        if (list != null) {
            cityList.addAll(list);
            for (CityEntity entity : cityList) {
                cityMap.put(entity.getName(), entity.getId());
            }
        }
        adapter.notifyDataSetChanged();
    }


    private class CityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;

        public CityAdapter(Context context) {
            super();
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_select_city, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            CityEntity entity = cityList.get(position);

            viewHolder.tvCity.setText(entity.getName());

            if (!isAutoLocation && selecredCity != null && selecredCity.getId() == entity.getId()){
                viewHolder.ivSelect.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.ivSelect.setVisibility(View.INVISIBLE);
            }

            viewHolder.llCity.setOnClickListener(new OnItemClickListener(position));
        }

        @Override
        public int getItemCount() {
            return cityList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout llCity;
            TextView tvCity;
            ImageView ivSelect;

            public ViewHolder(View itemView) {
                super(itemView);
                llCity = (LinearLayout) itemView.findViewById(R.id.llCity);
                tvCity = (TextView) itemView.findViewById(R.id.tvCity);
                ivSelect = (ImageView) itemView.findViewById(R.id.ivSelect);
            }
        }

        private class OnItemClickListener implements View.OnClickListener {

            private int position;

            public OnItemClickListener(int position) {
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                isAutoLocation = false;
                selecredCity = cityList.get(position);
                onBackPressed();
            }
        }

    }
}
