package com.jv.listen.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jv.listen.ConstText;
import com.jv.listen.R;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class DynamicFragment extends Fragment {

    View view;
    LineChartView lineChartView;
    Spinner spinner;
    TextView lineMapTitle;
    Context context;


    public DynamicFragment(Context context) {
        this.context = context;
    }
    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstText.DATA_CHANGE:
                    if(isViewNull())
                        return;
                    Bundle bundle = (Bundle) msg.obj;
                    ArrayList<String> GPST = (ArrayList<String>) bundle.get("GPST");
                    ArrayList<String> Dist = (ArrayList<String>) bundle.get("Dist");
                    ArrayList<String> Ratio = (ArrayList<String>) bundle.get("Ratio");
                    ArrayList<String> BaseName = (ArrayList<String>) bundle.get("BaseName");
                    ArrayList<String> BX = (ArrayList<String>) bundle.get("BX");
                    ArrayList<String> BY = (ArrayList<String>) bundle.get("BY");
                    ArrayList<String> BZ = (ArrayList<String>) bundle.get("BZ");
                    ArrayList<String> dRX = (ArrayList<String>) bundle.get("dRX");
                    ArrayList<String> dRY = (ArrayList<String>) bundle.get("dRY");
                    ArrayList<String> dRZ = (ArrayList<String>) bundle.get("dRZ");
                    if(BaseName.size() > Dist.size()) { // BaseStationXYZ ??????
                    } else {                    // ?????????
                        // ????????????
                        Collections.reverse(GPST);
                        Collections.reverse(Dist);
                        Collections.reverse(Ratio);
                        Collections.reverse(dRX);
                        Collections.reverse(dRY);
                        Collections.reverse(dRZ);
                        switch (spinner.getSelectedItem().toString()) {
                            case "Dist":
                                DrawLine(GPST, Dist);
                                break;
                            case "Ratio":
                                DrawLine(GPST, Ratio);
                                break;
                            case "dRX":
                                DrawLine(GPST, dRX);
                                break;
                            case "dRY":
                                DrawLine(GPST, dRY);
                                break;
                            case "dRZ":
                                DrawLine(GPST, dRZ);
                                break;
                        }
                        // ??????????????????
                        lineMapTitle.setText(spinner.getSelectedItem().toString());
                    }
            }
        }
    };

    private boolean isViewNull() {
        return lineChartView == null || spinner == null || context == null || lineMapTitle == null;
    }

    /**
     *  ?????????????????????
     *  ???????????????????????????,????????????????????????????????????
     *  ???????????????????????????????????????????????????????????????????????????
     *  ????????????????????????????????????????????????
     * @param xList X ??? ??????(??????)
     * @param yList Y ??? ?????????
     */
    private void DrawLine(ArrayList<String> xList,ArrayList<String> yList) {
        // ?????????
        List<PointValue> values = new ArrayList<>();
        for (int index = 0; index < yList.size(); index++)
            values.add(new PointValue(index, Float.valueOf(yList.get(index))));

        // ?????? ???
        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<>();
        line.setStrokeWidth(2);
//        line.setPointRadius(3);
        line.setHasPoints(false);
        line.setCubic(false);//?????????????????????????????????????????????\
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);
        lineChartView.setZoomEnabled(false);


        // X ??? ??????
        List<AxisValue> axisX = new ArrayList<>();
        for (int index = 0,listIndex = 0; index < yList.size(); index += yList.size() / 30,listIndex++)
            axisX.add(new AxisValue(index).setLabel(xList.get(listIndex)));
        // X ???
        Axis axis1 = new Axis();
        axis1.setValues(axisX);
        axis1.setTextSize(10);
        axis1.setTextColor(Color.BLUE);
        // Y ???
        Axis y = new Axis();
        y.setInside(true);
        y.setTextSize(7);
        y.setTextColor(Color.BLUE);
        // ????????????
        data.setAxisXBottom(axis1);
        data.setAxisYLeft(y);
        // ?????????????????????,??????????????? Float ????????????
        ArrayList<Float> m_maxArray = new ArrayList<>();
        for(int index = 0; index < yList.size(); index++)
            m_maxArray.add(Float.parseFloat(yList.get(index)));
        lineChartView.setLineChartData(data);
        final Viewport v = new Viewport(lineChartView.getMaximumViewport());
        v.top = Float.parseFloat(Collections.max(m_maxArray).toString());
        v.bottom = Float.parseFloat(Collections.min(m_maxArray).toString());
        lineChartView.setMaximumViewport(v);
        lineChartView.setCurrentViewport(v);
        System.err.println("setData.....!!!");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dynamic_fragment,container,false);
        lineChartView = view.findViewById(R.id.chart);
        spinner = view.findViewById(R.id.dataComs);
        lineMapTitle = view.findViewById(R.id.lineMapTitle);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Dist");
        arrayList.add("Ratio");
        arrayList.add("dRX");
        arrayList.add("dRY");
        arrayList.add("dRZ");

        spinner.setAdapter(new ArrayAdapter<>(context, R.layout.listview_item, arrayList));
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
