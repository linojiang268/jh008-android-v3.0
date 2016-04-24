package com.gather.android.dialog;

import android.content.Context;
import android.graphics.Color;

import com.gather.android.R;
import com.jihe.dialog.entity.DialogMenuItem;
import com.jihe.dialog.listener.OnBtnClickL;
import com.jihe.dialog.listener.OnBtnLeftClickL;
import com.jihe.dialog.listener.OnOperItemClickL;
import com.jihe.dialog.widget.NormalDialog;
import com.jihe.dialog.widget.NormalListDialog;
import com.jihe.dialog.widget.NormalTipDialog;

import java.util.ArrayList;

/**
 * Created by Levi on 2015/9/25.
 */
public class DialogCreater {
    /**
     * 创建带图片的文字listdialog
     * @param context
     * @param title
     * @param items
     * @return
     */
    public static NormalListDialog createImageListDialog(Context context, String title ,ArrayList<DialogMenuItem> items, OnOperItemClickL l){
        final NormalListDialog dialog = new NormalListDialog(context, items);
        dialog.title(title)//
                .titleTextSize_SP(16)//
                .titleBgColor(Color.parseColor("#ffffff"))//
                .itemPressColor(Color.parseColor("#dedede"))//
                .itemTextColor(Color.parseColor("#000000"))//
                .titleTextColor(Color.parseColor("#ffffff"))
                .titleBgColor(Color.parseColor("#18abff"))
                .itemTextSize(14)//
                .cornerRadius(0)//
                .widthScale(0.8f);
        dialog.setOnOperItemClickL(l);
        return dialog;
    }

    /**
     * 创建一般的带确定和取消按钮的dialog
     * @param context
     * @param title
     * @param content
     * @return
     */
    public static NormalDialog createNormalDialog(Context context, String title, String content, OnBtnLeftClickL l){
        final NormalDialog dialog = new NormalDialog(context);
        dialog.content(content)//
                .title(title)
                .style(NormalDialog.STYLE_TWO)//
                .titleTextSize(18)
                .contentTextSize(14);
        dialog.setOnBtnLeftClickL(l);
        return dialog;
    }

    /**
     * 创建一般的带确定和取消按钮的dialog
     * @param context
     * @param title
     * @param content
     * @return
     */
    public static NormalTipDialog createTipsDialog(Context context, String title, String content, String btnText, boolean cancelable, OnBtnClickL l){
        final NormalTipDialog dialog = new NormalTipDialog(context);
        dialog.content(content)//
                .title(title)
                .btnText(btnText)
                .titleTextSize(18)
                .contentTextSize(14)
                .style(NormalTipDialog.STYLE_TWO);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);
        dialog.setOnBtnClickL(l);
        return dialog;
    }

    /**
     * 创建分享dialog
     * @param context
     * @return
     */
    public static NormalListDialog createShareDialog(Context context, OnOperItemClickL l){
        ArrayList<DialogMenuItem> list = new ArrayList<>();
        list.add(new DialogMenuItem("QQ", R.drawable.icon_share_qq));
        list.add(new DialogMenuItem("微信", R.drawable.icon_share_wechat));
        list.add(new DialogMenuItem("朋友圈", R.drawable.icon_share_square));
        list.add(new DialogMenuItem("微博", R.drawable.icon_share_sina));
        final NormalListDialog dialog = createImageListDialog(context, "分享", list, l);
        return dialog;
    }
}
