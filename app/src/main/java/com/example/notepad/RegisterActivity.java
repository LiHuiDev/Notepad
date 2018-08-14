package com.example.notepad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.notepad.Bean.User;
import com.example.notepad.Util.Util;

import static com.example.notepad.Util.DAOTools.findUserByEmail;
import static com.example.notepad.Util.Util.checkEmail;

public class RegisterActivity extends AppCompatActivity {

    private Context context;
    private Toolbar toolbar;
    private EditText userName, email;
    private ImageButton resetUserName, resetEmail;
    private EditText password1;
    private ImageButton showPassword1;
    private EditText password2;
    private ImageButton showPassword2;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = getApplicationContext();

        initView();
        actionBar();
        editTextListener();
        registerButton();
    }

    public void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        email = (EditText) findViewById(R.id.email);
        resetEmail = (ImageButton) findViewById(R.id.reset_email);
        userName = (EditText) findViewById(R.id.user_name);
        resetUserName = (ImageButton) findViewById(R.id.reset_user_name);
        password1 = (EditText) findViewById(R.id.password1);
        showPassword1 = (ImageButton) findViewById(R.id.show_password1);
        password2 = (EditText) findViewById(R.id.password2);
        showPassword2 = (ImageButton) findViewById(R.id.show_password2);
        register = (Button)findViewById(R.id.register);
    }

    public void actionBar(){
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null){
            actionbar.setTitle("注册");
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void editTextListener(){
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(userName.getText().toString().trim().isEmpty()){//文字为空
                    resetUserName.setVisibility(View.GONE);//隐藏重置按钮
                }else{//有文字
                    resetUserName.setVisibility(View.VISIBLE);//显示重置按钮
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (userName.getText().toString().trim().isEmpty()) {
                    userName.setError(null, null);////隐藏错误提示
                }
            }
        });
        userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){// 得到焦点时的处理
                    if(!userName.getText().toString().trim().isEmpty()){//文字不为空
                        resetUserName.setVisibility(View.VISIBLE);
                    }
                }else{// 失去焦点时的处理
                    resetUserName.setVisibility(View.GONE);
                    String userNameString = userName.getText().toString().trim();
                    if (userNameString.isEmpty()) {
                        userName.setError("此处不为空");
                    }
                }
            }
        });
        //重置按钮点击处理
        resetUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName.setText("");
            }
        });
        //邮箱输入框焦点监听
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
        //邮箱输入框焦点监听
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

        //密码输入框文字监听
        password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(password1.getText().toString().trim().equals("")){
                    showPassword1.setVisibility(View.GONE);
                }else{
                    showPassword1.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //输入框焦点监听
        password1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){// 得到焦点时的处理
                    if(password1.getText().toString().length() > 0){// 文字不为空
                        showPassword1.setVisibility(View.VISIBLE);
                    }
                }else{// 失去焦点时的处理
                    showPassword1.setVisibility(View.GONE);
                    if (password1.getText().toString().trim().isEmpty()) {
                        password1.setError("此处不为空");
                    }
                }
            }
        });
        //显示密码按钮处理（点击显示再点击隐藏）
        showPassword1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = password1.getSelectionStart();//记录光标位置
                if(password1.getInputType() != (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)){//隐藏密码
                    showPassword1.setBackgroundResource(R.drawable.icon_hide_black);//修改图标
                    password1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else{//显示密码
                    showPassword1.setBackgroundResource(R.drawable.icon_show_black);
                    password1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                password1.setSelection(pos);//还原光标位置
            }
        });
        //密码输入框文字监听
        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(password2.getText().toString().trim().equals("")){
                    showPassword2.setVisibility(View.GONE);
                }else{
                    showPassword2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //输入框焦点监听
        password2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){// 得到焦点时的处理
                    if(!password2.getText().toString().trim().isEmpty()){// 文字不为空
                        showPassword2.setVisibility(View.VISIBLE);
                    }
                }else{// 失去焦点时的处理
                    showPassword2.setVisibility(View.GONE);
                    if (password2.getText().toString().trim().isEmpty()) {
                        password2.setError("此处不为空");
                    }
                }
            }
        });
        //显示密码按钮处理（点击显示再点击隐藏）
        showPassword2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = password2.getSelectionStart();//记录光标位置
                if(password2.getInputType() != (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)){//隐藏密码
                    showPassword2.setBackgroundResource(R.drawable.icon_hide_black);//修改图标
                    password2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else{//显示密码
                    showPassword2.setBackgroundResource(R.drawable.icon_show_black);
                    password2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                password2.setSelection(pos);//还原光标位置
            }
        });
    }

    public void registerButton(){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hintKeyboard();
                String userNameString = userName.getText().toString().trim();
                String emailString = email.getText().toString().trim();
                String password1String = password1.getText().toString().trim();
                String password2String = password2.getText().toString().trim();
                if(userNameString.isEmpty()){
                    userName.setError("此处不为空");
                }
                if(emailString.isEmpty()){
                    email.setError("此处不为空");
                }else{
                    if (!checkEmail(emailString)) {
                        email.setError("邮箱格式错误");
                    }
                }
                if(password1String.isEmpty()){
                    password1.setError("此处不为空");
                }
                if(password2String.isEmpty()){
                    password2.setError("此处不为空");
                }
                if(!password1String.isEmpty() && !password2String.isEmpty() && !password1String.equals(password2String)){
                    password2.setError("密码不一致");
                }
                if(!userNameString.isEmpty() && !emailString.isEmpty() && checkEmail(emailString)
                        && !password1String.isEmpty() && !password2String.isEmpty()
                        && password1String.equals(password2String)){
                    User user = new User(userNameString, emailString, password1String);
                    if(findUserByEmail(user) != null){
                        Util.showToast(context, "用户已注册，请直接登录！");
                        finish();
                    }else if(user.save()){
                        Util.showToast(context, "注册成功！");
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        finish();
                    } else {
                        Util.showToast(context, "注册失败，请重试！");
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
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
