package me.chen.fakelocation;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by chenqizheng on 2018/3/12.
 */
@Database(entities = {SearchHistory.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SearchHistoryDAO getSearchHistoryDAO();

}
