package me.chen.fakelocation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings

/**
 * Created by chenqizheng on 2018/2/9.
 */

class SettingsUtils {
    companion object {
        fun openGPSSettings(context: Context) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(intent)
        }

        fun openTestGPSProvider(context: Context) {
            val intent = Intent("//");
            val componentName = ComponentName("com.android.settings", "com.android.settings.DevelopmentSettings");
            intent.setComponent(componentName);
            intent.setAction(Intent.ACTION_VIEW);
            context.startActivity(intent)
        }
    }
}