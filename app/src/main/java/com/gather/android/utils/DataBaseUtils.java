package com.gather.android.utils;

import android.os.AsyncTask;

import com.gather.android.GatherApplication;
import com.gather.android.data.UserPref;
import com.gather.android.event.CheckMsgReadedEvent;
import com.gather.android.event.EventCenter;

import de.greenrobot.dao.query.QueryBuilder;
import gather.database.dao.OrgActMsgDao;
import gather.database.dao.SystemMsgDao;

/**
 * 数据库工具
 * Created by Administrator on 2015/8/6.
 */
public class DataBaseUtils {

    /**
     * 判断是否有未读的消息
     */
    public static void notifyTabReddot() {
        final String userId = UserPref.getInstance().getUserInfo().getUid();
        new AsyncTask<Void, Integer, Boolean>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                QueryBuilder qb = null;
                qb = GatherApplication.getInstance().getDaoSession().getOrgActMsgDao().queryBuilder();
                qb.where(OrgActMsgDao.Properties.Readed.eq(false), OrgActMsgDao.Properties.UserId.eq(userId));
                if (qb.count() > 0) {
                   return true;
                } else {
                    qb = GatherApplication.getInstance().getDaoSession().getSystemMsgDao().queryBuilder();
                    qb.where(SystemMsgDao.Properties.Readed.eq(false), SystemMsgDao.Properties.UserId.eq(userId));
                    if (qb.count() > 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                EventCenter.getInstance().post(new CheckMsgReadedEvent(aBoolean));
            }
        }.execute();
    }
}
