package com.gather.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.gather.android.R;
import com.gather.android.ui.activity.UserCenter;
import com.gather.android.entity.OrgMemberEntity;
import com.gather.android.entity.UserInfoEntity;
import com.gather.android.manager.PhoneManager;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/7/9.
 */
public class OrgMemberAdapter extends BaseAdapter implements IDataAdapter<List<OrgMemberEntity>> {

    private List<OrgMemberEntity> list = new ArrayList<OrgMemberEntity>();
    private Context context;

    public OrgMemberAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public OrgMemberEntity getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_org_member, null);
            holder = new ViewHolder(convertView);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.ivUserIcon.getLayoutParams();
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            params.width = (metrics.widthPixels - PhoneManager.dip2px(4 * 16)) / 3;
            params.height = params.width;
            holder.ivUserIcon.setLayoutParams(params);
            holder.viewSex.setLayoutParams(params);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        OrgMemberEntity model = getItem(position);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(model.getAvatar()))
                .setResizeOptions(new ResizeOptions(300, 300))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(holder.ivUserIcon.getController())
                .setImageRequest(request)
                .build();
        holder.ivUserIcon.setController(controller);
//        holder.ivUserIcon.setImageURI(Uri.parse(model.getAvatar()));
        if (model.getGender() == 1) {
            holder.viewSex.setBackgroundResource(R.drawable.shape_circle_blue);
        } else {
            holder.viewSex.setBackgroundResource(R.drawable.shape_circle_red);
        }
        holder.flItemAll.setOnClickListener(new OnUserIconClickListener(model));

        return convertView;
    }

    @Override
    public void notifyDataChanged(List<OrgMemberEntity> strings, boolean isRefresh) {
        if (isRefresh) {
            list.clear();
        }
        list.addAll(strings);
        notifyDataSetChanged();
    }

    @Override
    public List<OrgMemberEntity> getData() {
        return list;
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    static class ViewHolder {
        @InjectView(R.id.ivUserIcon)
        SimpleDraweeView ivUserIcon;
        @InjectView(R.id.viewSex)
        View viewSex;
        @InjectView(R.id.flItemAll)
        FrameLayout flItemAll;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private class OnUserIconClickListener implements View.OnClickListener {

        private OrgMemberEntity model;

        public OnUserIconClickListener(OrgMemberEntity model) {
            this.model = model;
        }

        @Override
        public void onClick(View v) {
            UserInfoEntity user = new UserInfoEntity();
            user.setUid(model.getId() + "");
            user.setNickName(model.getName());
            user.setGender(model.getGender());
            user.setAvatarUrl(model.getAvatar());
            Intent intent = new Intent(context, UserCenter.class);
            intent.putExtra("MODEL", user);
            context.startActivity(intent);
        }
    }
}
