package com.jv.listen.fragment;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jv.listen.ConstText;
import com.jv.listen.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.github.muddz.styleabletoast.StyleableToast;

public class HomeFragment extends Fragment {

    private Handler echats_handler;
    Handler statusHandler;
    private Connection connection = null;
    private Statement statement = null;
    private Context context = null;         // 上下文对象,操作UI需要
    private Spinner spinner = null;         // 下拉菜单组件

    // 数据库数据
    private String DATE = new String();
    private ArrayList<String> GPST = new ArrayList<>();     // 其他
    private ArrayList<String> Dist = new ArrayList<>();
    private ArrayList<String> Ratio = new ArrayList<>();
    private ArrayList<String> BaseName = new ArrayList<>(); // station
    private ArrayList<String> BX = new ArrayList<>();
    private ArrayList<String> BY = new ArrayList<>();
    private ArrayList<String> BZ = new ArrayList<>();
    private ArrayList<String> dRX = new ArrayList<>();
    private ArrayList<String> dRY = new ArrayList<>();
    private ArrayList<String> dRZ = new ArrayList<>();

    // 数据库语句(查询TABLES表内的NUMBER条数据)
    private String SQL_SELECT_UNKOWN_TABLE = "SELECT * FROM <TABLES> order by ID desc limit <NUMBER>;";

