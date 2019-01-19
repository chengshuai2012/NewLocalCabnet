package com.link.cloud.activity;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.link.cloud.CabinetApplication;
import com.link.cloud.R;
import com.link.cloud.bean.AllUser;
import com.link.cloud.bean.CabinetNumber;
import com.link.cloud.bean.CabinetRecord;
import com.link.cloud.listener.DialogCancelListener;
import com.link.cloud.veune.DialogUtils;
import com.link.cloud.veune.MdDevice;
import com.link.cloud.veune.MdUsbService;
import com.link.cloud.veune.OpenDoorUtil;
import com.link.cloud.veune.RxTimerUtil;
import com.link.cloud.veune.Venueutils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android_serialport_api.SerialPort;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import md.com.sdk.MicroFingerVein;

import static android.support.constraint.Constraints.TAG;

public class MainActivity extends Activity implements View.OnClickListener, DialogCancelListener, Venueutils.VenueCallBack {

    private RxTimerUtil rxTimerUtil;
    String phone;
    private RealmResults<AllUser> managersRealm;
    List<AllUser> managers = new ArrayList<>();
    private Realm realm;
    private DialogUtils dialogUtils;
    boolean isOpen;
    private Venueutils venueUtils;
    private RealmResults<AllUser> users;
    List<AllUser> peoples = new ArrayList<>();
    SharedPreferences userInfo;
    OpenDoorUtil openDoorUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView bind = findViewById(R.id.bind);
        LinearLayout return_lock = findViewById(R.id.return_lock);
        LinearLayout open_lcok = findViewById(R.id.open_lock);
        TextView manager = findViewById(R.id.manager);
        manager.setOnClickListener(this);
        bind.setOnClickListener(this);
        return_lock.setOnClickListener(this);
        open_lcok.setOnClickListener(this);
        rxTimerUtil = new RxTimerUtil();
        realm = Realm.getDefaultInstance();
        managersRealm = realm.where(AllUser.class).equalTo("isadmin", 1).findAll();
        managers.addAll(realm.copyFromRealm(managersRealm));
        managersRealm.addChangeListener(new RealmChangeListener<RealmResults<AllUser>>() {
            @Override
            public void onChange(RealmResults<AllUser> allUsers) {
                managers.clear();
                managers.addAll(realm.copyFromRealm(managersRealm));
            }
        });
        users = realm.where(AllUser.class).findAll();
        peoples.addAll(realm.copyFromRealm(users));
        users.addChangeListener(new RealmChangeListener<RealmResults<AllUser>>() {
            @Override
            public void onChange(RealmResults<AllUser> allUsers) {
                peoples.clear();
                peoples.addAll(realm.copyFromRealm(users));
            }
        });
        userInfo = getSharedPreferences("user_info", 0);
        String devicepwd = userInfo.getString("devicepwd", "");
        if (TextUtils.isEmpty(devicepwd)) {
            userInfo.edit().putString("devicepwd", "888888").commit();
        }
        dialogUtils = DialogUtils.getDialogUtils(this);
        dialogUtils.setDialogCanceListener(this);
        Intent intent = new Intent(this, MdUsbService.class);
        bindService(intent, mdSrvConn, Service.BIND_AUTO_CREATE);
        venueUtils = CabinetApplication.getVenueUtils();
        random = new Random();
        getBox();
        openDoorUtil = new OpenDoorUtil();
        serialpprt_wk1 = ((CabinetApplication) getApplicationContext()).serialPortOne;
        serialpprt_wk2 = ((CabinetApplication) getApplicationContext()).serialPortTwo;
        serialpprt_wk3 = ((CabinetApplication) getApplicationContext()).serialPortThree;

    }

    private Random random;

    public void initVenue() {
        venueUtils.initVenue(this, mdDeviceBinder, false, this);
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
        rxTimerUtil.cancel();
        realm.close();
        unbindService(mdSrvConn);
    }

    List<CabinetNumber> boxs = new ArrayList<>();
    private RealmResults<CabinetNumber> allBox;

    private void getBox() {
        allBox = realm.where(CabinetNumber.class).equalTo("isUser", "可用").findAll();
        this.allBox.addChangeListener(new RealmChangeListener<RealmResults<CabinetNumber>>() {
            @Override
            public void onChange(RealmResults<CabinetNumber> cabinetNumbers) {
                boxs.clear();
                boxs.addAll(cabinetNumbers);

            }
        });
        boxs.clear();
        boxs.addAll(realm.copyFromRealm(allBox));
    }

    int openType=2;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bind:
