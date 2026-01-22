package com.txl.blockmoonlighttreasurebox.ui;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

import com.txl.blockmoonlighttreasurebox.utils.AppExecutors;

/**
 * 显示工具类
 * 用于控制DisplayActivity在启动器中的显示和隐藏
 */
public class DisplayUtils {
    /**
     * 在启动器中显示或隐藏分析Activity
     * @param context 上下文
     * @param show 是否显示
     */
    public static void showAnalyzeActivityInLauncher(Context context, boolean show){
        ComponentName component = new ComponentName(context, DisplayActivity.class);
        PackageManager packageManager = context.getPackageManager();
        int newState = show ? COMPONENT_ENABLED_STATE_ENABLED : COMPONENT_ENABLED_STATE_DISABLED;
        // Blocks on IPC.
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                packageManager.setComponentEnabledSetting(component, newState, DONT_KILL_APP);
            }
        });
    }
}
