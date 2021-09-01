package com.jv.listen.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jv.listen.ConstText;
import com.jv.listen.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private MeFragment connectChange;
    private Handler echats_handler;
    private Connection connection;
    private Statement statement;
    private Context context;         // 上下文对象,操作UI需要
    private Spinner tablesSpinner;         // 下拉菜单组件
    private Spinner databaseSpinner;         // 下拉菜单组件
    private EditText getdatanum;
    private int getDataConut = 30;

    // 数据库数据
    private String DATE = new String();
    private ArrayList<String> databaselist = new ArrayList<>();
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
        if (statement == null || context == null || connection == null || databaseSpinner == null || getdatanum == null || tablesSpinner == null)
            return;
        if (databaselist.size() != 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.listview_item, databaselist);
            t_handler.post(new Runnable() {
                @Override
                public void run() {
                    databaseSpinner.setAdapter(adapter);
                    databaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            new Thread(() -> {
                                String sql = "show tables;";
                                ResultSet rs = null;
                                try {
                                    connection = DriverManager.getConnection("jdbc:mysql://112.46.66.4:3306/" + databaseSpinner.getSelectedItem().toString() + "?serverTimezone=UTC&useSSL=false", "root", "xgw123456");
                                    statement = connection.createStatement();
                                    System.err.println("数据库发生改变！！！");
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                                try {
                                    rs = statement.executeQuery(sql);
                                    if (rs == null)
                                        return;
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    System.err.println("SQL Error Message ->\n" + e.getMessage());
                                }
                                connectChange.setConnection(connection);
                                try {
                                    arrayList.clear();
                                    while (rs.next()) {
                                        arrayList.add(rs.getString(1));
                                        // GPST DIST    RATIO
                                    }
                                    ArrayAdapter<String> adapter1 = new ArrayAdapter<>(context, R.layout.listview_item, arrayList);
                                    t_handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tablesSpinner.setAdapter(adapter1);
                                            upData();
                                        }
                                    });
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                                t_handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(context, R.layout.listview_item, arrayList);
                                        tablesSpinner.setAdapter(adapter1);
                                    }
                                });
                            }).start();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            });
        }
    });

    private void upData() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.err.println("调用...");

