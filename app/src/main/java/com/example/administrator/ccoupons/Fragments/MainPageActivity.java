package com.example.administrator.ccoupons.Fragments;
/*
*首页布局
 */

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.administrator.ccoupons.AddCoupon.FillFormActivity;
import com.example.administrator.ccoupons.AddCoupon.QRcodeActivity;
import com.example.administrator.ccoupons.Connections.MessageGetService;
import com.example.administrator.ccoupons.MyApp;
import com.example.administrator.ccoupons.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainPageActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener{


    @BindView(R.id.main_bottombar)
    BottomNavigationBar bottomLayout;
    private ArrayList<Message> messageList;
    private boolean exit = false;
    private AlarmReceiver receiver;

    private CategoryFragment categoryFragment;
    private UserOptionFragment userOptionFragment;
    private MessageFragment messageFragment;

    private Fragment[] fragments = new Fragment[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        ButterKnife.bind(this);
        initFragments();
        initNavigationBar();
        initService();
    }

    private void initService() {

        receiver = new AlarmReceiver();
        IntentFilter filter = new IntentFilter("com.example.administrator.ccoupons.MESSAGE_BROADCAST");
        registerReceiver(receiver, filter);

        Intent intent = MessageGetService.newIntent(this);
        startService(intent);
        MessageGetService.setServiceAlarm(this);

    }


    private void initFragments() {
        categoryFragment = new CategoryFragment();
        userOptionFragment = new UserOptionFragment();
        messageFragment = new MessageFragment();
        fragments[0] = categoryFragment;
        fragments[1] = messageFragment;
        fragments[2] = userOptionFragment;
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragment_frame, categoryFragment);
        fragmentTransaction.add(R.id.fragment_frame, messageFragment);
        fragmentTransaction.add(R.id.fragment_frame, userOptionFragment);
        fragmentTransaction.commit();
        showFragment(0);
    }


    private void showFragment(int index) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        hideAllFragments(ft);
        ft.show(fragments[index]);
        ft.commitAllowingStateLoss();
    }

    private void hideAllFragments(FragmentTransaction ft) {
        if (categoryFragment != null)
            ft.hide(categoryFragment);
        if (userOptionFragment != null) {
            ft.hide(userOptionFragment);
        }
        if (messageFragment != null)
            ft.hide(messageFragment);
    }

    //初始化底部导航栏
    private void initNavigationBar() {
        bottomLayout.setMode(BottomNavigationBar.MODE_FIXED);
        bottomLayout.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomLayout.addItem(new BottomNavigationItem(R.drawable.main_mainpage, "主页").setActiveColor(R.color.blue))
                .addItem(new BottomNavigationItem(R.drawable.message, "消息").setActiveColor(R.color.red))
                .addItem(new BottomNavigationItem(R.drawable.main_me, "我的").setActiveColor(R.color.yellow))
                .setFirstSelectedPosition(0)
                .initialise();
        bottomLayout.setTabSelectedListener(this);
    }


    @Override
    public void onTabSelected(int position) {
        if (fragments != null) {
            showFragment(position);
        }

    }

    @Override
    public void onTabUnselected(int position) {
    }

    @Override
    public void onTabReselected(int position) {

    }


    //按返回键回到F1,在F1双击返回键退出
    @Override
    public void onBackPressed() {
        if (exit) {
            this.finish();
        } else {
            exit = true;
            Toast.makeText(this,
                    "再按返回键退出程序", Toast.LENGTH_SHORT).show();
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    exit = false;
                    timer.cancel();
                }
            }, 2000);
        }
    }


    /*

    {"messageResult":
    [{"messageid": "001", "content": "lalala", "time": "2017-01-01",
    "messagecat": "\u4e0a\u67b6\u7684\u4f18\u60e0\u5238\u88ab\u8d2d\u4e70", "hasread": 0, "couponid": "001"},
    {"messageid": "002", "content": "dididi", "time": "2017-01-01", "messagecat": "\u4e0a\u67b6\u7684\u4f18\u60e0\u5238\u5373\u5c06\u8fc7\u671f",
    "hasread": 0, "couponid": "002"}],
    "couponResult": [{"product": "\u542e\u6307\u539f\u5473\u9e21wh"}, {"product": "\u829d\u58eb\u85af\u6761wh"}]}
     */
    private void parseMessageJSON(String msg) {
        System.out.println("message = " + msg);
        //TODO:parse JSON string
        messageList = new ArrayList<>();
        try {
            JSONObject mainObj = new JSONObject(msg);
            JSONArray jsonArray = mainObj.getJSONArray("messageResult");
            JSONArray couponArray = mainObj.getJSONArray("couponResult");
            System.out.println(jsonArray.length() + ",,,," + couponArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Message message = Message.decodeFromJSON(this, obj);
                JSONObject couponObj = couponArray.getJSONObject(i);
                String couponName = couponObj.getString("product");
                message.setCouponName(couponName);
                String listprice = couponObj.getString("listprice");
                String img_url = couponObj.getString("pic");
                message.setCouponPrice(listprice);
                message.setCouponURL(img_url);
                messageList.add(message);
            }
            MyApp app = (MyApp) getApplicationContext();
            app.setMessageList(messageList);
            sendNotification("您有新的消息");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra("content");
            parseMessageJSON(json);
        }

        public AlarmReceiver() {

        }

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void setDialog() {
        final Dialog mCameraDialog = new Dialog(this, R.style.BottomDialog);
        RelativeLayout root = (RelativeLayout) LayoutInflater.from(this).inflate(
                R.layout.bottom_sheet, null);

        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        root.measure(0, 0);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        lp.width = displayMetrics.widthPixels;
        lp.height = displayMetrics.heightPixels;

        lp.alpha = 1f; // 透明度

        //设置监听器
        ImageView closeButton = (ImageView) mCameraDialog.findViewById(R.id.close_btn_bottom_sheet);
        ImageView QRScanButton = (ImageView) mCameraDialog.findViewById(R.id.sell_btn_scanqr),
                FillFormButton = (ImageView) mCameraDialog.findViewById(R.id.sell_btn_fillform);
        QRScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainPageActivity.this, QRcodeActivity.class));
                mCameraDialog.dismiss();
            }
        });
        FillFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainPageActivity.this, FillFormActivity.class));
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraDialog.dismiss();
            }
        });
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }


    //发送通知
    public void sendNotification(String str) {
        Intent i = new Intent(this, MainPageActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("Ticker")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("U惠")
                .setContentText(str)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(0, notification);
    }
}
