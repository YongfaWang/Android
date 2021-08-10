package com.jv.listen.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jv.listen.R;
import com.jv.listen.fragment.DynamicFragment;
import com.jv.listen.fragment.HomeFragment;
import com.jv.listen.fragment.MeFragment;
import com.jv.listen.utils.StatusBar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Home extends AppCompatActivity {

    private Connection connection = null;
    private Statement statement = null;

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment;
    DynamicFragment dynamicFragment;
    MeFragment meFragment;
    Fragment[] fragments;
    int lastFragment;//用于记录上个选择的Fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        StatusBar.setStatusBarMode(this, true, R.color.white);
        // StatusBar.FullScreen.fitsSystemWindows(this);
        Window window = Home.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        StyleableToast.makeText(Home.this,"发哥万岁！", Toast.LENGTH_LONG,R.style.mytoast).show();
        FragmentManager fragmentManager =  this.getSupportFragmentManager();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    System.err.println("--------------------------------------------");
                    e.printStackTrace();
                    System.err.println("Class Error Message ->\n" + e.getMessage());
                }
                try {
                    connection = DriverManager.getConnection("jdbc:mysql://113.219.245.205:3306/RTKSaveDataRelease?serverTimezone=UTC&useSSL=false","root","123456");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    System.err.println("SQL Error Message ->\n" + throwables.getMessage());
                }
                if(connection == null)
                    System.err.println("连接对象为空");
                try {
                    statement = connection.createStatement();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    System.err.println("SQL Error Message ->\n" + throwables.getMessage());
                }
//                String sql = "select * from LJH01_LJH15";
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        initFragment();
    }
    //初始化fragment和fragment数组
    void initFragment()
    {
        dynamicFragment = new DynamicFragment();
        homeFragment = new HomeFragment(connection,statement,Home.this,dynamicFragment.getHandler());
        meFragment = new MeFragment();
        fragments = new Fragment[]{ homeFragment, dynamicFragment, meFragment };
        lastFragment = 0;
        getSupportFragmentManager().beginTransaction().replace(R.id.views,homeFragment).show(homeFragment).commit();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(changeFragment);
    }

    BottomNavigationView.OnNavigationItemSelectedListener changeFragment= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId())
            {
                case R.id.navigation_home:
                {
                    if(lastFragment!=0)
                    {
                        switchFragment(lastFragment,0);
                        lastFragment=0;
                    }
                    return true;
                }
                case R.id.navigation_dynamic:
                {
                    if(lastFragment!=1)
                    {
                        switchFragment(lastFragment,1);
                        lastFragment=1;

                    }

                    return true;
                }
                case R.id.navigation_me:
                {
                    if(lastFragment!=2)
                    {
                        switchFragment(lastFragment,2);
                        lastFragment=2;

                    }

                    return true;
                }


            }


            return false;
        }
    };


    //切换Fragment
    void switchFragment(int lastfragment,int index)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
        if (fragments[index].isAdded() == false) {
            transaction.add(R.id.views, fragments[index]);


        }
        transaction.show(fragments[index]).commitAllowingStateLoss();


    }
}
