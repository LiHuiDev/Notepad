package com.example.notepad;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.notepad.Bean.User;

import static com.example.notepad.Util.DAOTools.checkUser;
import static com.example.notepad.Util.DAOTools.deleteUserNote;
import static com.example.notepad.Util.DAOTools.deleteUserRemind;
import static com.example.notepad.Util.DAOTools.findUserByEmail;
import static com.example.notepad.Util.Util.checkEmail;
import static com.example.notepad.Util.Util.showToast;

public class LoginActivity extends AppCompatActivity {

    private Context context;
    private Toolbar toolbar;
    private TextView skip;
    private EditText email;
    private ImageButton resetEmail;
    private EditText password;
    private ImageButton showPassword;
    private Button login;
    private TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();

        initView();
        actionBar();
        editTextListener();
        buttonListener();
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        email = (EditText) findViewById(R.id.email);
        resetEmail = (ImageButton) findViewById(R.id.reset_email);
        password = (EditText) findViewById(R.id.password);
        showPassword = (ImageButton) findViewById(R.id.show_password);
        login = (Button)findViewById(R.id.login);
        register = (TextView)findViewById(R.id.register);
        skip = (TextView)findViewById(R.id.skip);
    }

    public void actionBar(){
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null){
            actionbar.setTitle("登录");
        }
    }

    private void editTextListener(){
        //邮箱输入框监听
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(email.getText().toString().trim().equals("")){//文字为空
                    resetEmail.setVisibility(View.GONE);//隐藏重置按钮
                }else{//有文字
                    resetEmail.setVisibility(View.VISIBLE);//显示重置按钮
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (email.getText().toString().trim().equals("")) {
                    email.setError(null, null);////隐藏错误提示
                }
            }
        });
        //输入框焦点监听
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){// 得到焦点时的处理
                    if(!email.getText().toString().trim().isEmpty()){//文字不为空
                        resetEmail.setVisibility(View.VISIBLE);
                    }
                }else{// 失去焦点时的处理
                    resetEmail.setVisibility(View.GONE);
                    String userNameString = email.getText().toString().trim();
                    if (!userNameString.isEmpty()) {
                        //邮箱格式校验
                        if (!checkEmail(userNameString)) {
                            email.setError("邮箱格式错误");
                        }
                    }else{
                        email.setError("此处不为空");
                    }
                }
            }
        });
        //重置按钮点击处理
        resetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email.setText("");
            }
        });

        //密码输入框焦点监听
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String passwordString = password.getText().toString().trim();
                if(b){// 得到焦点时的处理
                    if(!passwordString.isEmpty()){// 文字不为空
                        showPassword.setVisibility(View.VISIBLE);
                    }
                }else{// 失去焦点时的处理
                    showPassword.setVisibility(View.GONE);
                    if (passwordString.isEmpty()) {
                        password.setError("此处不为空");
                    }
                }
            }
        });
        //密码输入框文字监听
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(password.getText().toString().trim().equals("")){
                    showPassword.setVisibility(View.GONE);
                }else{
                    showPassword.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //显示密码按钮处理（点击显示再点击隐藏）
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = password.getSelectionStart();//记录光标位置
                if(password.getInputType() != (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)){//隐藏密码
                    showPassword.setBackgroundResource(R.drawable.icon_hide_black);//修改图标
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else{//显示密码
                    showPassword.setBackgroundResource(R.drawable.icon_show_black);
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                password.setSelection(pos);//还原光标位置
            }
        });
    }

    public void buttonListener(){
        //注册
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RegisterActivity.class);
                startActivity(intent);
            }
        });
        //游客登录（跳过）
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("游客访问");
                builder.setMessage("确定以游客身份访问吗？游客数据将不保存，且无法享受加密服务！");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        User user = new User("temp", "temp@temp.com");
                        if(findUserByEmail(user) == null){
                            user.save();
                        }
                        //删除临时数据库内容
                        SharedPreferences.Editor editoricon = getSharedPreferences("icon", MODE_PRIVATE).edit();
                        editoricon.clear();
                        editoricon.apply();
                        deleteUserNote(user);
                        deleteUserRemind(user);

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
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
        });
        //登录按钮
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hintKeyboard();
                String emailString = email.getText().toString().trim();
                String passwordString = password.getText().toString().trim();

                if(emailString.isEmpty()){//账号为空
                    email.setError("此处不为空");
                }else{
                    if(!checkEmail(emailString)){//账号格式不正确
                        email.setError("邮箱格式错误");
                    }
                }
                if(passwordString.isEmpty()){//密码为空
                    password.setError("此处不为空");
                }

                if(!emailString.isEmpty() && checkEmail(emailString) && !passwordString.isEmpty()){
                    User user = new User();
                    user.setEmail(emailString);
                    user.setPassword(passwordString);
                    if(findUserByEmail(user) != null){
                        if(checkUser(user)){//验证账户密码
                            //登陆成功写入SharedPreferences
                            SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                            editor.putString("email", emailString);
                            editor.apply();
                            //跳转
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                            finish();
                        }else{
                            showToast(context, "密码错误");
                        }
                    }else{
                        showToast(context, "用户不存在");
                    }
                }
            }
        });
    }

    //关闭软键盘
    public void hintKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getCurrentFocus()!=null){
            if (getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
