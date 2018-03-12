package me.chen.fakelocation;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

/**
 * Created by chenqizheng on 2018/3/12.
 */
@Dao
public interface SearchHistoryDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHistorys(SearchHistory... histories);

    @Query("Select * from SearchHistory")
    SearchHistory[] loadAll();

}