    // 数据库中所有表
    ArrayList<String> arrayList = new ArrayList<>();
    Handler t_handler = new Handler();
    View retView = null;
    TextView textView = null;
    Timer timer = new Timer();
    private Thread tableListThread = new Thread(() -> {
        // timer.cancel();
        if(connection == null)
            Log.e("HomeFragment","NULL Connection");
        if(statement == null)
            Log.e("HomeFragment","NULL Statement");
        if(context == null)
            Log.e("HomeFragment","NULL Context");
        String sql = "show tables;";
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(sql);
            if(rs == null)
                return;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error Message ->\n" + e.getMessage());
        }
        try {
            while(rs.next()) {
                arrayList.add(rs.getString(1));
                // GPST DIST    RATIO
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.listview_item,arrayList);
            t_handler.post(new Runnable() {
                @Override
                public void run() {
                    spinner.setAdapter(adapter);
                }
            });
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    StyleableToast.makeText(context, "没有选择", Toast.LENGTH_LONG, R.style.mytoast).show();
                }
            });
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        upData();
    });

    private void upData() {
        timer.schedule(new TimerTask() {
        @Override
        public void run() {
        System.err.println("调用...");

//                int line = 0;
        Thread selectTable = new Thread(() -> {
            clearAllList();
            SQL_SELECT_UNKOWN_TABLE = "SELECT * FROM " + spinner.getSelectedItem().toString() + " order by ID desc limit 30;";
            ResultSet resultSet = null;
            try {
                if(statement.isClosed()) {
                    return;
                }
                resultSet = statement.executeQuery(SQL_SELECT_UNKOWN_TABLE);
                if(resultSet == null) {
                    return;
                }
                boolean statuslook = true;
                if(spinner.getSelectedItem().toString().equals("BaseStationXYZ"))
                {
                    while(resultSet.next())
                    {
                        // 仅取时分秒,毫秒的四舍五入结果对秒影响
                        String str = resultSet.getString("GPST").substring(11,19);
                        String str2 = resultSet.getString("GPST").substring(20,21);
                        if(Integer.parseInt(str2) > 5) {
                            int s = Integer.parseInt(str.substring(str.length() - 1));
                            ++s;
                            str = str.substring(0,str.length() - 1) + s;
                            if(s >= 10) {
                                str = str.substring(0,str.length() - 2) + s;
                            }
                            else {
                                str = str.substring(0,str.length() - 1) + s;
                            }
                        }
                        if(statuslook) {
                            DATE = resultSet.getString("GPST");
                            statuslook = false;
                        }
                        GPST.add(str);
                        BaseName.add(resultSet.getString("BaseName"));
                        BX.add(resultSet.getString("BX"));
                        BY.add(resultSet.getString("BY"));
                        BZ.add(resultSet.getString("BZ"));
//                                ++line;
                    }

                } else {
                    while(resultSet.next())
                    {
                        // 仅取时分秒,毫秒的四舍五入结果对秒影响
                        String str = resultSet.getString("GPST").substring(11,19);
                        String str2 = resultSet.getString("GPST").substring(20,21);
                        if(Integer.parseInt(str2) > 5) {
                            int s = Integer.parseInt(str.substring(str.length() - 1));
                            ++s;
                            if(s >= 10) {
                                str = str.substring(0,str.length() - 2) + s;
                            }
                            else {
                                str = str.substring(0,str.length() - 1) + s;
                            }
                        }
                        if(statuslook) {
                            DATE = resultSet.getString("GPST");
                            statuslook = false;
                        }
                        GPST.add(str);
                        Dist.add(resultSet.getString("Dist"));
                        Ratio.add(resultSet.getString("Ratio"));
                        dRX.add((resultSet.getString("dRX")));
                        dRY.add((resultSet.getString("dRY")));
                        dRZ.add((resultSet.getString("dRZ")));
                    }
                }

                // 发送最后一个时间
                if(statusHandler != null) {
                    sendStatus();
                }
            } catch (SQLException throwables) {
                if(throwables.getErrorCode() == 0)
                    System.err.println("SQL 断开连接");
//                throwables.printStackTrace();
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putStringArrayList("GPST",GPST);
            bundle.putStringArrayList("Dist",Dist);
            bundle.putStringArrayList("Ratio",Ratio);
            bundle.putStringArrayList("BaseName",BaseName);
            bundle.putStringArrayList("BX",BX);
            bundle.putStringArrayList("BY",BY);
            bundle.putStringArrayList("BZ",BZ);
            bundle.putStringArrayList("dRX",dRX);
            bundle.putStringArrayList("dRY",dRY);
            bundle.putStringArrayList("dRZ",dRZ);
            echats_handler.sendMessage(echats_handler.obtainMessage(ConstText.DATA_CHANGE,bundle));
            // 异步操作UI,将run函数交给UI线程去处理
            t_handler.post(new Runnable() {
                @Override
                public void run() {
                    if(spinner.getSelectedItem().toString().equals("BaseStationXYZ")) {
                        String temp = "";
                        for(int index = 0; index < GPST.size(); index++) {
                            temp += GPST.get(index) + "\t " + BaseName.get(index) + "\t " + BX.get(index) + "\t " + BY.get(index) + "\t " + BZ.get(index) + "\n\n";
                        }
                        textView.setText(temp);
                    } else {
                        String temp = "";
                        for(int index = 0; index < GPST.size(); index++) {
                            temp += GPST.get(index) + "\t " + Dist.get(index) + "\t " + Ratio.get(index) + dRX.get(index) + "\t " + dRY.get(index) + "\t " + dRZ.get(index) +  "\n\n";
                        }
                        textView.setText(temp);
                    }
                }
            });
        });
        selectTable.start();
        }
        }, 1000, 2000);
    }


    public HomeFragment(Connection connection, Statement statement, Context context, Handler handler) {
        this.connection = connection;
        this.statement = statement;
        this.context = context;
        this.echats_handler = handler;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        retView = inflater.inflate(R.layout.home_fragment,container,false);
        textView = retView.findViewById(R.id.result_textview);
        spinner = retView.findViewById(R.id.spinner);
        textView.setMovementMethod(new ScrollingMovementMethod());//设置textview可以滑动
        textView.setScrollbarFadingEnabled(false);//设置scrollbar一直显示
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textView.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        // 可以在这里写布局事件
        tableListThread.start();
        return retView;
    }

    private void clearAllList() {
        GPST.clear();
        Dist.clear();
        Ratio.clear();
        BaseName.clear();
        BX.clear();
        BY.clear();
        BZ.clear();
        dRX.clear();
        dRY.clear();
        dRZ.clear();
    }


    public void setStatusHandler(Handler handler) {
        this.statusHandler = handler;
    }

    private void sendStatus() {
        Bundle bundle = new Bundle();
        bundle.putString("DATE",DATE);
        statusHandler.sendMessage(statusHandler.obtainMessage(ConstText.DATA_UPDATA_STATUS,bundle));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
