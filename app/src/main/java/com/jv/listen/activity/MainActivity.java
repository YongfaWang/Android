package com.jv.listen.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jv.listen.R;
import com.jv.listen.fragment.DynamicFragment;
import com.jv.listen.fragment.HomeFragment;
import com.jv.listen.fragment.MeFragment;
import com.jv.listen.utils.Permission;
import com.jv.listen.utils.StatusBar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Connection connection;
    private Statement statement;
    private ArrayList<String> databaseName = new ArrayList<>();

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment;
    DynamicFragment dynamicFragment;
    MeFragment meFragment;
    Fragment[] fragments;
    int lastFragment;//用于记录上个选择的Fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Permission.IgnoringBatteryOptimizations.isIgnoringBatteryOptimizations(this)) {
                Toast.makeText(this, "注意!接下来应用可能会向你申请一个权限,它会防止应用在熄屏后和数据库断开,你如果不同意权限,应用仍可运行,但熄屏后,此应用的所有功能在本次执行将失效.", Toast.LENGTH_SHORT).show();
                AlertDialog alertDialog =  new AlertDialog.Builder(this).setTitle("注意你的弹窗")
                        .setMessage("注意!接下来应用可能会向你申请一个权限,它会防止应用在熄屏后和数据库断开,你如果不同意权限,应用仍可运行,但熄屏后,此应用的所有功能在本次执行将失效.\n如果你后悔禁止权限,你可以重启应用,重启如果不可用,可以打开设置搜索'电池优化'将此权限设为允许.").create();
                alertDialog.show();
                Permission.IgnoringBatteryOptimizations.requestIgnoreBatteryOptimizations(this);
            }
        }
        StatusBar.setStatusBarMode(this, true, R.color.white);
        // StatusBar.FullScreen.fitsSystemWindows(this);
        Window window = MainActivity.this.getWindow();
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
                    connection = DriverManager.getConnection("jdbc:mysql://112.46.66.4:3306/mysql?serverTimezone=UTC&useSSL=false","root","xgw123456");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    System.err.println("SQL Error Message ->\n" + throwables.getMessage());
                    return;
                }
                if(connection == null) {
                    return;
                }
                try {
                    statement = connection.createStatement();
                    ResultSet mList = statement.executeQuery("show databases;");
                    int count = mList.getMetaData().getColumnCount();
                    // 取出带有RTK字符的数据库名
                    while (mList.next())
                        for (int i = 0; i < count; i++)
                            if(mList.getString(i + 1).contains("RTK")) databaseName.add(mList.getString(i + 1));
                    connection = DriverManager.getConnection("jdbc:mysql://112.46.66.4:3306/" + databaseName.get(0) + "?serverTimezone=UTC&useSSL=false","root","xgw123456");
                    statement = connection.createStatement();
                    if(connection == null)
                        System.err.println("指定数据库返回NULL！！！");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
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
        homeFragment.setDatabaseList(databaseName);
    }
    //初始化fragment和fragment数组
    void initFragment()
    {
        dynamicFragment = new DynamicFragment(MainActivity.this);
        meFragment = new MeFragment(MainActivity.this, connection);
        System.err.println("传入构造时的地址"+ connection);
        homeFragment = new HomeFragment(connection,statement, MainActivity.this,dynamicFragment.getHandler(),meFragment.getMeFragment());
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
