package com.gather.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;

import butterknife.InjectView;

/**
 * 展示页，从FirstPage点开始跳转此页
 * Created by Levi on 2015/7/7.
 */
public class ShowPage extends BaseActivity{
    @InjectView(R.id.btn_backpress)
    ImageButton btnBackpress;

    @InjectView(R.id.btn_register)
    Button btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_page);

        btnBackpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowPage.this, IntrestPage.class);
                intent.putExtra(IntrestPage.EXTRA_MODE, IntrestPage.MODE_REGISTER);
                startActivity(intent);
            }
        });
    }
}
