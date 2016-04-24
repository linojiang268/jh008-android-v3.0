package com.gather.android.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gather.android.Constant;
import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.event.EventCenter;
import com.gather.android.event.MultiImageSelectEvent;
import com.gather.android.event.SingleImageSelectEvent;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import butterknife.InjectView;
import me.nereo.multi_image_selector.MultiImageSelectorFragment;
import me.nereo.multi_image_selector.crop.Crop;

/**
 * Created by Christain on 2015/6/11.
 */
public class ImageSelectActivity extends BaseActivity implements MultiImageSelectorFragment.Callback {
    //最大图片选择次数，int类型，默认9
    private static final String EXTRA_SELECT_COUNT = "max_select_count";
    //图片选择模式，默认多选
    private static final String EXTRA_SELECT_MODE = "select_count_mode";
    //是否显示相机，默认显示
    private static final String EXTRA_SHOW_CAMERA = "show_camera";
    //裁剪尺寸（头像一般是正方形的，所以只做了头像裁剪配置）
    private static final String EXTRA_CROP_SIZE = "crop_size";
//    /**
//     * 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合
//     */
//    public static final String EXTRA_RESULT = "select_result";
    /**
     * 默认选择集
     */
    private static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";


    //-------------------------模式---------------------------
    //单选
    private static final int MODE_SINGLE = 0;
    // 多选
    private static final int MODE_MULTI = 1;
    // 裁剪（单选后裁剪）
    private static final int MODE_CROP = 2;
    //----------------------------------------------------


    private ArrayList<String> resultList;

    private int cropSize;
    private int mMaxCount;
    private int mMode;

    @InjectView(R.id.ivToolbarBack)
    ImageView ivToolbarBack;
    @InjectView(R.id.tvToolbarTitle)
    TextView tvToolbarTitle;
    @InjectView(R.id.commit)
    Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_select_activity);
        initSystemBar(R.color.titlebar_bg_color);

        Intent intent = getIntent();
        mMaxCount = 1;
        mMode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_SINGLE);
        if (mMode == MODE_MULTI){
            mMaxCount =  intent.getIntExtra(EXTRA_SELECT_COUNT, 9);
             if (intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
                 resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
             }
        }
        else if (mMode == MODE_CROP){
            cropSize = intent.getIntExtra(EXTRA_CROP_SIZE, 200);
        }
        if (resultList == null){
            resultList = new ArrayList<>();
        }
        boolean showCamera = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);

        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mMaxCount);
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mMode == MODE_CROP? MODE_SINGLE : mMode);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, showCamera);
        bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);

        tvToolbarTitle.setText(R.string.picture);
        ivToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });

        if (mMode == MODE_MULTI) {
            mSubmitButton.setVisibility(View.VISIBLE);
            updateSelectCountView();
            mSubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (resultList.size() > 0){
                        // 返回已选择的图片数据
                        EventCenter.getInstance().post(new MultiImageSelectEvent(resultList));
                        finish();
                    }
                    else {
                        Toast.makeText(ImageSelectActivity.this, R.string.select_no_image, Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            mSubmitButton.setVisibility(View.INVISIBLE);
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
                .commit();

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void updateSelectCountView(){
        String text = String.format(getString(R.string.select_image_done), resultList.size(), mMaxCount);
        mSubmitButton.setText(text);
    }

    private boolean isCrop(){
        return  mMode == MODE_CROP;
    }

    private void toCrop(File srcFile){
        File outputFile = new File(Constant.UPLOAD_FILES_DIR_PATH + System.currentTimeMillis() + ".jpg");
        Crop.cropHearportrait(this, Uri.fromFile(srcFile), Uri.fromFile(outputFile), cropSize);
    }

    @Override
    public void onSingleImageSelected(String path) {
        if (isCrop()){
            File file = new File(path);
            toCrop(file);
        }
        else {
            EventCenter.getInstance().post(new SingleImageSelectEvent(path));
            setResult(RESULT_OK);
            finish();
        }

    }

    @Override
    public void onImageSelected(String path) {
        if(!resultList.contains(path)) {
            resultList.add(path);
        }
        updateSelectCountView();
    }

    @Override
    public void onImageUnselected(String path) {
        if(resultList.contains(path)) {
            resultList.remove(path);
        }
        updateSelectCountView();
    }

    @Override
    public void onCameraShot(File imageFile) {
        if (imageFile != null) {
            if (isCrop()){
                toCrop(imageFile);
            }
            else {
                EventCenter.getInstance().post(new SingleImageSelectEvent(imageFile));
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Crop.REQUEST_CROP){
            Uri uri = Crop.getOutput(data);
            try {
                File file = new File(new URI(uri.toString()));
                EventCenter.getInstance().post(new SingleImageSelectEvent(file));
                setResult(RESULT_OK);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static final int REQUEST_CODE_SLELECT_IMAGE = 123;

    /**
     * 多选图片
     */
    public static void pickImages(Activity activity, int maxCount, boolean showCamera, ArrayList<String> list){
        Intent intent = new Intent(activity, ImageSelectActivity.class);
        intent.putExtra(EXTRA_SELECT_MODE, MODE_MULTI);
        intent.putExtra(EXTRA_SELECT_COUNT, maxCount);
        intent.putExtra(EXTRA_SHOW_CAMERA, showCamera);
        if (list != null && list.size() > 0) {
            intent.putStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST, list);
        }
        activity.startActivityForResult(intent, REQUEST_CODE_SLELECT_IMAGE);
    }

    /**
     * 单选图片
     */
    public static void pickImage(Activity activity, boolean showCamera){
        Intent intent = new Intent(activity, ImageSelectActivity.class);
        intent.putExtra(EXTRA_SELECT_MODE, MODE_SINGLE);
        intent.putExtra(EXTRA_SHOW_CAMERA, showCamera);
        activity.startActivityForResult(intent, REQUEST_CODE_SLELECT_IMAGE);
    }

    /**
     * 单选图片后裁剪
     */
    public static void pickAndCropImage(Activity activity, boolean showCamera, int size){
        Intent intent = new Intent(activity, ImageSelectActivity.class);
        intent.putExtra(EXTRA_SELECT_MODE, MODE_CROP);
        intent.putExtra(EXTRA_CROP_SIZE, size);
        intent.putExtra(EXTRA_SHOW_CAMERA, showCamera);
        activity.startActivityForResult(intent, REQUEST_CODE_SLELECT_IMAGE);
    }
}
