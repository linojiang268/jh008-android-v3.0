package com.gather.android.utils;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gather.android.R;
import com.shizhefei.mvc.ILoadViewFactory;
import com.shizhefei.view.vary.VaryViewHelper;

/**
 * 切换卡上的列表
 * Created by Administrator on 2015/7/30.
 */
public class TabHostLoadViewFactory implements ILoadViewFactory {

    @Override
    public ILoadMoreView madeLoadMoreView() {
        return new LoadMoreHelper();
    }

    @Override
    public ILoadView madeLoadView() {
        return new LoadViewHelper();
    }

    private class LoadMoreHelper implements ILoadMoreView {

        protected TextView footView;

        protected View.OnClickListener onClickRefreshListener;

        @Override
        public void init(FootViewAdder footViewHolder, View.OnClickListener onClickRefreshListener) {
            View layout = footViewHolder.addFootView(R.layout.loadmore_tab_host_footer);
            footView = (TextView) layout.findViewById(R.id.tvFooter);
            this.onClickRefreshListener = onClickRefreshListener;
            showNormal();
        }

        @Override
        public void showNormal() {
            footView.setVisibility(View.VISIBLE);
            footView.setText("点击加载更多");
            footView.setOnClickListener(onClickRefreshListener);
        }

        @Override
        public void showLoading() {
            footView.setVisibility(View.VISIBLE);
            footView.setText("正在加载中..");
            footView.setOnClickListener(null);
        }

        @Override
        public void showFail() {
            footView.setVisibility(View.VISIBLE);
            footView.setText("加载失败，点击重新加载");
            footView.setOnClickListener(onClickRefreshListener);
        }

        @Override
        public void showNomore() {
            footView.setVisibility(View.GONE);
            footView.setText("");
            footView.setOnClickListener(null);
        }

    }

    private class LoadViewHelper implements ILoadView {
        private VaryViewHelper helper;
        private View.OnClickListener onClickRefreshListener;
        private Context context;

        @Override
        public void init(View switchView, View.OnClickListener onClickRefreshListener) {
            this.context = switchView.getContext().getApplicationContext();
            this.onClickRefreshListener = onClickRefreshListener;
            helper = new VaryViewHelper(switchView);
        }

        @Override
        public void restore() {
            helper.restoreView();
        }

        @Override
        public void showLoading() {
            View layout = helper.inflate(R.layout.loading_view_tab);
            TextView textView = (TextView) layout.findViewById(R.id.tvLoading);
            textView.setText("玩命加载中......");
//            LoadingView loadingview= (LoadingView) layout.findViewById(R.id.loadView);
//            loadingview.setLoadingText("加载中...");
            helper.showLayout(layout);
        }

        @Override
        public void tipFail() {
            Toast.makeText(context, "加载失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void showFail() {
            View layout = helper.inflate(R.layout.load_error_tab);
            TextView textView = (TextView) layout.findViewById(com.shizhefei.view.mvc.R.id.textView1);
            textView.setText("加载失败~~");
            ImageView imageView = (ImageView) layout.findViewById(com.shizhefei.view.mvc.R.id.imageView1);
            imageView.setImageResource(R.drawable.icon_error_tips);
            imageView.setOnClickListener(onClickRefreshListener);
            Button button = (Button) layout.findViewById(com.shizhefei.view.mvc.R.id.button1);
//            button.setText("点击重试");
//            button.setOnClickListener(onClickRefreshListener);
            helper.showLayout(layout);
        }

        @Override
        public void showEmpty() {
            View layout = helper.inflate(R.layout.load_empty_tab);
            TextView textView = (TextView) layout.findViewById(com.shizhefei.view.mvc.R.id.textView1);
            textView.setText("暂时没内容哦！！");
            ImageView imageView = (ImageView) layout.findViewById(com.shizhefei.view.mvc.R.id.imageView1);
            imageView.setImageResource(R.drawable.icon_empty_tips);
            imageView.setOnClickListener(onClickRefreshListener);
            Button button = (Button) layout.findViewById(com.shizhefei.view.mvc.R.id.button1);
            button.setVisibility(View.GONE);
//            button.setText("重试");
//            button.setOnClickListener(onClickRefreshListener);
            helper.showLayout(layout);
        }

    }
}
