package com.jv.listen.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jv.listen.ConstText;
import com.jv.listen.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class DynamicFragment extends Fragment {

    View view = null;
    LineChartView lineChartView;

    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstText.DATA_CHANGE:
                    //  数据发生改变
                    Bundle bundle = (Bundle) msg.obj;
                    ArrayList<String> GPST = (ArrayList<String>) bundle.get("GPST");
                    ArrayList<String> Dist = (ArrayList<String>) bundle.get("Dist");
                    ArrayList<String> Ratio = (ArrayList<String>) bundle.get("Ratio");
                    ArrayList<String> BaseName = (ArrayList<String>) bundle.get("BaseName");
                    ArrayList<String> BX = (ArrayList<String>) bundle.get("BX");
                    ArrayList<String> BY = (ArrayList<String>) bundle.get("BY");
                    ArrayList<String> BZ = (ArrayList<String>) bundle.get("BZ");
                    if(lineChartView == null)
                        return;
                    if(BaseName.size() == 30) { // BaseStationXYZ 图表

                    } else {                    // 其他表

                        List<PointValue> values = new ArrayList<PointValue>();
                        values.add(new PointValue(0, 2));
                        values.add(new PointValue(1, 4));
                        values.add(new PointValue(2, 3));
                        values.add(new PointValue(3, 4));

                        //In most cased you can call data model methods in builder-pattern-like manner.
                        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
                        List<Line> lines = new ArrayList<Line>();
                        lines.add(line);

                        LineChartData data = new LineChartData();
                        data.setLines(lines);
                        lineChartView.setZoomEnabled(false);
                        lineChartView.setLineChartData(data);
                        System.err.println("setData.....!!!");

                    }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dynamic_fragment,container,false);
        lineChartView = view.findViewById(R.id.chart);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public Handler getHandler() {
        return handler;
    }
}
