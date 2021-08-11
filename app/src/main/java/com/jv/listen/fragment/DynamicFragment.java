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
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
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
                        Collections.reverse(GPST);
                        Collections.reverse(Dist);

                        List<PointValue> values = new ArrayList<>();
                        for (int index = 0; index < Dist.size(); index++)
                            values.add(new PointValue(index, new Float(Dist.get(index))));

                        //In most cased you can call data model methods in builder-pattern-like manner.
                        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
                        List<Line> lines = new ArrayList<Line>();
                        lines.add(line);

                        LineChartData data = new LineChartData();
                        data.setLines(lines);
                        lineChartView.setZoomEnabled(false);


                        List<AxisValue> axisX = new ArrayList<>();
                        for (int index = 0; index < GPST.size(); index++)
                            axisX.add(new AxisValue(index).setLabel(GPST.get(index)));

                        List<AxisValue> axisY = new ArrayList<>();
                        for (int index = 0; index < Dist.size(); index++) {
                            axisY.add(new AxisValue(index));
                            System.err.println(Dist.get(index));
                        }

                        Axis axis1 = new Axis();
                        axis1.setValues(axisX);
                        axis1.setTextColor(Color.BLUE);

                        Axis axis2 = new Axis();
//                        axis2.setValues(axisY);
                        axis2.setTextSize(10);
                        axis2.setTextColor(Color.BLUE);

                        data.setAxisXBottom(axis1);
                        data.setAxisYLeft(axis2);


                        lineChartView.setLineChartData(data);

                        System.err.println("setData.....!!!");

                    }
//                        if (lineChartView == null)
//                            return;
//                        System.err.println(Dist);
//                        List<PointValue> mPointValues = new ArrayList<PointValue>();
//                        List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
//                        for (int i = 0; i < GPST.size(); i++) {
//                            mAxisXValues.add(new AxisValue(i).setLabel(GPST.get(i)));
//                        }
//                        for (int i = 0; i < Dist.size(); i++) {
//                            mPointValues.add(new PointValue(i, Float.valueOf(Dist.get(i))));
//                        }
//                        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //折线的颜色（橙色）
//                        List<Line> lines = new ArrayList<Line>();
//                        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
//                        lines.add(line);
//                        LineChartData data = new LineChartData();
//                        data.setLines(lines);
//                        //坐标轴
//                        Axis axisX = new Axis(); //X轴
//                        axisX.setTextColor(Color.GRAY);  //设置字体颜色
//                        //axisX.setName("date");  //表格名称
//                        axisX.setTextSize(10);//设置字体大小
//                        axisX.setMaxLabelChars(8); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
//                        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
//                        data.setAxisXBottom(axisX); //x 轴在底部
//                        axisX.setHasLines(true); //x 轴分割线
//                        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
//                        Axis axisY = new Axis();  //Y轴
//                        axisY.setTextSize(10);//设置字体大小
//                        axisY.setTextColor(Color.GRAY);  //设置字体颜色
//                        data.setAxisYLeft(axisY);  //Y轴设置在左边
//                        lineChartView.setZoomEnabled(false);
//                        lineChartView.setLineChartData(data);
//                    }
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
