package com.gather.android.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.gather.android.R;
import com.gather.android.baseclass.BaseActivity;
import com.gather.android.manager.PhoneManager;
import com.gather.android.ui.fragment.ActBaseFragment;
import com.gather.android.utils.Checker;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Levi on 2015/7/20.
 */
public class SearchActActivity extends BaseActivity {

    @InjectView(R.id.btn_search)
    Button btnSearch;
    @InjectView(R.id.et_keyword)
    EditText etKeyword;
    @InjectView(R.id.btn_clear)
    ImageButton btnClear;

    private ActBaseFragment actBaseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_act);

        btnClear.setVisibility(View.GONE);
        etKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                btnClear.setVisibility(Checker.isEmpty(editable.toString()) ? View.INVISIBLE : View.VISIBLE);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAct();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etKeyword.setText("");
            }
        });

        Bundle bundle = new Bundle();
        bundle.putInt(ActBaseFragment.EXTRA_MODE, ActBaseFragment.MODE_SEARCH);
        actBaseFragment = new ActBaseFragment();
        actBaseFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_act, actBaseFragment)
                .commit();
    }

    @OnClick({R.id.ibtn_backpress})
    void OnClick(View view) {
        switch (view.getId()) {
            case R.id.ibtn_backpress:
                onBackPressed();
                break;
        }
    }



    private void searchAct(){
        String ketword = etKeyword.getText().toString();
        if (Checker.isEmpty(ketword)){
            toast(R.string.input_keyword_of_act);
        }
        else {
            PhoneManager.hideKeyboard(this);
            actBaseFragment.search(ketword);
        }
    }
}