//                View okView = View.inflate(this,R.layout.verify_success,null);
//                dialogUtils.showPayOkDialog(okView,"aaaaaa");
                startActivity(new Intent(this, BindActivity.class));
                break;
            case R.id.return_lock:
                isOpen = false;
                openType = 3;
                View veune_return_dialog = View.inflate(this, R.layout.veune_open_dialog, null);
                dialogUtils.showOpenDialog(veune_return_dialog, isOpen);
                rxTimerUtil.interval(1000, new RxTimerUtil.IRxNext() {
                    @Override
                    public void doNext(long number) {

                        int state = venueUtils.getState();
                        Log.e(TAG, "doNext: " + state);
                        Log.e(TAG, "doNext: " + managers.size());
                        if (state == 3) {
                            phone = null;
                            phone = venueUtils.identifyNewImg(peoples);
                            if (phone != null) {
                                dialogUtils.dissMiss();
                                RealmResults<CabinetNumber> CabinetNumbers = realm.where(CabinetNumber.class).equalTo("phone", phone).findAll();
                                if (CabinetNumbers.size() > 0) {
                                    openLockCabinet(CabinetNumbers.get(0).getCabinetNumber(),phone);
                                } else {
                                    Toast.makeText(MainActivity.this, getString(R.string.no_cabinet_found), Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(MainActivity.this, getString(R.string.cheack_fail), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                        }
                    }
                });
                isOpen = false;
                break;
            case R.id.open_lock:
                isOpen = true;
                View veune_open_dialog = View.inflate(this, R.layout.veune_open_dialog, null);
                dialogUtils.showOpenDialog(veune_open_dialog, isOpen);
                rxTimerUtil.interval(1000, new RxTimerUtil.IRxNext() {
                    @Override
                    public void doNext(long number) {

                        int state = venueUtils.getState();
                        Log.e(TAG, "doNext: " + state);
                        Log.e(TAG, "doNext: " + peoples.size());
                        if (state == 3) {
                            phone = null;
                            phone = venueUtils.identifyNewImg(peoples);

                            if (phone != null) {
                                dialogUtils.dissMiss();
                                if (boxs.size() > 0) {
                                    int i = random.nextInt(boxs.size());
                                    RealmResults<CabinetNumber> CabinetNumbers = realm.where(CabinetNumber.class).equalTo("phone", phone).findAll();
                                    if (CabinetNumbers.size() > 0) {
                                        openLockCabinet(CabinetNumbers.get(0).getCabinetNumber(), phone);
                                        openType = 2;
                                    } else {
                                        final AllUser phones = realm.where(AllUser.class).equalTo("phone", MainActivity.this.phone).findFirst();
                                        String cabinetNumber = boxs.get(i).getCabinetNumber();
                                        final CabinetNumber cabinetNumber1 = realm.where(CabinetNumber.class).equalTo("cabinetNumber", cabinetNumber).findFirst();
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                cabinetNumber1.setPhone(phones.getPhone());
                                                cabinetNumber1.setPassWord(phones.getPassword());
                                            }
                                        });
                                        openType = 1;
                                        openLockCabinet(cabinetNumber, phone);
                                    }

                                } else {
                                    Toast.makeText(MainActivity.this, getString(R.string.on_cabinet), Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                Toast.makeText(MainActivity.this, getString(R.string.cheack_fail), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                        }
                    }
                });


                break;

            case R.id.manager:
                rxTimerUtil.interval(1000, new RxTimerUtil.IRxNext() {
                    @Override
                    public void doNext(long number) {

                        int state = venueUtils.getState();
                        Log.e(TAG, "doNext: " + state);
                        Log.e(TAG, "doNext: " + managers.size());
                        if (state == 3) {
                            phone = null;
                            phone = venueUtils.identifyNewImg(managers);
                            if (phone != null) {
                                dialogUtils.dissMiss();
                                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                            } else {
                                Toast.makeText(MainActivity.this, getString(R.string.no_manager), Toast.LENGTH_SHORT).show();

                            }
                        } else {

                        }
                    }
                });
                View manager = View.inflate(this, R.layout.veune_dialog, null);
                dialogUtils.showManagerDialog(manager);
                break;
        }
    }

    private void openLockCabinet(String cabinetNumber,String phone) {
        final CabinetNumber openCabinet = realm.where(CabinetNumber.class).equalTo("cabinetNumber", cabinetNumber).findFirst();
        boolean b = openLock(cabinetNumber, openCabinet);
        if (b) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (openType == 3) {
                        openCabinet.setIsUser("可用");
                        openCabinet.setPhone("");
                        openCabinet.setPassWord("");

                    } else {
                        openCabinet.setIsUser("使用中");
                    }

                }
            });
            long createTime = System.currentTimeMillis() + 8 * 60 * 60 * 1000;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long time1 = new Long(createTime);
            String d = format.format(time1);
            final CabinetRecord cabinetRecord = new CabinetRecord();
            cabinetRecord.setCabinetNumber(cabinetNumber);
            if (openType == 1) {
                cabinetRecord.setCabinetStating("存件");
            } else if (openType == 2) {
                cabinetRecord.setCabinetStating("续存");
            } else if (openType == 3) {
                cabinetRecord.setCabinetStating("退柜");
            }

            cabinetRecord.setMemberName("会员");
            cabinetRecord.setOpentime(d);
            cabinetRecord.setPhoneNum(phone);
            cabinetRecord.setCreatTime(createTime);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealm(cabinetRecord);
                }
            });
        }

    }

    public SerialPort serialpprt_wk1 = null;
    public SerialPort serialpprt_wk2 = null;
    public SerialPort serialpprt_wk3 = null;

    private boolean openLock(String cabinetNumber, CabinetNumber openCabinet) {
        boolean isOpenSuccess = false;
        String circuitNumber = openCabinet.getCircuitNumber();
        int nuberlock = Integer.parseInt(circuitNumber);
        if (nuberlock > 10) {
            nuberlock = nuberlock % 10;
            if (nuberlock == 0) {
                nuberlock = 10;
            }
        }
        try {
            if (Integer.parseInt(openCabinet.getCabinetLockPlate()) <= 10) {
                serialpprt_wk1.getOutputStream().write(openDoorUtil.openOneDoor(Integer.parseInt(openCabinet.getCabinetLockPlate()), nuberlock));
            } else if (Integer.parseInt(openCabinet.getCabinetLockPlate()) > 10 && Integer.parseInt(openCabinet.getCabinetLockPlate()) < 20) {
                serialpprt_wk2.getOutputStream().write(openDoorUtil.openOneDoor(Integer.parseInt(openCabinet.getCabinetLockPlate()) % 10, nuberlock));
            } else if (Integer.parseInt(openCabinet.getCabinetLockPlate()) > 20 && Integer.parseInt(openCabinet.getCabinetLockPlate()) < 30) {
                serialpprt_wk3.getOutputStream().write(openDoorUtil.openOneDoor(Integer.parseInt(openCabinet.getCabinetLockPlate()) % 10, nuberlock));
            } else if (Integer.parseInt(openCabinet.getCabinetLockPlate()) == 20) {
                serialpprt_wk2.getOutputStream().write(openDoorUtil.openOneDoor(10, nuberlock));
            } else if (Integer.parseInt(openCabinet.getCabinetLockPlate()) == 30) {
                serialpprt_wk3.getOutputStream().write(openDoorUtil.openOneDoor(10, nuberlock));
            }
            View okView = View.inflate(this, R.layout.verify_success, null);
            dialogUtils.showPayOkDialog(okView, cabinetNumber + "号柜门已打开");
            rxTimerUtil.timer(3000, new RxTimerUtil.IRxNext() {
                @Override
                public void doNext(long number) {
                    dialogUtils.dissMiss();
                }
            });
            isOpenSuccess = true;
        } catch (Exception e) {

        } finally {

        }
        return isOpenSuccess;
    }

    public void openLock(String cabinet, String password) {
        Log.e(TAG, "openLock: "+cabinet+password );
        if(isOpen){
            openType=2;
        }else {
            openType=3;
        }
        CabinetNumber cabinetNumber = realm.where(CabinetNumber.class).equalTo("cabinetNumber", cabinet).findFirst();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(cabinet)) {
            Toast.makeText(this, "请输入柜号", Toast.LENGTH_SHORT).show();
            return;
        }
        if(cabinetNumber==null){
            Toast.makeText(this, "没有找到您租用的柜子", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(cabinetNumber.getPassWord())||"可用".equals(cabinetNumber.getIsUser())){
            Toast.makeText(this, "柜号或密码不正确", Toast.LENGTH_SHORT).show();
        }else {
            openLockCabinet(cabinet,cabinetNumber.getPhone());
        }

    }

    public void gotoSetting(String password) {

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }


        String repwd = userInfo.getString("devicepwd", "0");

        if (!password.equals(repwd)) {
            Toast.makeText(this, "密码不正确", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void dialogCancel() {
        rxTimerUtil.cancel();
        Log.e("dialogCancel: ", "cancel");
    }

    @Override
    public void onVenuePay() {

    }

    @Override
    public void modelMsg(int state, String msg, Bitmap bitmap) {

    }
}
