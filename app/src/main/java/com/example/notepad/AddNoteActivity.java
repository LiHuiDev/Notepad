package com.example.notepad;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.notepad.Bean.Note;
import com.example.notepad.Bean.User;
import com.example.notepad.Util.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Date;

public class AddNoteActivity extends AppCompatActivity {

    private User user;
    private Context context;
    private Toolbar toolbar;
    private AppCompatTextView complete;
    private MaterialEditText lableEditText;
    private MaterialEditText contentEditText;
    private SwitchCompat isEncrypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        user = (User) getIntent().getSerializableExtra("user");
        context = getApplicationContext();

        initView();
        actionBar();
        buttonListener();
    }

    public void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        complete = (AppCompatTextView) findViewById(R.id.complete);
        lableEditText = (MaterialEditText)findViewById(R.id.label_edit_text);
        contentEditText = (MaterialEditText)findViewById(R.id.content_edit_text);
        isEncrypt = (SwitchCompat)findViewById(R.id.isEncrypt);
    }

    public void actionBar(){
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true);//返回按钮
            actionbar.setTitle("新的笔记");
        }
    }

    public void buttonListener(){
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = lableEditText.getText().toString().trim();
                String content = contentEditText.getText().toString().trim();
                String encrypted;
                if(isEncrypt.isChecked()){
                    encrypted = "true";
                }else{
                    encrypted = "false";
                }

                if(title.isEmpty()){
                    lableEditText.setError("此处不为空");
                }
                if(content.isEmpty()){
                    contentEditText.setError("此处不为空");
                }
                if(!title.isEmpty() && !content.isEmpty()){
                    Note note = new Note(user.getEmail(), title, content, new Date(), encrypted);
                    if(note.save()){
                        Util.showToast(context, "保存成功");
                        finish();
                    } else {
                        Util.showToast(context, "保存失败，请重试！");
                    }
                }
            }
        });
    }

    //退出监控
    public void finishListener(){
        String title = lableEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        if(title.isEmpty() || content.isEmpty()){
            finish();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(AddNoteActivity.this);
            builder.setTitle("退出编辑");
            builder.setMessage("数据还未保存，确定退出编辑吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finishListener();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finishListener();
    }
}
