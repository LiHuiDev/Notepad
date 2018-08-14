package com.example.notepad;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.notepad.Bean.Remind;
import com.example.notepad.Util.Util;

import java.text.ParseException;
import java.util.Calendar;

public class BootCompleteReceiver extends BroadcastReceiver {
    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Util.showToast(context, "接收到啦！！");

        Remind remind = (Remind) intent.getSerializableExtra("remind");
        addAm(context, remind, 0, 0);
    }

    public static void addAm(Context context, Remind remind, int repetition, long intervalMillis){
        Calendar calendar = Calendar.getInstance();
        try{
            calendar = Util.stringToCalendar(remind.getDateTime());
        }catch (ParseException e){}

        Intent intent = new Intent(context, RingActivity.class);
        intent.putExtra("remind", remind);
        PendingIntent sender = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (repetition == 0) {
                am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            } else {
                am.setWindow(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intervalMillis, sender);
            }
        }else {
            if (repetition == 0) {
                am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            } else {
                am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intervalMillis, sender);
            }
        }
//        am.cancel(sender);//取消
    }
}
