package com.example.notepad.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.notepad.Bean.Remind;
import com.example.notepad.EditRemindActivity;
import com.example.notepad.R;

import java.util.List;

import static com.example.notepad.Util.DAOTools.deleteOneRemind;
import static com.example.notepad.Util.Util.showToast;

public class RemindAdapter extends RecyclerView.Adapter<RemindAdapter.ViewHolder>{
    private Context context;
    private List<Remind> reminds;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View v;
        TextView dateTime;
        TextView schedule;

        private ViewHolder(View view){
            super(view);
            v = view;
            dateTime = (TextView) view.findViewById(R.id.date_time);
            schedule = (TextView) view.findViewById(R.id.schedule);
        }
    }

    public RemindAdapter(List<Remind> noteList){
        reminds = noteList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null){
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item_remind, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        //item点击编辑
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Remind remind = reminds.get(position);
                Intent intent = new Intent(context, EditRemindActivity.class);
                intent.putExtra("remind", remind);//数据传给详情页
                context.startActivity(intent);
            }
        });
        //item长按删除
        holder.v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("删除日程")
                        .setMessage("确定删除日程吗？删除后不可恢复呦！请谨慎操作！！！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int position = holder.getAdapterPosition();
                                Remind remind = reminds.get(position);
                                //删除标记


                                //数据库里删除
                                int flag = deleteOneRemind(remind);
                                if(flag > 0){
                                    showToast(context, "删除成功");
                                }else{
                                    showToast(context, "删除失败");
                                }
                                //列表里删除元素
                                reminds.remove(position);
                                notifyItemRemoved(position);
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Remind remind = reminds.get(position);
        holder.schedule.setText(remind.getSchedule());
        holder.dateTime.setText(remind.getDateTime());
    }

    @Override
    public int getItemCount() {
        return reminds.size();
    }


}
