package com.example.notepad;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.notepad.Adapter.ViewPagerAdapter;
import com.example.notepad.Bean.Note;
import com.example.notepad.Bean.User;
import com.example.notepad.Util.GlideImageLoader;
import com.example.notepad.Util.Util;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.litepal.crud.DataSupport;
import org.litepal.crud.async.FindMultiExecutor;
import org.litepal.crud.callback.FindMultiCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.notepad.Util.DAOTools.findNote;
import static com.example.notepad.Util.DAOTools.findUserByEmail;
import static com.example.notepad.Util.DAOTools.queryNote;
import static com.example.notepad.Util.Util.showToast;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private User user;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ImageView icon;
    private EditText userName;
    private Toolbar toolbar;
    private ActionBar actionbar;
    private SearchView searchView;
    private ViewPager viewPager;

    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User oldUser = (User) getIntent().getSerializableExtra("user");
        user = findUserByEmail(oldUser);

        context = getApplicationContext();

        initView();
        actionBar();
        viewPagerListener();
        navigationItemSelectedListener();
    }

    public void initView(){
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchView = (SearchView)findViewById(R.id.action_search);
        viewPager = (ViewPager) findViewById(R.id.container);
    }

    public void actionBar(){
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    public void viewPagerListener(){
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NoteFragment());
        adapter.addFragment(new RemindFragment());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        actionbar.setTitle("记事本");
                        break;
                    case 1:
                        actionbar.setTitle("日程");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //菜单控制
    public void navigationItemSelectedListener(){
        View view = navigationView.getHeaderView(0);
        icon = (ImageView)view.findViewById(R.id.icon);
        SharedPreferences pref = getSharedPreferences("icon", Context.MODE_PRIVATE);
        String imageString = pref.getString("image", "");
        if(!imageString.isEmpty()){
            byte[] byteArray = Base64.decode(imageString, Base64.DEFAULT);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
            icon.setImageBitmap(bitmap);
        }
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker imagePicker = ImagePicker.getInstance();
                imagePicker.setImageLoader(new GlideImageLoader());
                imagePicker.setMultiMode(false);   //单选/多选
                imagePicker.setShowCamera(true);  //显示拍照按钮
                imagePicker.setFocusWidth(800);
                imagePicker.setFocusHeight(800);
                Intent intent = new Intent(MainActivity.this, ImageGridActivity.class);
                startActivityForResult(intent, 100);
            }
        });
        userName = (EditText) view.findViewById(R.id.user_name);
        userName.setText(user.getUserName());
        userName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (i == EditorInfo.IME_ACTION_SEND || i == EditorInfo.IME_ACTION_DONE
                        || (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.getKeyCode()
                        && KeyEvent.ACTION_DOWN == keyEvent.getAction())) {
                    //处理事件
                    userName.clearFocus();//失去焦点
                    User newUser = new User();
                    newUser.setUserName(userName.getText().toString().trim());
                    if(newUser.updateAll("email=?", user.getEmail()) > 0){
                        Util.showToast(context, "保存成功");
                    }
                }
                return false;
            }
        });
        TextView email = (TextView) view.findViewById(R.id.email);
        email.setText(user.getEmail());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawer.closeDrawer(GravityCompat.START);
                SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();

                switch (item.getItemId()){
                    case R.id.no_encrypted:
                        actionbar.setTitle("记事本");
                        displayControl("false");//显示未加密笔记
                        break;
                    case R.id.encrypted:
                        actionbar.setTitle("加密记事本");
                        if(user.getEmail().equals("temp@temp.com")){
                            displayControl("true");//显示加密笔记
                        }else{
                            alertDialog();
                        }
                        break;
                    case R.id.schedule:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.nav_share://退出
                        finish();
                        break;
                    case R.id.nav_send://注销
                        editor.clear();
                        editor.apply();
                        SharedPreferences.Editor editoricon = getSharedPreferences("icon", MODE_PRIVATE).edit();
                        editoricon.clear();
                        editoricon.apply();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    //加密密码输入框
    public void alertDialog(){
        final View view = View.inflate(this, R.layout.input_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle("请输入登录密码");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MaterialEditText password = (MaterialEditText)view.findViewById(R.id.password);
                String passwordString = password.getText().toString().trim();
                if(passwordString.equals(user.getPassword())){
                    displayControl("true");//显示加密笔记
                }else{
                    showToast(context, "密码错误");
                }
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    //显示加密笔记或未加密笔记
    public void displayControl (String encrypt){
        viewPager.setCurrentItem(0);
        SharedPreferences.Editor editor = getSharedPreferences("encrypt", MODE_PRIVATE).edit();
        editor.putString("encrypt", encrypt);
        editor.apply();
        List<Note>notes = findNote(user, encrypt);
        View view = adapter.getItem(0).getView();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        NoteFragment.refreshNotes(notes, recyclerView);
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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        //searchItem.expandActionView();
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        ComponentName componentName = getComponentName();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        searchView.setQueryHint("搜索笔记");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hintKeyboard();
                viewPager.setCurrentItem(0);
                SharedPreferences pref = getSharedPreferences("encrypt", MODE_PRIVATE);
                String encrypt = pref.getString("encrypt", "false");
                List<Note> notes = queryNote(user, query, encrypt);
                View view = adapter.getItem(0).getView();
                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
                NoteFragment.refreshNotes(notes, recyclerView);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewPager.setCurrentItem(0);
                SharedPreferences pref = getSharedPreferences("encrypt", MODE_PRIVATE);
                String encrypt = pref.getString("encrypt", "false");
                FindMultiExecutor find =  DataSupport.where("userEmail=? and (title like ? or content like ?) and encrypted=?", user.getEmail(), "%"+newText+"%", "%"+newText+"%", encrypt).order("time desc").findAsync(Note.class);
                find.listen(new FindMultiCallback() {
                    @Override
                    public <T> void onFinish(List<T> t) {
                        List<Note> notes = (List<Note>)t;
                        View view = adapter.getItem(0).getView();
                        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
                        NoteFragment.refreshNotes(notes, recyclerView);
                    }
                });
                return true;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    viewPager.setCurrentItem(0);
                }
            }
        });
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 100:{
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    if (data != null) {
                        ArrayList<ImageItem> imageItems = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                        if (imageItems != null && imageItems.size() > 0) {
                            for (int i = 0; i < imageItems.size(); i++) {
                                Bitmap bitmap = BitmapFactory.decodeFile(imageItems.get(i).path);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
                                byte[] byteArray = byteArrayOutputStream.toByteArray();
                                String imageString = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
                                SharedPreferences.Editor editor = getSharedPreferences("icon", MODE_PRIVATE).edit();
                                editor.putString("image", imageString);
                                editor.apply();
                                icon.setImageBitmap(bitmap);
                            }
                        }
                    } else {
                        showToast(context, "没有选择图片");
                    }
                }
                break;
            }
            default:
                break;
        }
    }
}
