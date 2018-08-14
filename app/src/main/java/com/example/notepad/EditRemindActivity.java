package com.example.notepad;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import com.example.notepad.Bean.Remind;
import com.example.notepad.Bean.User;
import com.example.notepad.Util.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.ParseException;
import java.util.Calendar;

import cn.qqtheme.framework.util.ConvertUtils;

import static com.example.notepad.Util.Util.stringToCalendar;

public class EditRemindActivity extends AppCompatActivity {

    private Context context;
    private User user;
    private Toolbar toolbar;
    private AppCompatTextView complete;
    private MaterialEditText schedule;
    private AppCompatTextView dateTime;
    private String dateTimeString;

    private Remind remind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remind);

        context = getApplicationContext();

        initView();
        actionBar();
        setData();
        editTextChangedListener();
        dateTimeClickListener();
        buttonListener();
    }

    public void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        complete = (AppCompatTextView) findViewById(R.id.complete);
        schedule = (MaterialEditText)findViewById(R.id.schedule_edit_text);
        dateTime = (AppCompatTextView)findViewById(R.id.date_time);
    }

    public void actionBar(){
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true);//返回按钮
            actionbar.setTitle("编辑日程");
        }
    }

    public void setData(){
        remind  = (Remind)getIntent().getSerializableExtra("remind");
        if (remind != null){
            schedule.setText(remind.getSchedule());
            dateTime.setText(remind.getDateTime());
        }
    }

    public void editTextChangedListener(){
        schedule.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!schedule.getText().toString().equals("")
                        && !dateTime.getText().toString().equals("")){
                    complete.setVisibility(View.VISIBLE);
                }else{
                    complete.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dateTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!schedule.getText().toString().equals("") && !dateTime.getText().toString().equals("")){
                    complete.setVisibility(View.VISIBLE);
                }else{
                    complete.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void dateTimeClickListener(){
        dateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Calendar calendar = stringToCalendar(remind.getDateTime());
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    onYearMonthDayPicker(view, year, month, day, hour, minute);
                }catch (ParseException e){}

            }
        });
    }

    public void buttonListener(){
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Remind newRemind = new Remind();
                String scheduleString = schedule.getText().toString();
                String dateTimeString = dateTime.getText().toString();
                newRemind.setSchedule(scheduleString);
                newRemind.setDateTime(dateTimeString);
                int flag = newRemind.updateAll("userEmail=? and schedule=? and dateTime=?", remind.getUserEmail(), remind.getSchedule(), remind.getDateTime());
                if(flag > 0){
                    Util.showToast(context, "保存成功");
                    EditRemindActivity.this.finish();
                }
            }
        });
    }

    public void onYearMonthDayPicker(final View view, int year, int month, int day, final int hour, final int minute) {
        //获取当前年月日
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);//当前年
        int currentMonth = calendar.get(Calendar.MONTH) + 1;//当前月从0开始所以加一
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);//当前日

        final cn.qqtheme.framework.picker.DatePicker picker = new cn.qqtheme.framework.picker.DatePicker(this);
        picker.setCanceledOnTouchOutside(false);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(this, 10));
        picker.setRangeEnd(currentYear+10, 12 , 31);
        picker.setRangeStart(currentYear, currentMonth, currentDay);
        picker.setSelectedItem(year, month, day);
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(new cn.qqtheme.framework.picker.DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                dateTimeString = year+"-"+month+"-"+day;
                onTimePicker(view, hour, minute);
            }
        });
        picker.setOnWheelListener(new cn.qqtheme.framework.picker.DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
    }
    public void onTimePicker(View view, int hour, int minute) {
        cn.qqtheme.framework.picker.TimePicker picker = new cn.qqtheme.framework.picker.TimePicker(this, cn.qqtheme.framework.picker.TimePicker.HOUR_24);
        picker.setCanceledOnTouchOutside(false);
        picker.setUseWeight(false);
        picker.setCycleDisable(false);
        picker.setRangeStart(0, 0);//00:00
        picker.setRangeEnd(23, 59);//23:59
        picker.setResetWhileWheel(false);
        picker.setSelectedItem(hour, minute);
        picker.setTopLineVisible(false);
        picker.setPadding(ConvertUtils.toPx(this, 15));
        picker.setOnTimePickListener(new cn.qqtheme.framework.picker.TimePicker.OnTimePickListener() {
            @Override
            public void onTimePicked(String hour, String minute) {
                dateTime.setText(dateTimeString+" "+hour+":"+minute);
            }
        });
        picker.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
