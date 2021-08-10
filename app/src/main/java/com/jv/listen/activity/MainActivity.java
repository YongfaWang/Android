package com.jv.listen.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jv.listen.R;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import io.github.muddz.styleabletoast.StyleableToast;

public class MainActivity extends AppCompatActivity {

    TextView signin;
    TextView signup;
    TextView forget;
    EditText username;
    EditText password;

    BmobUser bmobUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.signup);
        forget = findViewById(R.id.forget_password);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);




        Bmob.initialize(this, "908ca541b00136936c875b300345d9c3");
        bmobUser = BmobUser.getCurrentUser();  // 这个函数的返回值是一个用户缓存,如果可以返回则证明上次登录过,反之需要登录
        if(bmobUser == null) {
            bmobUser = new BmobUser();
        } else {
            startActivity(new Intent(MainActivity.this, Home.class));
        }
        signin.setOnClickListener(view -> {
            bmobUser.setUsername(username.getText().toString());
            bmobUser.setPassword(password.getText().toString());
            bmobUser.login(new SaveListener<BmobUser>() {
                @Override
                public void done(BmobUser o, BmobException e) {
                    if(e != null)
                        StyleableToast.makeText(MainActivity.this,"登录失败：" + e, Toast.LENGTH_LONG,R.style.mytoast).show();
                    else {
                        StyleableToast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_LONG, R.style.mytoast).show();
                        startActivity(new Intent(MainActivity.this, Home.class));
                    }
                }
            });
            // StyleableToast.makeText(MainActivity.this,"登录", Toast.LENGTH_LONG,R.style.mytoast).show();
        });
        signup.setOnClickListener(view -> {

        });
    }
}