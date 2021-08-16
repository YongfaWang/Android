package com.jv.listen.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.jv.listen.ConstText;
import com.jv.listen.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MeFragment extends Fragment {

    Context context;
    CardView indicatorLight;
    TextView speed;

    Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ConstText.DATA_UPDATA_STATUS:
                    if(speed == null || indicatorLight == null)
                        return;
                    Bundle bundle = (Bundle) msg.obj;
                    try {
                        System.err.println("----------------------------f");
                        long difference = DateTo(bundle.get("DATE").toString());
                        speed.setText("Current table last DateTime and local time difference:" + difference);
                        // 超过 1 分钟,服务器可能停止更新.之后指示灯颜色变红.
                        if(difference > 60000)
                            indicatorLight.setCardBackgroundColor(getResources().getColor(R.color.lsRed));
                        else
                            indicatorLight.setCardBackgroundColor(getResources().getColor(R.color.lsGreen));
                        System.err.println("延迟:" + DateTo(bundle.get("DATE").toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    System.err.println("数据库" + bundle.get("DATE"));
                    System.err.println("当前时间：" + System.currentTimeMillis());
                    System.err.println("----------------------------l");
                    break;
            }
        }
    };

    public long DateTo(String datetime) throws ParseException {
        System.err.println("数据库时间戳" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").parse(datetime).getTime());
        return System.currentTimeMillis() - (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").parse(datetime).getTime() + 8 * 3600000 - 16000);
    }
    /**
     *  默认构造。
     * @param context   宿主的上下文
     */
    public MeFragment(Context context) {
        this.context = context;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.me_fragment,container,false);
        indicatorLight = view.findViewById(R.id.indicatorLight);
        speed = view.findViewById(R.id.speed);
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
