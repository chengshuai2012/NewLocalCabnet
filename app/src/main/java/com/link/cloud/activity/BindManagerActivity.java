package com.link.cloud.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import android.widget.Toast;

import com.link.cloud.CabinetApplication;
import com.link.cloud.R;
import com.link.cloud.bean.AllUser;
import com.link.cloud.veune.MdDevice;
import com.link.cloud.veune.MdUsbService;
import com.link.cloud.veune.RxTimerUtil;
import com.link.cloud.veune.Venueutils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import md.com.sdk.MicroFingerVein;

/**
 * Created by 49488 on 2019/1/14.
 */

public class BindManagerActivity extends Activity implements View.OnClickListener, Venueutils.VenueCallBack {
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
    private Realm realm;
    Venueutils venueutils;
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
        realm = Realm.getDefaultInstance();
        timerUtil = new RxTimerUtil();
        Intent intent = new Intent(this, MdUsbService.class);
        bindService(intent, mdSrvConn, Service.BIND_AUTO_CREATE);
        venueutils = CabinetApplication.getVenueUtils();
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
                    }else {
                        Toast.makeText(this,"清输入正确的手机号",Toast.LENGTH_LONG).show();
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
                int state = venueutils.getState();
                Log.e("getState: ", state + "");
                if (state == 3) {
                    venueutils.workModel();
                }else {
                    if(state!=4){
                        bind_info.setText(getResources().getString(R.string.right_finger));

                    }
                }

            }

        });
    }
    public void initVenue() {
        venueutils.initVenue(this, mdDeviceBinder, false,this);
    }
    private List<MdDevice> mdDevicesList = new ArrayList<MdDevice>();
    public static MdDevice mdDevice;
    private final int MSG_REFRESH_LIST = 0;
    private Handler listManageH = new Handler(new Handler.Callback() {

        @Override

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_LIST: {
                    mdDevicesList.clear();
                    mdDevicesList = getDevList();
                    if (mdDevicesList.size() > 0) {
                        mdDevice = mdDevicesList.get(0);
                    } else {
                        listManageH.sendEmptyMessageDelayed(MSG_REFRESH_LIST, 1500L);

                    }
                    break;
                }

            }
            return false;

        }

    });
    String Tag = "Venueutils";
    public MdUsbService.MyBinder mdDeviceBinder;
    private List<MdDevice> getDevList() {
        List<MdDevice> mdDevList = new ArrayList<MdDevice>();
        if (mdDeviceBinder != null) {
            int deviceCount = MicroFingerVein.fvdev_get_count();
            for (int i = 0; i < deviceCount; i++) {
                MdDevice mdDevice = new MdDevice();
                mdDevice.setNo(i);
                mdDevice.setIndex(mdDeviceBinder.getDeviceNo(i));
                mdDevList.add(mdDevice);
                initVenue();
            }
        } else {
            Log.e(Tag, "microFingerVein not initialized by MdUsbService yet,wait a moment...");
        }
        return mdDevList;

    }

    private ServiceConnection mdSrvConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mdDeviceBinder = (MdUsbService.MyBinder) service;
            if (mdDeviceBinder != null) {
                mdDeviceBinder.setOnUsbMsgCallback(mdUsbMsgCallback);
                listManageH.sendEmptyMessage(MSG_REFRESH_LIST);
                Log.e(Tag, "bind MdUsbService success.");
            } else {
                Log.e(Tag, "bind MdUsbService failed.");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(Tag, "disconnect MdUsbService.");
        }
    };

    private MdUsbService.UsbMsgCallback mdUsbMsgCallback = new MdUsbService.UsbMsgCallback() {
        @Override
        public void onUsbConnSuccess(String usbManufacturerName, String usbDeviceName) {
            String newUsbInfo = "USB厂商：" + usbManufacturerName + "  \nUSB节点：" + usbDeviceName;
            Log.e(Tag, newUsbInfo);
        }

        @Override
        public void onUsbDisconnect() {
            Log.e(Tag, "USB连接已断开");
        }

    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerUtil.cancel();
        realm.close();
        unbindService(mdSrvConn);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void modelMsg(int state, String msg, Bitmap bitmap) {
        if (state == 3) {
//            if (bitmap != null) {
//                bind_iamge.setImageBitmap(bitmap);
//            }
            bind_info.setText(getResources().getString(R.string.bind_success));
            long createTime = System.currentTimeMillis() + 8 * 60 * 60 * 1000;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long time1 = new Long(createTime);
            String d = format.format(time1);
            final AllUser allUser = new AllUser();
            allUser.setIsadmin(1);
            allUser.setFingerprint(msg);
            allUser.setPassword(pass);
            allUser.setCreateTime(d);
            allUser.setPhone(phoneNumber1);
          realm.executeTransaction(new Realm.Transaction() {
              @Override
              public void execute(Realm realm) {
                  realm.copyToRealm(allUser);
              }
          });
            ll_two.setVisibility(View.GONE);
            ll_three.setVisibility(View.VISIBLE);
            input_vuene.setTextColor(getResources().getColor(R.color.white));
            input_vuene.setBackground(null);
            bind_success.setTextColor(getResources().getColor(R.color.main_color));
            bind_success.setBackgroundResource(R.drawable.bg_white_rectangle_corner_r20);
            handler.sendEmptyMessageDelayed(5, 3000);
        }

        if (state == 2) {
//            if (bitmap != null) {
//                bind_iamge.setImageBitmap(bitmap);
//            }
            bind_info.setText(getResources().getString(R.string.same_finger));
        }
        if (state == 1) {
//            if (bitmap != null) {
//                bind_iamge.setImageBitmap(bitmap);
//            }
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
