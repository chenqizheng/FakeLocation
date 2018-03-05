package me.chen.fakelocation

import android.util.Log

/**
 * Created by chenqizheng on 2018/3/5.
 */
class Logger {
    companion object {
        fun log(tag: String, msg: String) {
            if (BuildConfig.DEBUG) {
                Log.i(tag, msg)
            }
        }
    }
}