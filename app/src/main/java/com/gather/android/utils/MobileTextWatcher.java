package com.gather.android.utils;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by Levi on 2015/8/26.
 */
public class MobileTextWatcher implements TextWatcher {
    private EditText editText;

    public MobileTextWatcher(EditText editText){
        this.editText = editText;
        this.editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
        this.editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count == 1) {
            int length = s.length();
            if (length == 4 || length == 9) {
                editText.setText(s.subSequence(0, start) + " " + s.subSequence(start, length));
                editText.setSelection(s.length() + 1);
            }
        } else if (count == 0) {
            if (s.length() == 4 || s.length() == 9) {
                editText.setText(s.subSequence(0, s.length() - 1));
                editText.setSelection(s.length() - 1);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
