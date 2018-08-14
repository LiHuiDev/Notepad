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
import com.example.notepad.Util.Util;
import com.rengwuxian.materialedittext.MaterialEditText;

import static com.example.notepad.Util.Util.dateFormat;

public class EditNoteActivity extends AppCompatActivity {

    private Context context;
    private Toolbar toolbar;
    private AppCompatTextView complete;
    private MaterialEditText lableEditText;
    private MaterialEditText contentEditText;
    private SwitchCompat isEncrypt;
    private AppCompatTextView createTime;

    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        context = getApplicationContext();

        initView();
        actionBar();
        setData();
        buttonListener();
    }

    public void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        complete = (AppCompatTextView) findViewById(R.id.complete);
        lableEditText = (MaterialEditText)findViewById(R.id.label_edit_text);
        contentEditText = (MaterialEditText)findViewById(R.id.content_edit_text);
        isEncrypt = (SwitchCompat)findViewById(R.id.isEncrypt);
        createTime = (AppCompatTextView) findViewById(R.id.create_time);
    }

    public void actionBar(){
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true);//返回按钮
            actionbar.setTitle("编辑笔记");
        }
    }

    public void setData(){
        note  = (Note)getIntent().getSerializableExtra("note");
        if (note != null){
            lableEditText.setText(note.getTitle());
            contentEditText.setText(note.getContent());
            if(note.getEncrypted().equals("true")){
                isEncrypt.setChecked(true);
            }else{
                isEncrypt.setChecked(false);
            }
            createTime.setText("创建于： " + dateFormat(note.getTime()));
        }
    }

    public void buttonListener(){
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Note newNote = new Note();
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
                    newNote.setTitle(title);
                    newNote.setContent(content);
                    newNote.setEncrypted(encrypted);
                    int flag = newNote.updateAll("userEmail=? and title=? and content=? and encrypted=?", note.getUserEmail(), note.getTitle(), note.getContent(), note.getEncrypted());
                    if(flag > 0){
                        Util.showToast(context, "保存成功");
                        finish();
                    } else {
                        Util.showToast(context, "保存失败，请重试！");
                    }
                }
            }
        });
    }

    public void finishListener(){
        String title = lableEditText.getText().toString();
        String content = contentEditText.getText().toString();
        if(title.isEmpty() || content.isEmpty() || (note.getTitle().equals(title) && note.getContent().equals(content))){
            finish();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(EditNoteActivity.this);
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
