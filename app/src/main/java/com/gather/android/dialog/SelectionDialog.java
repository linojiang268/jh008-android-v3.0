package com.gather.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gather.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Levi on 2015/7/9.
 */
public class SelectionDialog extends Dialog {
    private static  final int MAX_VISIBLE_ITEM = 5;

    public SelectionDialog(Context context) {
        super(context, R.style.dialog_untran);
    }

    private RelativeLayout dialog_frame;
    private Button btnCancel;
    private ListView lvSelection;
    private final List<String> list = new ArrayList<>();
    private ListAdapter adapter;
    private boolean isVisibleChange = false;
    private OnSelectedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setWindowAnimations(R.style.DialogBottomInAnim);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

        setContentView(R.layout.selection_dialog);

        initView();
    }

    public void setSlectListener(OnSelectedListener listener){
        this.listener = listener;
    }

    private void initView(){
        dialog_frame = (RelativeLayout) findViewById(R.id.dialog_frame);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        lvSelection = (ListView) findViewById(R.id.lv_selection);
        adapter = new ListAdapter();
        lvSelection.setAdapter(adapter);

        lvSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (listener != null){
                    listener.onSelected(i, adapter.getItem(i));
                }
                dismiss();
            }
        });

        dialog_frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        if (isVisibleChange){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lvSelection.getLayoutParams();
            int count = list.size();
            params.height = getContext().getResources().getDimensionPixelOffset(R.dimen.text_height) * count + count - 1;
            lvSelection.setLayoutParams(params);
        }
    }

    public void setItemData(List<String> l){
        isVisibleChange = (list.size() != l.size());
        list.clear();
        list.addAll(l);
        if (adapter != null){
            adapter.notifyDataSetChanged();
        }
    }


    private class  ListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView tv = null;
            if (view == null){
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.selection_dialog_item, null);
                tv = (TextView) view.findViewById(R.id.tv_item);
                view.setTag(tv);
            }
            else {
                tv = (TextView) view.getTag();
            }
            if (i == 0){
                tv.setBackgroundResource(R.drawable.shape_select_type_top);
            }
            else if (i == getCount() - 1){
                tv.setBackgroundResource(R.drawable.shape_select_type_bottom);
            }
            else {
                tv.setBackgroundResource(R.drawable.shape_select_type_mid);
            }
            tv.setText(getItem(i));
            return view;
        }
    }

    public interface  OnSelectedListener{
        public void onSelected(int pos, String item);
    }
}
