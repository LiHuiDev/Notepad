package com.example.notepad.Bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Date;

public class Note extends DataSupport implements Serializable{

    private String userEmail;//用户
    private String title;//标题
    private String content;//内容
    private Date time;//时间
    private String encrypted;//加密

    public Note() {
    }

    public Note(String userEmail, String title, String content, Date time, String encrypted) {
        this.userEmail = userEmail;
        this.title = title;
        this.content = content;
        this.time = time;
        this.encrypted = encrypted;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(String encrypted) {
        this.encrypted = encrypted;
    }
}
