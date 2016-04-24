package com.gather.android.baseclass;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.bugtags.library.Bugtags;
import com.gather.android.Constant;
import com.gather.android.R;
import com.gather.android.data.UserPref;
import com.gather.android.dialog.DialogCreater;
import com.gather.android.event.lifecycle.IComponentContainer;
import com.gather.android.event.lifecycle.LifeCycleComponent;
import com.gather.android.event.lifecycle.LifeCycleComponentManager;
import com.gather.android.http.OkHttpUtil;
import com.gather.android.manager.AppManage;
import com.gather.android.ui.activity.IntrestPage;
import com.gather.android.ui.activity.Login;
import com.jihe.dialog.listener.OnBtnClickL;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import io.yunba.android.manager.YunBaManager;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

/**
 * Created by Christain on 2015/5/25.
 */
public class BaseActivity extends AppCompatActivity implements IComponentContainer, AppManage.ActivityInter {
    public static final String BROADCAST_FLAG = "com.gather.broadcast.common";

    private LifeCycleComponentManager mComponentContainer = new LifeCycleComponentManager();
    private static LinearInterpolator interpolator = new LinearInterpolator();

    @Optional
    @InjectView(R.id.toolbar)
    public Toolbar toolbar;

    private CommonBroadcastReceiver mBroadcastReceiver;
    private boolean needReLoginBroadcastReceiver = false;
    private boolean hasIntentForBroadcast = false;


    private Dialog mWarningDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManage.getInstance().addActivity(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.inject(this);
        if (!(layoutResID == R.layout.act_detail || layoutResID == R.layout.show_page || layoutResID == R.layout.login || layoutResID == R.layout.user_center || layoutResID == R.layout.guide_activity)) {
            initSystemBar(0);
        }
        needReLoginBroadcastReceiver = (layoutResID != R.layout.login);
    }

    @Override
    public void finish() {
        AppManage.getInstance().finishActivity(this);
    }

    @Override
    public void finishActivity() {
        super.finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mComponentContainer.onBecomesVisibleFromTotallyInvisible();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mComponentContainer.onBecomesVisibleFromPartiallyInvisible();
        TCAgent.onResume(this);
        if (Constant.SHOW_LOG) {
            Bugtags.onResume(this);
        }
        if (needReLoginBroadcastReceiver) {
            if (mBroadcastReceiver == null){
                mBroadcastReceiver = new CommonBroadcastReceiver();
            }
            hasIntentForBroadcast = false;
            IntentFilter filter = new IntentFilter();
            filter.addAction(BROADCAST_FLAG);
            registerReceiver(mBroadcastReceiver, filter);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mComponentContainer.onBecomesPartiallyInvisible();
        TCAgent.onPause(this);
        if (Constant.SHOW_LOG) {
            Bugtags.onPause(this);
        }

        if (needReLoginBroadcastReceiver) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (Constant.SHOW_LOG) {
            Bugtags.onDispatchTouchEvent(this, ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onStop() {
        super.onStop();
        mComponentContainer.onBecomesTotallyInvisible();
        OkHttpUtil.getOkHttpClient().cancel(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mComponentContainer.onDestroy();
    }

    @Override
    public void addComponent(LifeCycleComponent component) {
        mComponentContainer.addComponent(component);
    }

    public void initSystemBar(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            if (color == 0) {
                tintManager.setStatusBarTintResource(R.color.style_color_primary);
            } else {
                tintManager.setStatusBarTintResource(color);
            }
        }
    }

    //设置systembar透明
    public void setTranslucentStatus(boolean status) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (status) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 开始执行toolBar进入动画
     */
    protected void startToolBarAnimation(final AnimatorListenerAdapter onAnimationEnd) {
        final int toolbarSize = getResources().getDimensionPixelOffset(R.dimen.titlebar_height);
        final ArrayList<View> list = new ArrayList<View>();
        for (int i = 0; i < ((ViewGroup) toolbar.getChildAt(0)).getChildCount(); i++) {
            View view = ((ViewGroup) toolbar.getChildAt(0)).getChildAt(i);
            if (view.getVisibility() == View.VISIBLE) {
                view.setAlpha(0);
                list.add(view);
            }
        }
        toolbar.setTranslationY(-toolbarSize);
        animate(toolbar).translationY(0)
                .setDuration(300)
                .setInterpolator(interpolator)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        for (int i = 0; i < list.size(); i++) {
                            View view = list.get(i);
                            view.setTranslationY(-toolbarSize);
                            animate(view).translationY(0)
                                    .setDuration(200)
                                    .setStartDelay(i * 200)
                                    .setInterpolator(interpolator)
                                    .alpha(1)
                                    .setListener(i == (list.size() - 1) ? onAnimationEnd : null);

                        }
                    }
                });

    }

    /**
     * 开始执行contentView动画
     */
    protected void startContentViewAnimation(View contentView, AnimatorListenerAdapter onAnimationEnd) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(contentView, "alpha", 1),
                ObjectAnimator.ofFloat(contentView, "translationY", 0)
        );
        set.setDuration(400).start();
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(onAnimationEnd);
    }

    /**
     * toast message
     *
     * @param text
     */
    protected void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * toast message
     *
     * @param resource
     */
    protected void toast(int resource) {
        Toast.makeText(this, resource, Toast.LENGTH_SHORT).show();
    }

    private class CommonBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.hasExtra("TYPE") && !hasIntentForBroadcast){
                hasIntentForBroadcast = true;
                YunBaManager.stop(getApplicationContext());
                int type = intent.getIntExtra("TYPE", -1);
                switch (type){
                    case 1://cookie失效，需要重新登录
                        showWarningDialog("您的登录状态已经失效，请重新登录", "立即登录", new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                mWarningDialog.dismiss();
                                UserPref.getInstance().clear();
                                AppManage.getInstance().finishOther();
                                Intent intent = new Intent(BaseActivity.this, Login.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        break;
                    case 2://需要完善资料
                        showWarningDialog("为了给您提供更好的服务，请先完善资料", "立即完善", new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                mWarningDialog.dismiss();
                                AppManage.getInstance().finishOther();
                                Intent intent = new Intent(BaseActivity.this, IntrestPage.class);
                                intent.putExtra(IntrestPage.EXTRA_PHONE, UserPref.getInstance().getUserInfo().getMobile());
                                intent.putExtra(IntrestPage.EXTRA_MODE, IntrestPage.MODE_FIX_INFO);
                                startActivity(intent);
                            }
                        });
                        break;
                    case 3://在其他地方登录
                        showWarningDialog("您的账号已在其他设备登录，如需继续使用请重新登录", "立即登录", new OnBtnClickL() {
                            @Override
                            public void onBtnClick() {
                                mWarningDialog.dismiss();
                                UserPref.getInstance().clear();
                                AppManage.getInstance().finishOther();
                                Intent intent = new Intent(BaseActivity.this, Login.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        break;
                }
            }
        }
    }

    private void showWarningDialog(String content, String btnTxt,OnBtnClickL l){
        mWarningDialog = DialogCreater.createTipsDialog(this, "温馨提示", content, btnTxt, false, l);
        mWarningDialog.show();
    }

}
