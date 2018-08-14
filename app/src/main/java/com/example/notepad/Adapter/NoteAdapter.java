package com.example.notepad.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.notepad.Bean.Note;
import com.example.notepad.EditNoteActivity;
import com.example.notepad.R;

import java.util.Date;
import java.util.List;

import static com.example.notepad.R.drawable.ic_lock_outline_black_18dp;
import static com.example.notepad.Util.DAOTools.deleteOneNote;
import static com.example.notepad.Util.Util.getIntervalDays;
import static com.example.notepad.Util.Util.showToast;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private Context context;
    private List<Note> notes;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View v;
        TextView title;
        TextView content;
        TextView time;
        ImageView iconLock;

        private ViewHolder(View view){
            super(view);
            v = view;
            title = (TextView) view.findViewById(R.id.title);
            content = (TextView) view.findViewById(R.id.content);
            time = (TextView) view.findViewById(R.id.time);
            iconLock = (ImageView)view.findViewById(R.id.icon_lock);
        }
    }

    public NoteAdapter(List<Note> noteList){
        notes = noteList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null){
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item_note, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        //item点击编辑
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Note note = notes.get(position);
                Intent intent = new Intent(context, EditNoteActivity.class);
                intent.putExtra("note", note);//数据传给详情页
                context.startActivity(intent);
            }
        });
        //item长按删除
        holder.v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("删除笔记")
                        .setMessage("确定删除数据吗？删除后不可恢复呦！请谨慎操作！！！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int position = holder.getAdapterPosition();
                                Note note = notes.get(position);
                                //数据库里删除
                                int flag = deleteOneNote(note);
                                if(flag > 0){
                                    showToast(context, "删除成功");
                                }else{
                                    showToast(context, "删除失败");
                                }
                                //列表里删除元素
                                notes.remove(position);
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
        Note note = notes.get(position);
        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());
        String interval = getIntervalDays(new Date(), note.getTime());
        holder.time.setText(interval);
        if(note.getEncrypted().equals("true")){
            holder.iconLock.setImageResource(ic_lock_outline_black_18dp);
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

}
