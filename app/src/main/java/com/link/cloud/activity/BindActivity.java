package com.link.cloud.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.link.cloud.CabinetApplication;
import com.link.cloud.R;
import com.link.cloud.bean.AllUser;
import com.link.cloud.veune.RxTimerUtil;
import com.link.cloud.veune.Venueutils;

import java.text.SimpleDateFormat;

/**
 * Created by 49488 on 2019/1/14.
 */

public class BindActivity extends Activity implements View.OnClickListener, Venueutils.VenueCallBack {
    StringBuilder phoneNumber;
    EditText containerNo;
    boolean isPass = false;
    Button bindKeypad1, bindKeypad2, bindKeypad3, bindKeypad4, bindKeypad5, bindKeypad6, bindKeypad7,
            bindKeypad8, bindKeypad9, bindKeypad0, cleanButton, deleteButton, backButton, sureButton;
    TextView finsh, input_tel, input_password, input_vuene, bind_success, bind_info, bind_success_infp;
    private String phoneNumber1, pass;
    LinearLayout ll_one, ll_two, ll_three;
    private RxTimerUtil timerUtil;
    ImageView bind_iamge;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_activity);
        phoneNumber = new StringBuilder();
        containerNo = (EditText) findViewById(R.id.phone_number);
        bindKeypad1 = (Button) findViewById(R.id.bind_keypad_1);
        finsh = (TextView) findViewById(R.id.finsh);
        bindKeypad2 = (Button) findViewById(R.id.bind_keypad_2);
        bindKeypad3 = (Button) findViewById(R.id.bind_keypad_3);
        bindKeypad4 = (Button) findViewById(R.id.bind_keypad_4);
        bindKeypad5 = (Button) findViewById(R.id.bind_keypad_5);
        bindKeypad6 = (Button) findViewById(R.id.bind_keypad_6);
        bindKeypad7 = (Button) findViewById(R.id.bind_keypad_7);
        bindKeypad8 = (Button) findViewById(R.id.bind_keypad_8);
        bindKeypad9 = (Button) findViewById(R.id.bind_keypad_9);
        cleanButton = (Button) findViewById(R.id.clean_button);
        bindKeypad0 = (Button) findViewById(R.id.bind_keypad_0);
        deleteButton = (Button) findViewById(R.id.delete_button);
        backButton = (Button) findViewById(R.id.back_button);
        sureButton = (Button) findViewById(R.id.sure_button);
        input_tel = (TextView) findViewById(R.id.input_tel);
        bind_info = (TextView) findViewById(R.id.bind_info);
        bind_iamge = (ImageView) findViewById(R.id.bind_iamge);
        bind_success_infp = (TextView) findViewById(R.id.bind_success_infp);
        ll_one = findViewById(R.id.ll_one);
        ll_two = findViewById(R.id.ll_two);
        ll_three = findViewById(R.id.ll_three);
        input_password = (TextView) findViewById(R.id.input_password);
        input_vuene = (TextView) findViewById(R.id.input_vuene);
        bind_success = (TextView) findViewById(R.id.bind_success);
        bindKeypad1.setOnClickListener(this);
        bindKeypad2.setOnClickListener(this);
        bindKeypad3.setOnClickListener(this);
        bindKeypad4.setOnClickListener(this);
        bindKeypad5.setOnClickListener(this);
        bindKeypad6.setOnClickListener(this);
        bindKeypad7.setOnClickListener(this);
        bindKeypad9.setOnClickListener(this);
        bindKeypad8.setOnClickListener(this);
        bindKeypad0.setOnClickListener(this);
        cleanButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        sureButton.setOnClickListener(this);
        bind_info.setOnClickListener(this);
        bind_success_infp.setOnClickListener(this);
        finsh.setOnClickListener(this);
        containerNo.setShowSoftInputOnFocus(false);
        isPass = false;
        CabinetApplication.getVenueUtils().initVenue(this, this, false);
        timerUtil = new RxTimerUtil();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finsh:
            case R.id.back_button:
                finish();
                break;
            case R.id.bind_keypad_0:
            case R.id.bind_keypad_1:
            case R.id.bind_keypad_2:
            case R.id.bind_keypad_3:
            case R.id.bind_keypad_4:
            case R.id.bind_keypad_5:
            case R.id.bind_keypad_6:
            case R.id.bind_keypad_7:
            case R.id.bind_keypad_8:
            case R.id.bind_keypad_9:
                if (phoneNumber.length() > 10) {
                    return;
                }
                phoneNumber.append(((TextView) v).getText());
                containerNo.setText(phoneNumber.toString());
                containerNo.setSelection(phoneNumber.length());
                break;
            case R.id.delete_button:

                if (phoneNumber.length() >= 1) {
                    phoneNumber.deleteCharAt(phoneNumber.length() - 1);
                } else {
                    return;
                }
                containerNo.setText(phoneNumber.toString());
                containerNo.setSelection(phoneNumber.length());

                break;
            case R.id.clean_button:

                phoneNumber.delete(0, phoneNumber.length());


                containerNo.setText(phoneNumber.toString());

                containerNo.setSelection(phoneNumber.length());
                break;

            case R.id.sure_button:

                if (isPass) {
                    pass = containerNo.getText().toString();
                    input_password.setTextColor(getResources().getColor(R.color.white));
                    input_password.setBackground(null);
                    input_vuene.setTextColor(getResources().getColor(R.color.main_color));
                    input_vuene.setBackgroundResource(R.drawable.bg_white_rectangle_corner_r20);
                    ll_one.setVisibility(View.GONE);
                    ll_two.setVisibility(View.VISIBLE);
                    finger();
                } else {
                    phoneNumber1 = containerNo.getText().toString();

                    if (!TextUtils.isEmpty(phoneNumber1) && phoneNumber1.length() == 11) {
                        phoneNumber.delete(0, phoneNumber.length());
                        containerNo.setText("");
                        containerNo.setHint(getString(R.string.inpu_num_pass));
                        isPass = true;
                        input_tel.setBackground(null);
                        input_tel.setTextColor(getResources().getColor(R.color.white));
                        input_password.setTextColor(getResources().getColor(R.color.main_color));
                        input_password.setBackgroundResource(R.drawable.bg_white_rectangle_corner_r20);
                    }
                }
                Log.e("onClick: ", phoneNumber1 + ">>>>" + pass);
                break;
            case R.id.bind_success_infp:
                finish();
                break;

        }
    }

    private void finger() {
        timerUtil.interval(1000, new RxTimerUtil.IRxNext() {
            @Override
            public void doNext(long number) {
                int state = CabinetApplication.getVenueUtils().getState();
                Log.e("getState: ", state + "");
                if (state == 3) {
                    CabinetApplication.getVenueUtils().workModel();
                }

            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerUtil.cancel();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void modelMsg(int state, String msg, Bitmap bitmap) {
        if (state == 3) {
            if (bitmap != null) {
                bind_iamge.setImageBitmap(bitmap);
            }
            bind_info.setText(getResources().getString(R.string.bind_success));
            long createTime = System.currentTimeMillis() + 8 * 60 * 60 * 1000;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long time1 = new Long(createTime);
            String d = format.format(time1);
            AllUser allUser = new AllUser();
            allUser.setIsadmin(0);
            allUser.setFingerprint(msg);
            allUser.setPassword(pass);
            allUser.setCreateTime(d);
            allUser.setPhone(phoneNumber1);
            ll_two.setVisibility(View.GONE);
            ll_three.setVisibility(View.VISIBLE);
            input_vuene.setTextColor(getResources().getColor(R.color.white));
            input_vuene.setBackground(null);
            bind_success.setTextColor(getResources().getColor(R.color.main_color));
            bind_success.setBackgroundResource(R.drawable.bg_white_rectangle_corner_r20);
            handler.sendEmptyMessageDelayed(5, 3000);
        }

        if (state == 2) {
            if (bitmap != null) {
                bind_iamge.setImageBitmap(bitmap);
            }
            bind_info.setText(getResources().getString(R.string.same_finger));
        }
        if (state == 1) {
            if (bitmap != null) {
                bind_iamge.setImageBitmap(bitmap);
            }
            bind_info.setText(getResources().getString(R.string.again_finger));
        }
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            finish();
        }
    };
}
