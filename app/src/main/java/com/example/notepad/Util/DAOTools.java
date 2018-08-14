package com.example.notepad.Util;

import com.example.notepad.Bean.Note;
import com.example.notepad.Bean.Remind;
import com.example.notepad.Bean.User;

import org.litepal.crud.DataSupport;

import java.util.List;

public class DAOTools {
    /**
     * 根据邮箱查询用户
     * @param user
     * @return
     */
    public static User findUserByEmail(User user){
        return DataSupport.where("email=?", user.getEmail()).findFirst(User.class);
    }

    /**
     * 验证账户密码
     * @param user
     * @return
     */
    public static boolean checkUser(User user){
        if(findUserByEmail(user) != null){
            int size = DataSupport.where("email=? and password=?", user.getEmail(), user.getPassword()).find(User.class).size();
            if(size > 0){
                return true;
            }
        }
        return  false;
    }

    //删除用户全部记事本
    public static int deleteUserNote(User user){
        return DataSupport.deleteAll(Note.class, "userEmail=?", user.getEmail());
    }

    public static int deleteOneNote(Note note){
        return DataSupport.deleteAll(Note.class, "userEmail=? and title=? and content=? and encrypted=?",
                note.getUserEmail(), note.getTitle(), note.getContent(), note.getEncrypted());
    }

    //删除用户全部日程
    public static int deleteUserRemind(User user){
        return DataSupport.deleteAll(Remind.class, "userEmail=?", user.getEmail());
    }

    public static int deleteOneRemind(Remind remind){
        return DataSupport.deleteAll(Remind.class, "userEmail=? and schedule=? and dateTime=?",
                remind.getUserEmail(), remind.getSchedule(), remind.getDateTime());
    }

    public static List<Note>findNote(User user, String encrypt){
        return DataSupport.where("userEmail=? and encrypted=?", user.getEmail(), encrypt).order("time desc").find(Note.class);
    }

    public static List<Note>queryNote(User user, String query, String encrypt){
        return DataSupport.where("userEmail=? and (title like ? or content like ?) and encrypted=?",
                user.getEmail(), "%"+query+"%", "%"+query+"%", encrypt).order("time desc").find(Note.class);
    }

    public static List<Remind> findRemind(User user){
        return DataSupport.where("userEmail=?", user.getEmail()).order("dateTime asc").find(Remind.class);
    }

}
