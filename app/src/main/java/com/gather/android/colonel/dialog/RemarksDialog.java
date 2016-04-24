package com.gather.android.colonel.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gather.android.R;
import com.jihe.dialog.widget.base.BaseDialog;

/**
 * Created by Levi on 15/10/3.
 */
public class RemarksDialog extends BaseDialog {
    private TextView tvTitle;
    private EditText etContent;
    private Button btnCommit;

    private String title, remarks;
    private AddRemarksListener listener;

    /**
     * method execute order:
     * show:constrouctor---show---oncreate---onStart---onAttachToWindow
     * dismiss:dismiss---onDetachedFromWindow---onStop
     *
     * @param context
     */
    public RemarksDialog(Context context) {
        super(context);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public void setAddRemarksListener(AddRemarksListener l){
        listener = l;
    }

    public void setContentAndTitle(String remarks, String title){
        this.title = title;
        this.remarks = remarks;
    }

    @Override
    public View onCreateView() {
        widthScale(0.88f);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.colonel_signup_remarks_dialog, null);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        etContent = (EditText) view.findViewById(R.id.etContent);
        btnCommit = (Button) view.findViewById(R.id.btnCommit);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnCommit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                dismiss();
                String content = etContent.getText().toString();
                if (TextUtils.isEmpty(content)){
                    Toast.makeText(context, "请输入备注内容", Toast.LENGTH_SHORT).show();
                }
                else if (listener != null){
                    listener.addRemarks(content);
                }
            }
        });

        return view;
    }

    @Override
    public boolean setUiBeforShow() {
        tvTitle.setText(title);
        if (!TextUtils.isEmpty(remarks)){
            etContent.setText("");
            etContent.append(remarks);
        }
        else {
            etContent.setText("");
        }
        return false;
    }


    public interface AddRemarksListener {
        public void addRemarks(String remarks);
    }
}