//                int line = 0;
                Thread selectTable = new Thread(() -> {
                    /**
                     * 使用线程锁的原因：MeFragment 和 HomeFrage使用的同一个Concention对象,都带有定时器,当 HomeFragment 请求量
                     * 非常大的时候,可能会发生响应延迟先现象,然后 MeFragment 的定时触发会执行新的SQL语句,这时候 ResultSet 会发生改变,
                     * HomeFragement 在使用getString()等方法的时候,这个ResultSet的字段已经变成了MeFragment获取的字段,这样,HomeFragment
                     * 获取的时候可能没有这个字段,从而导致空指针异常,为了解决这个异常,我使用了最便捷的方法。
                     */
                    synchronized (context) {
                        clearAllList();
                        SQL_SELECT_UNKOWN_TABLE = "SELECT * FROM " + tablesSpinner.getSelectedItem().toString() + " order by ID desc limit " + getDataConut + ";";
                        ResultSet resultSet = null;
                        try {
                            if (statement.isClosed()) {
                                System.err.println("调用isClosed()为true");
                                return;
                            }
                            System.err.println("构成语句2：" + SQL_SELECT_UNKOWN_TABLE);
                            resultSet = statement.executeQuery(SQL_SELECT_UNKOWN_TABLE);
                            System.err.println("构成语句3：" + SQL_SELECT_UNKOWN_TABLE);
                            if (resultSet == null) {
                                return;
                            }
                            ResultSetMetaData data = resultSet.getMetaData();
                            System.err.println("输出字段" + data.getColumnCount());
                            for (int i = 1; i <= data.getColumnCount(); i++) {
                                System.err.println(data.getColumnName(i));
                            }
                            boolean statuslook = true;
                            if (tablesSpinner.getSelectedItem().toString().equals("BaseStationXYZ")) {
                                while (resultSet.next()) {
                                    // 仅取时分秒,毫秒的四舍五入结果对秒影响
                                    System.err.println("开始获取GPST");
                                    String str = resultSet.getString("GPST").substring(11, 19);
                                    System.err.println("完成获取GPST");
                                    String str2 = resultSet.getString("GPST").substring(20, 21);
                                    if (Integer.parseInt(str2) > 5) {
                                        int s = Integer.parseInt(str.substring(str.length() - 1));
                                        ++s;
                                        str = str.substring(0, str.length() - 1) + s;
                                        if (s >= 10) {
                                            str = str.substring(0, str.length() - 2) + s;
                                        } else {
                                            str = str.substring(0, str.length() - 1) + s;
                                        }
                                    }
                                    if (statuslook) {
                                        DATE = resultSet.getString("GPST");
                                        statuslook = false;
                                    }
                                    GPST.add(str);
                                    System.err.println("找到->" + resultSet.getString("BaseName"));
                                    BaseName.add(resultSet.getString("BaseName"));
                                    BX.add(resultSet.getString("BX"));
                                    BY.add(resultSet.getString("BY"));
                                    BZ.add(resultSet.getString("BZ"));
//                                ++line;
                                }

                            } else {
                                while (resultSet.next()) {
                                    // 仅取时分秒,毫秒的四舍五入结果对秒影响
                                    String str = resultSet.getString("GPST").substring(11, 19);
                                    String str2 = resultSet.getString("GPST").substring(20, 21);
                                    if (Integer.parseInt(str2) > 5) {
                                        int s = Integer.parseInt(str.substring(str.length() - 1));
                                        ++s;
                                        if (s >= 10) {
                                            str = str.substring(0, str.length() - 2) + s;
                                        } else {
                                            str = str.substring(0, str.length() - 1) + s;
                                        }
                                    }
                                    if (statuslook) {
                                        DATE = resultSet.getString("GPST");
                                        statuslook = false;
                                    }
                                    System.err.println("开始get数据");
                                    if (resultSet == null)
                                        System.err.println("结果集为空");
                                    else
                                        System.err.println("结果集不为空");
                                    GPST.add(str);
                                    System.err.println("获取Dist" + resultSet.getString("Dist"));
                                    Dist.add(resultSet.getString("Dist"));
                                    Ratio.add(resultSet.getString("Ratio"));
                                    dRX.add((resultSet.getString("dRX")));
                                    dRY.add((resultSet.getString("dRY")));
                                    dRZ.add((resultSet.getString("dRZ")));
                                    System.err.println("完成");
                                }
                            }
                        } catch (SQLException throwables) {
                            if (throwables.getErrorCode() == 0)
                                System.err.println("SQL 断开连接");
                            throwables.printStackTrace();
                            return;
                        }

                        Bundle bundle = new Bundle();
                        ArrayList<String> mGPST = new ArrayList<>();
                        for (int i = 0; i < GPST.size(); i += getDataConut / 30)
                            mGPST.add(GPST.get(i));
                        bundle.putStringArrayList("GPST", mGPST);
                        bundle.putStringArrayList("Dist", Dist);
                        bundle.putStringArrayList("Ratio", Ratio);
                        bundle.putStringArrayList("BaseName", BaseName);
                        bundle.putStringArrayList("BX", BX);
                        bundle.putStringArrayList("BY", BY);
                        bundle.putStringArrayList("BZ", BZ);
                        bundle.putStringArrayList("dRX", dRX);
                        bundle.putStringArrayList("dRY", dRY);
                        bundle.putStringArrayList("dRZ", dRZ);
                        echats_handler.sendMessage(echats_handler.obtainMessage(ConstText.DATA_CHANGE, bundle));
                        // 异步操作UI,将run函数交给UI线程去处理
                        t_handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (tablesSpinner.getSelectedItem().toString().equals("BaseStationXYZ")) {
                                    String temp = "";
                                    for (int index = 0; index < GPST.size(); index++)
                                        temp +=
                                                GPST.get(index) + "\t " +
                                                        BaseName.get(index) + "\t " +
                                                        BX.get(index) + "\t " +
                                                        BY.get(index) + "\t " +
                                                        BZ.get(index) + "\n\n";
                                    textView.setText(temp);
                                } else {
                                    String temp = "";
                                    System.err.println("GPST SIZE:" + GPST.size());
                                    for (int index = 0; index < GPST.size(); index++)
                                        temp +=
                                                GPST.get(index) + "\t " +
                                                        Dist.get(index) + "\t " +
                                                        Ratio.get(index) + "\t " +
                                                        dRX.get(index) + "\t " +
                                                        dRY.get(index) + "\t " +
                                                        dRZ.get(index) + "\n\n";
                                    textView.setText(temp);
                                }
                            }
                        });
                    }
                });
                selectTable.start();
            }
        }, 1000, 2000);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        retView = inflater.inflate(R.layout.home_fragment, container, false);
        textView = retView.findViewById(R.id.result_textview);
        tablesSpinner = retView.findViewById(R.id.tablesSpinner);
        databaseSpinner = retView.findViewById(R.id.databaseSpinner);
        getdatanum = retView.findViewById(R.id.getdatanum);
        textView.setMovementMethod(new ScrollingMovementMethod());//设置textview可以滑动
        textView.setScrollbarFadingEnabled(false);//设置scrollbar一直显示
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textView.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        getdatanum.setText("30");
        getdatanum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String v = getdatanum.getText().toString();
                if ("".equals(v))
                    return;
                int vaule = Integer.parseInt(v);
                System.err.println("----------------------------------------" + vaule);
                if (vaule < 30 || vaule > 10000) {
                    Toast.makeText(context, "不允许获取过大或过小的数值", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    getDataConut = vaule;
                }
                //getDataConut
            }

            @Override
            public void afterTextChanged(Editable editable) {

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public HomeFragment(Connection connection, Statement statement, Context context, Handler handler, MeFragment connectChange) {
        this.connection = connection;
        this.statement = statement;
        this.context = context;
        this.echats_handler = handler;
        this.connectChange = connectChange;
    }

    /**
     * 设置数据库下拉列表数据
     *
     * @param arrayList 数据库名集合
     */
    public void setDatabaseList(ArrayList<String> arrayList) {
        databaselist = arrayList;
    }
}
