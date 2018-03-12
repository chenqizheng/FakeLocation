package me.chen.fakelocation

import android.app.Application
import android.arch.persistence.room.Room


/**
 * Created by Chen on 2018/3/10.
 */
class App : Application() {

    lateinit var mAppDatabase: AppDatabase
    override fun onCreate() {
        super.onCreate()
        mAppDatabase = Room.databaseBuilder(applicationContext,
                AppDatabase::class.java, "app").build()
    }

}