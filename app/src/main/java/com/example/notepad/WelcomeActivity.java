package com.example.notepad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.notepad.Bean.User;

public class WelcomeActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        imageView = (ImageView)findViewById(R.id.welcome_image);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor editor = getSharedPreferences("encrypt", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();

                Intent intent;
                //从缓存获取用户信息
                SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
                String emailString = pref.getString("email", "");
                if(!emailString.isEmpty()){
                    User user = new User();
                    user.setEmail(emailString);
                    intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }else{
                    intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        },1000*2);
    }
}
