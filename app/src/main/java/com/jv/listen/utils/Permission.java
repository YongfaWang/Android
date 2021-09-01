package com.jv.listen.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

public class Permission {


    /**
     * 忽略电池优化权限类
     */
    public static class IgnoringBatteryOptimizations {
        /**
         * 判断应用是否获得忽略电池优化权限
         *
         * @param context 上下文对象
         * @return
         */
        @RequiresApi(api = Build.VERSION_CODES.M)
        public static boolean isIgnoringBatteryOptimizations(Context context) {
            boolean isIgnoring = false;
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                isIgnoring = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
            }
            return isIgnoring;
        }

        /**
         * 申请忽略电池优化权限
         *
         * @param context 上下文对象
         */
        @RequiresApi(api = Build.VERSION_CODES.M)
        public static void requestIgnoreBatteryOptimizations(Context context) {
            try {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
