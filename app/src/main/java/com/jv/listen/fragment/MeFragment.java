package com.jv.listen.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jv.listen.ConstText;
import com.jv.listen.R;
import com.jv.listen.adapter.StatusAdapter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MeFragment extends Fragment {

    private Context mContext;
    private ListView mStatuslist;
    private View mView;

    private Connection mConnection;
    private boolean isConnect; // 预留 构造时如果为null

    Timer statusUpData = new Timer();

    /**
     * 返回datetime与当前时间的差
     *
     * @param datetime
     * @return datetime与当前时间的差
     * @throws ParseException
     */
    public long DateTo(String datetime) throws ParseException {
        return System.currentTimeMillis() - (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").parse(datetime).getTime() + 8 * 3600000 - 16000);
    }

    /**
     * 默认构造。
     *
     * @param mContext 宿主的上下文
     */
    public MeFragment(Context mContext, Connection connection) {
        this.mContext = mContext;
        if (connection == null) {
            this.isConnect = false;
        } else {
            this.mConnection = connection;
            this.isConnect = true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.me_fragment, container, false);
        mStatuslist = mView.findViewById(R.id.statuslist);
        statusUpData.schedule(new TimerTask() {
            @Override
            public void run() {
                System.err.println("准备............................................");
                dbUpDataStatus();
            }
        }, 0, 1000);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void dbUpDataStatus() {
        if (isViewNull())
            return;
        new Thread(() -> {
            synchronized (mContext) {
                ArrayList<String> mTableName = new ArrayList<>();
                // 获得数据所有表名
                ArrayList<String> mListItemName = new ArrayList();
                ArrayList<Long> mListItemValue = new ArrayList();
                try {
                    ResultSet mTableSet = mConnection.createStatement().executeQuery("show tables;");

                    if (mTableSet == null)
                        return;
                    while (mTableSet.next()) {
                        mTableName.add(mTableSet.getString(1));
                    }
                    for (int i = 0; i < mTableName.size(); i++) {
                        ResultSet table = mConnection.createStatement().executeQuery("SELECT * FROM " + mTableName.get(i) + " order by ID desc limit 1;");
                        // "SELECT * FROM BaseStationXYZ order by ID desc limit 1;"
                        long difference = -1;
                        while (table.next()) {
                            difference = DateTo(table.getString("GPST"));
                            mListItemName.add(mTableName.get(i));
                            mListItemValue.add(difference);
                        }
                    }
                } catch (SQLException | ParseException throwables) {
                    throwables.printStackTrace();
                }
                getActivity().runOnUiThread(() -> {
                    mStatuslist.setAdapter(new StatusAdapter(getContext(), mListItemName, mListItemValue, getResources().getColor(R.color.lsRed), getResources().getColor(R.color.lsGreen)));
                });
            }
        }).start();
    }

    private boolean isViewNull() {
        return mContext == null || mView == null || mStatuslist == null;
    }

    public MeFragment getMeFragment() {
        return this;
    }

    public void setConnection(Connection mConnection) {
        this.mConnection = mConnection;
    }
}
