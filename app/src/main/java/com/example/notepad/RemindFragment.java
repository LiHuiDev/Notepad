package com.example.notepad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.notepad.Adapter.RemindAdapter;
import com.example.notepad.Bean.Remind;
import com.example.notepad.Bean.User;
import com.example.notepad.Util.Util;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.example.notepad.Util.DAOTools.findRemind;

public class RemindFragment extends Fragment {
    private View rootView;
    private Context context;
    private AppCompatActivity mActivity;
    private User user;

    TextView mTextMonthDay;
    TextView mTextYear;
    TextView mTextLunar;
    TextView mTextCurrentDay;
    CalendarLayout mCalendarLayout;
    CalendarView mCalendarView;
    RelativeLayout mRelativeTool;
    FrameLayout mFrameLayout;
    RecyclerView mRecyclerView;
    FloatingActionButton mFloatingActionButton;

    int mYear;
    int mMonth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_remind, container, false);
        context = inflater.getContext();
        mActivity = (AppCompatActivity)getActivity();

        user = (User) mActivity.getIntent().getSerializableExtra("user");

        initView();
        initData();
        listener();

        return rootView;
    }

    public void initView(){
        mTextMonthDay = (TextView) rootView.findViewById(R.id.tv_month_day);
        mTextYear = (TextView) rootView.findViewById(R.id.tv_year);
        mTextLunar = (TextView) rootView.findViewById(R.id.tv_lunar);
        mFrameLayout = (FrameLayout) rootView.findViewById(R.id.fl_current);
        mTextCurrentDay = (TextView) rootView.findViewById(R.id.tv_current_day);
        mCalendarLayout = (CalendarLayout) rootView.findViewById(R.id.calendarLayout);
        mCalendarView = (CalendarView) rootView.findViewById(R.id.calendarView);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_remind);
        mFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);

        mYear = mCalendarView.getCurYear();
        mMonth = mCalendarView.getCurMonth();

        mCalendarView.setRange(mYear, mMonth, mYear+10, 12);//范围
        mCalendarView.scrollToCurrent();//现在
    }
    protected void initData() {
        List<Remind> reminds = findRemind(user);
        Remind remind;
        List<Remind> newReminds = new ArrayList<>();
        List<Calendar> schemes = new ArrayList<>();
        java.util.Calendar current, calendar;

        int size = reminds.size();
        if(size > 0){
            for(int i = 0; i < size; i++){
                remind = reminds.get(i);
                try {
                    current = java.util.Calendar.getInstance();//今天
                    calendar = Util.stringToCalendar(remind.getDateTime());
                    if(calendar.after(current)){
                        int year = calendar.get(java.util.Calendar.YEAR);//当前年
                        int month = 1 + calendar.get(java.util.Calendar.MONTH);//当前月
                        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);//当前日
                        schemes.add(getSchemeCalendar(year, month, day));
                        newReminds.add(remind);
                    }else{
                        DataSupport.deleteAllAsync(Remind.class, "userEmail=? and schedule=? and dateTime=?", remind.getUserEmail(), remind.getSchedule(), remind.getDateTime());
                    }
                }catch (ParseException e){}
            }
        }

        mCalendarView.setSchemeDate(schemes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
        if(newReminds.size() > 0){
            mRecyclerView.setAdapter(new RemindAdapter(newReminds));
        }
    }
    private Calendar getSchemeCalendar(int year, int month, int day) {
//        int color;
//        Random random = new Random();
//        int i = Math.abs(random.nextInt()) % 7;
//        switch (i){
//            case 0:
//                color = 0xFF40db25;
//                break;
//            case 1:
//                color = 0xFFe69138;
//                break;
//            case 2:
//                color = 0xFFdf1356;
//                break;
//            case 3:
//                color = 0xFFedc56d;
//                break;
//            case 4:
//                color = 0xFFaacc44;
//                break;
//            case 5:
//                color = 0xFFbc13f0;
//                break;
//            case 6:
//                color = 0xFF13acf0;
//                break;
//            default:
//                color = 0xFF40db25;
//        }
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(ContextCompat.getColor(context, R.color.colorPrimary));
        return calendar;
    }

    public void listener(){
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarView.showYearSelectLayout(mYear);
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));

        mFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCalendarView.isYearSelectLayoutVisible()){
                    mCalendarView.closeYearSelectLayout();
                }
                mCalendarView.scrollToCurrent();
            }
        });

        mCalendarView.setOnDateSelectedListener(new CalendarView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Calendar calendar, boolean isClick) {
                mTextLunar.setVisibility(View.VISIBLE);
                mTextYear.setVisibility(View.VISIBLE);
                mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
                mTextYear.setText(String.valueOf(calendar.getYear()));
                mTextLunar.setText(calendar.getLunar());
            }
        });
        mCalendarView.setOnYearChangeListener(new CalendarView.OnYearChangeListener() {
            @Override
            public void onYearChange(int year) {
                mTextMonthDay.setText(String.valueOf(year));
            }
        });

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, AddRemindActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

    }

    public static void refreshReminds(final List<Calendar>schemes, final CalendarView calendarView,
                                      final List<Remind>reminds, final RecyclerView recyclerView){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                calendarView.setSchemeDate(schemes);
                recyclerView.setAdapter(new RemindAdapter(reminds));
            }
        }, 100);
    }

    @Override
    public void onResume() {
        mCalendarView.clearSchemeDate();
        List<Remind> reminds = findRemind(user);
        Remind remind;
        List<Remind> newReminds = new ArrayList<>();
        List<Calendar> schemes = new ArrayList<>();
        java.util.Calendar current, calendar;

        int size = reminds.size();
        if(size > 0){
            for(int i = 0; i < size; i++){
                remind = reminds.get(i);
                try {
                    current = java.util.Calendar.getInstance();
                    calendar = Util.stringToCalendar(remind.getDateTime());
                    if(calendar.after(current)){
                        int year = calendar.get(java.util.Calendar.YEAR);//当前年
                        int month = 1 + calendar.get(java.util.Calendar.MONTH);//当前月
                        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);//当前日
                        schemes.add(getSchemeCalendar(year, month, day));
                        newReminds.add(remind);
                    }else{
                        DataSupport.deleteAllAsync(Remind.class, "userEmail=? and schedule=? and dateTime=?", remind.getUserEmail(), remind.getSchedule(), remind.getDateTime());
                    }
                }catch (ParseException e){}
            }
        }

        refreshReminds(schemes, mCalendarView, newReminds, mRecyclerView);
        super.onResume();
    }
}
