package com.example.notepad.Util;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {

    private static Toast toast;

    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    //编辑框获取焦点
    public static void getEditTextFocus(EditText editText){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    public static String getCurrentDate(){
        String date;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);//当前年
        int month = 1 + calendar.get(Calendar.MONTH);//当前月
        int day = calendar.get(Calendar.DAY_OF_MONTH);//当前日
        date = year + "-" + month + "-" + day;
        return date;
    }

    public static String getCurrentDateTime(){
        String dateTime;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);//当前年
        int month = 1 + calendar.get(Calendar.MONTH);//当前月
        int day = calendar.get(Calendar.DAY_OF_MONTH);//当前日
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//当前时
        int minute = calendar.get(Calendar.MINUTE);//当前分
        int second = calendar.get(Calendar.SECOND);//当前秒
        dateTime = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
        return dateTime;
    }

    public static String dateFormat(Date date){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    public static Calendar stringToCalendar(String string) throws ParseException{
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = df.parse(string);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    //计算两个时间的间隔
    public static String getIntervalDays(Date startday, Date endday){
        if(startday.after(endday)){
            Date cal=startday;
            startday=endday;
            endday=cal;
        }
        long sl = startday.getTime();
        long el = endday.getTime();
        long ei = el - sl;

        int day = (int)(ei / (1000*60*60*24));
        if (day == 0){
            int hour = (int)((ei % (1000*60*60*24))/(1000*60*60));
            if (hour == 0){
                int minute = (int)(ei % (1000*60*60))/(1000*60);
                if(minute == 0){
                    return "刚刚";
                }
                return minute + "分钟之前";
            } else{
                return hour + "小时之前";
            }
        } else{
            return day + "天之前";
        }
    }

    /**
     * 邮箱格式检验
     * @param email
     * @return
     */
    public static boolean checkEmail(String email){
        return email.matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    }

}
