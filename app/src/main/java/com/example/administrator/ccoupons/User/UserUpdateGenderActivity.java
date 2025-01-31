package com.example.administrator.ccoupons.User;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.ccoupons.Connections.ConnectionManager;
import com.example.administrator.ccoupons.Data.GlobalConfig;
import com.example.administrator.ccoupons.Gender;
import com.example.administrator.ccoupons.MyApp;
import com.example.administrator.ccoupons.R;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserUpdateGenderActivity extends AppCompatActivity {


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private int gender;
    private MyApp app;
    private final static String updateUserInformationURL = GlobalConfig.base_URL + GlobalConfig.updateUserInformation_URL;
    private final String[] genderStr = {"男", "女"};

    @BindView(R.id.user_update_gender_toolbar)
    Toolbar toolbar;
    @BindView(R.id.user_update_gender)
    TextView updateGender;
    @BindView(R.id.user_update_gender_radio)
    RadioGroup genderRadio;

    @OnClick(R.id.user_update_gender)
    public void onclick(View view) {
        if (gender == Gender.MALE) {
            update(genderStr[0]);
        } else update(genderStr[1]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update_gender);
        ButterKnife.bind(this);
        initToolbar();
        initData();
        initRadio();
    }


    /**
     * init toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    /**
     * init information data
     */
    private void initData() {
        app = (MyApp) getApplicationContext();
        if (app.getGender() == Gender.MALE) {
            genderRadio.check(R.id.radio_button_male);
        } else genderRadio.check(R.id.radio_button_female);
    }


    /**
     * init the gender choose radio
     */
    private void initRadio() {
        genderRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                int buttonId = radioGroup.getCheckedRadioButtonId();
                if (buttonId == R.id.radio_button_male)
                    gender = Gender.MALE;
                else gender = Gender.FEMALE;
            }
        });
    }


    /**
     * update saved data
     * @param gender
     */
    private void update(String gender) {
        String url_str = updateUserInformationURL;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userID", app.getUserId());
        map.put("gender", gender);

        ZLoadingDialog dialog = new ZLoadingDialog(UserUpdateGenderActivity.this);
        dialog.setLoadingBuilder(Z_TYPE.DOUBLE_CIRCLE)
                .setLoadingColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setCanceledOnTouchOutside(false)
                .setHintText("正在修改")
                .show();
        ConnectionManager connectionManager = new ConnectionManager(url_str, map, dialog);
        connectionManager.setConnectionListener(new ConnectionManager.UHuiConnectionListener() {
            @Override
            public void onConnectionSuccess(String response) {
                System.out.println("Result: " + response.toString());
                parseMessage(response);
            }

            @Override
            public void onConnectionTimeOut() {
                Toast.makeText(getApplicationContext(), "连接服务器超时，请稍后再试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectionFailed() {
                Toast.makeText(getApplicationContext(), "连接服务器遇到问题，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        });
        connectionManager.connect();
    }


    /**
     * parse the message data
     * @param response
     */
    private void parseMessage(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("result")) {
                String result = jsonObject.getString("result");
                if (result.equals("200")) {
                    app.setGender(gender);
                    Toast.makeText(UserUpdateGenderActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            if (jsonObject.has("error")) {
                Toast.makeText(UserUpdateGenderActivity.this, "好像出了点问题哟", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
