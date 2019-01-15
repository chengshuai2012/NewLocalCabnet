package com.link.cloud.veune;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.link.cloud.activity.MainActivity;


/**
 * Created by Administrator on 2017/9/8.
 */

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver()
    {
    }
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1 .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

}
