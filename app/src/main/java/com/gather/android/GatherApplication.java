package com.gather.android;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.baidu.mapapi.SDKInitializer;
import com.bugtags.library.Bugtags;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.manager.PhoneManager;
import com.gather.android.utils.PushUtils;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import java.io.File;

import gather.database.dao.DaoMaster;
import gather.database.dao.DaoSession;
import io.yunba.android.manager.YunBaManager;

/**
 * Created by Christain on 2015/5/25.
 */
public class GatherApplication extends Application {
    private static Context mContext;
    private static GatherApplication INSTANCE;
    private DaoSession daoSession;
    public IMqttActionListener pushListener;
//    private RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        mContext = getApplicationContext();

        Logger.init("GatherFinal").hideThreadInfo();
//        if (Constant.SHOW_LOG) {
            Bugtags.start(Constant.BUGTAGS_APPKEY, this, Bugtags.BTGInvocationEventBubble);
//        }
//        else {
//            ExceptionManage.start();
//        }
        SDKInitializer.initialize(this);
//        mRefWatcher = LeakCanary.install(INSTANCE);
//        Stetho.initialize(
//                Stetho.newInitializerBuilder(this)
//                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
//                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
//                        .build());
//        OkHttpUtil.getOkHttpClient().networkInterceptors().add(new StethoInterceptor());
        /*******初始化Facebook Fresco********/
        DiskCacheConfig mainDiskCacheConfig = DiskCacheConfig.newBuilder()
                .setBaseDirectoryPath(new File(PhoneManager.getAppRootPath()))
                .setBaseDirectoryName("cache")
                .setMaxCacheSize(50 * 1024 * 1024)
                .setMaxCacheSizeOnLowDiskSpace(30 * 1024 * 1024)
                .setMaxCacheSizeOnVeryLowDiskSpace(15 * 1024 * 1024)
                .setVersion(1)
                .build();
        /*************渐进显示******************/
        ProgressiveJpegConfig pjpegConfig = new ProgressiveJpegConfig() {
            @Override
            public int getNextScanNumberToDecode(int scanNumber) {
                return scanNumber + 2;
            }

            public QualityInfo getQualityInfo(int scanNumber) {
                boolean isGoodEnough = (scanNumber >= 5);
                return ImmutableQualityInfo.of(scanNumber, isGoodEnough, false);
            }
        };
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory.newBuilder(INSTANCE, OkHttpUtil.getOkHttpClient())
                .setProgressiveJpegConfig(pjpegConfig)
//                .setBitmapMemoryCacheParamsSupplier(bitmapCacheParamsSupplier)
//                .setCacheKeyFactory(cacheKeyFactory)
//                .setEncodedMemoryCacheParamsSupplier(encodedCacheParamsSupplier)
//                .setExecutorSupplier(executorSupplier)
//                .setImageCacheStatsTracker(imageCacheStatsTracker)
                .setMainDiskCacheConfig(mainDiskCacheConfig)
//                .setMemoryTrimmableRegistry(memoryTrimmableRegistry)
//                .setNetworkFetchProducer(networkFetchProducer)
//                .setPoolFactory(poolFactory)
//                .setProgressiveJpegConfig(progressiveJpegConfig)
//                .setRequestListeners(requestListeners)
//                .setSmallImageDiskCacheConfig(smallImageDiskCacheConfig)
                .build();
        Fresco.initialize(INSTANCE, config);

        /**
         * 初始化数据库
         */
        setupDatabase();

        /**
         * 初始化云吧推送
         */
        startBlackService();
    }

    /**
     * 数据库
     */
    private void setupDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "gather-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    /**
     * 云吧push
     */
    private void startBlackService() {
        YunBaManager.start(getApplicationContext());
        pushListener = new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                if (Constant.SHOW_LOG) {
                    String topic = PushUtils.join(asyncActionToken.getTopics(), ",");
                    Logger.d("Subscribe succeed : " + topic);
                }
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                if (Constant.SHOW_LOG) {
                    Logger.d( "Subscribe failed : " + exception.getMessage());
                }
            }
        };
    }


    public static synchronized GatherApplication getInstance() {
        return INSTANCE;
    }

    public static synchronized Context getContext() {
        return mContext;
    }
}
